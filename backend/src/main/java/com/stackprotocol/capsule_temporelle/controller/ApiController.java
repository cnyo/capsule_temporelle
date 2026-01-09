package com.stackprotocol.capsule_temporelle.controller;

import com.stackprotocol.capsule_temporelle.dto.TimeCapsulePost;
import com.stackprotocol.capsule_temporelle.exception.CapsuleLaunchDateException;
import com.stackprotocol.capsule_temporelle.exception.CapsuleNotFoundException;
import com.stackprotocol.capsule_temporelle.exception.CapsuleNotLaunchedException;
import com.stackprotocol.capsule_temporelle.model.TimeCapsule;
import com.stackprotocol.capsule_temporelle.service.TimeCapsuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ApiController {

    private final TimeCapsuleService timeCapsuleService;

    public ApiController(TimeCapsuleService timeCapsuleService) {
        this.timeCapsuleService = timeCapsuleService;
    }

    @PostMapping("/api/capsules")
    public ResponseEntity<Void> saveTimeCapsule(@RequestBody TimeCapsulePost capsule) {
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

    @GetMapping("/api/capsules/{id}")
    public ResponseEntity<Map<String, Object>> getTimeCapsule(@PathVariable UUID id) {
        try {
            TimeCapsule capsule = timeCapsuleService.getLaunchedCapsule(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", capsule);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (CapsuleNotFoundException e) {
            return buildError(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (CapsuleNotLaunchedException e) {
            return buildError(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            return buildError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @GetMapping("/api/capsule/all")
    public List<TimeCapsule> getAllLaunchedTimeCapsules() {
        return timeCapsuleService.getAllCapsules();
    }

    private ResponseEntity<Map<String, Object>> buildError(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(Map.of(
                        "success", false,
                        "message", message
                ));
    }
}
