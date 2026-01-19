package com.stackprotocol.capsule_temporelle.service;

import com.stackprotocol.capsule_temporelle.dto.TimeCapsulePost;
import com.stackprotocol.capsule_temporelle.dto.TimeCapsuleResume;
import com.stackprotocol.capsule_temporelle.exception.CapsuleLaunchDateException;
import com.stackprotocol.capsule_temporelle.exception.CapsuleNotFoundException;
import com.stackprotocol.capsule_temporelle.exception.CapsuleNotLaunchedException;
import com.stackprotocol.capsule_temporelle.model.TimeCapsule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

@Service
public class TimeCapsuleService {
    private static final Logger LOGGER = Logger.getLogger(TimeCapsuleService.class.getName());

    private final ObjectMapper objectMapper;
    private final Path filePath;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public TimeCapsuleService(@Value("${capsule.file.path}") String filePath, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // Résoudre le chemin absolu
        Path path = Paths.get(filePath);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.dir"), filePath);
        }
        this.filePath = path;
        LOGGER.info("Chemin du fichier de capsules: " + this.filePath.toAbsolutePath());
        // Créer les dossier/fichier s'ils n'existent pas
        initFile();
    }

    /**
     * Saves a new TimeCapsule constructed from the provided TimeCapsulePost object.
     * The new capsule is added to an existing list of capsules,
     * which is then written to a persistent store.
     *
     * @param capsule the TimeCapsulePost object containing the message and launch date information
     *                required to create a new TimeCapsule.
     * @return the newly created TimeCapsule instance containing the saved information.
     * @throws RuntimeException if an error occurs during the save operation.
     */
    public TimeCapsule save(TimeCapsulePost capsule) {
        lock.writeLock().lock();
        try {
            List<TimeCapsule> capsules = readCapsules();
            TimeCapsule newCapsule = new TimeCapsule(capsule.getMessage(), capsule.getLaunchDate());
            capsules.add(newCapsule);

            writeCapsules(capsules);

            return newCapsule;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'écriture du fichier JSON: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Retrieves a launched TimeCapsule identified by the specified UUID.
     *
     * This method checks whether the capsule exists, verifies if it has a valid
     * launch date, and confirms that the current date is on or after the launch date.
     * If any of these conditions are not met, appropriate exceptions are thrown.
     *
     * @param uuid the unique identifier of the TimeCapsule to be retrieved
     * @return the TimeCapsule instance that matches the given UUID and has been launched
     * @throws CapsuleNotFoundException if no capsule with the specified UUID is found
     * @throws CapsuleLaunchDateException if the capsule does not have a defined launch date
     * @throws CapsuleNotLaunchedException if the capsule's launch date is in the future
     * @throws IOException if there is an error reading the capsule data from storage
     */
    public TimeCapsule getLaunchedCapsule(UUID uuid) throws CapsuleNotFoundException, CapsuleNotLaunchedException, IOException, CapsuleLaunchDateException {
        lock.readLock().lock();

        try {
            TimeCapsule capsule = readCapsules().stream().filter(
                    c -> c.getId().equals(uuid.toString())
            ).findFirst().orElse(null);

            if (capsule == null) {
                System.out.println(capsule);
                throw new CapsuleNotFoundException("La capsule n'existe pas.");
            }

            if (capsule.getLaunchDate() == null) {
                throw new CapsuleLaunchDateException("La date de la capsule n'a pas été renseigné.");
            }

            if (LocalDate.now().isBefore(capsule.getLaunchDate())) {
                throw new CapsuleNotLaunchedException("La capsule ne peut pas encore être ouverte !");
            }

            return capsule;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la lecture du fichier JSON: " + e.getMessage());
            throw e;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Retrieves a list of all available TimeCapsuleResume objects from the data source.
     *
     * @return a List of TimeCapsuleResume objects representing all stored time capsules.
     * @throws RuntimeException if an error occurs while reading the data source.
     */
    public List<TimeCapsuleResume> getAllCapsules() {
        lock.readLock().lock();
        try {
            List<TimeCapsuleResume> capsules = objectMapper.readValue(filePath.toFile(), new TypeReference<List<TimeCapsuleResume>>() {});
            capsules.sort(new Comparator<TimeCapsuleResume>() {
                @Override
                public int compare(TimeCapsuleResume capsule1, TimeCapsuleResume capsule2) {
                    return capsule1.getLaunchDate().compareTo(capsule2.getLaunchDate());
                }
            });
            return capsules;
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de la lecture du fichier JSON: " + e.getMessage());
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Writes a list of TimeCapsule objects to the data store in a formatted JSON structure.
     *
     * @param capsules the list of TimeCapsule objects to export to the persistent storage
     */
    public void writeCapsules(List<TimeCapsule> capsules) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(filePath.toFile(), capsules);
        } catch (Exception e) {
            LOGGER.severe("Erreur lors de l'écriture du fichier JSON: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Reads and deserializes a list of TimeCapsule objects from the JSON file located at the specified file path.
     *
     * @return a List of TimeCapsule objects retrieved from the data source.
     * @throws IOException if an error occurs while reading or parsing the JSON file.
     */
    private List<TimeCapsule> readCapsules() throws IOException {
        try {
            return objectMapper.readValue(filePath.toFile(), new TypeReference<List<TimeCapsule>>() {});
        } catch(Exception e) {
            LOGGER.severe("Erreur lors de la lecture du fichier JSON: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Initializes the file and its parent directories if they do not already exist.
     *
     * This method ensures the directory structure exists by creating it if necessary,
     * then checks for the file's existence. If the file does not exist, it is created
     * and initialized with an empty JSON array.
     *
     * In case of an error during directory or file creation, a runtime exception is thrown.
     *
     * @throws RuntimeException if an IOException occurs during file or directory creation
     *                          or initialization.
     */
    private void initFile() {
        try {
            // Créé le dossier si il n'existe pas
            if (filePath.getParent() != null) {
                Files.createDirectories(filePath.getParent());
            }

            // Créé le fichier si il n'existe pas
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                Files.write(filePath, "[]".getBytes());
            }

        } catch (IOException e) {
            LOGGER.severe("Erreur lors de l'initialisation du fichier: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
