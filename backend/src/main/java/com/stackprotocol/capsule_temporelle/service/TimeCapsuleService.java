package com.stackprotocol.capsule_temporelle.service;

import com.stackprotocol.capsule_temporelle.dto.TimeCapsulePost;
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
import java.util.List;
import java.util.UUID;
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
        this.filePath = Paths.get(filePath);
        this.objectMapper = objectMapper;
        // Créer les dossier/fichier s'ils n'existent pas
        initFile();
    }

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

    public TimeCapsule getLaunchedCapsule(UUID uuid) throws CapsuleNotFoundException, CapsuleNotLaunchedException {
        lock.readLock().lock();

        try {
            TimeCapsule capsule = readCapsules().stream().filter(
                    c -> c.getId().equals(uuid.toString())
            ).findFirst().orElse(null);

            if (capsule == null) {
                throw new CapsuleNotFoundException("La capsule n'existe pas.");
            }

            if (capsule.getLaunchDate() == null) {
                throw new CapsuleLaunchDateException("La date de la capsule n'a pas été renseigné.");
            }

            if (LocalDate.now().isBefore(capsule.getLaunchDate())) {
                throw new CapsuleNotLaunchedException("La capsule ne peut pas encore être ouverte !");
            }

            return capsule;
        } catch (IOException e) {
            LOGGER.severe("Erreur lors de la lecture du fichier JSON: " + e.getMessage());
            throw new RuntimeException("Erreur lors de la récupération de la capsule", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            lock.readLock().unlock();
        }
    }

    public List<TimeCapsule> getAllCapsules() {
        return objectMapper.readValue(filePath.toFile(), new TypeReference<List<TimeCapsule>>() {});
    }

    public void writeCapsules(List<TimeCapsule> capsules) {
        objectMapper.writerWithDefaultPrettyPrinter()
                .writeValue(filePath.toFile(), capsules);
    }

    private List<TimeCapsule> readCapsules() throws IOException {
        try {
            return objectMapper.readValue(filePath.toFile(), new TypeReference<List<TimeCapsule>>() {});
        } catch(Exception e) {
            LOGGER.severe("Erreur lors de la lecture du fichier JSON: " + e.getMessage());
            throw e;
        }
    }

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
