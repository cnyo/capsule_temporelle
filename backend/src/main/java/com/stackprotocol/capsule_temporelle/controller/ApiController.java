package com.stackprotocol.capsule_temporelle.controller;

import com.stackprotocol.capsule_temporelle.dto.TimeCapsulePost;
import com.stackprotocol.capsule_temporelle.dto.TimeCapsuleResume;
import com.stackprotocol.capsule_temporelle.exception.CapsuleNotFoundException;
import com.stackprotocol.capsule_temporelle.exception.CapsuleNotLaunchedException;
import com.stackprotocol.capsule_temporelle.model.TimeCapsule;
import com.stackprotocol.capsule_temporelle.service.TimeCapsuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * This class serves as a REST controller for managing Time Capsules.
 * It exposes endpoints to create, retrieve, and list time capsules.
 *
 * The controller interacts with a {@link TimeCapsuleService} to handle the business logic associated with time capsules.
 * All endpoints return appropriate HTTP responses based on the success or failure of the operations.
 */
@RestController
public class ApiController {

    private final TimeCapsuleService timeCapsuleService;

    public ApiController(TimeCapsuleService timeCapsuleService) {
        this.timeCapsuleService = timeCapsuleService;
    }

    /**
     * Saves a new time capsule and returns an appropriate HTTP response.
     * The input time capsule details are provided in the request body and validated
     * before being saved. If successful, the location of the created resource is returned.
     *
     * @param capsule the details of the time capsule to be created, including message and launch date
     * @return ResponseEntity indicating the result of the operation:
     *         - HTTP 201 (Created) with the location of the created resource if successful
     *         - HTTP 400 (Bad Request) if validation fails or the operation cannot be completed
     */
    @PostMapping("/api/capsules")
    public ResponseEntity<Void> saveTimeCapsule(@Validated @RequestBody TimeCapsulePost capsule) {
        try {
            TimeCapsule capsuleCreated = timeCapsuleService.save(capsule);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(capsuleCreated.getId())
                    .toUri();

            System.out.println(location);
            return ResponseEntity.created(location).build();
        } catch (RuntimeException e) {
            System.out.println(capsule.toString());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves a list of all launched time capsules.
     *
     * @return a list of {@code TimeCapsuleResume} objects containing the summary of launched time capsules.
     */
    @GetMapping("/api/capsules")
    public List<TimeCapsuleResume> getAllLaunchedTimeCapsules() {
        return timeCapsuleService.getAllCapsules();
    }

    /**
     * Retrieves a launched time capsule based on its unique identifier.
     *
     * @param id the unique identifier of the time capsule to retrieve
     * @return ResponseEntity containing the retrieved {@code TimeCapsule} object if it is found and launched,
     *         or an appropriate HTTP status:
     *         - HTTP 404 (Not Found) if the capsule does not exist
     *         - HTTP 403 (Forbidden) if the capsule has not been launched
     *         - HTTP 500 (Internal Server Error) for unexpected server failures
     */
    @GetMapping("/api/capsules/{id}")
    public ResponseEntity<?> getTimeCapsule(@PathVariable UUID id) {
        try {
            TimeCapsule capsule = timeCapsuleService.getLaunchedCapsule(id);
            return ResponseEntity.ok(capsule);
        } catch (CapsuleNotFoundException e) {
            System.out.println(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        } catch (CapsuleNotLaunchedException e) {
            System.out.println(HttpStatus.FORBIDDEN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            System.out.println(HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
