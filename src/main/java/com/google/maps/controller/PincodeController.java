package com.google.maps.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.google.maps.entity.DistanceEntity;
import com.google.maps.service.DistanceService;

@RestController
@RequestMapping("/api/v1/pincode")
public class PincodeController {

    @Autowired
    private DistanceService distanceService;

    @Value("${google.maps.api.key}")
    private String apiKey;

    @GetMapping("/distance")
    public ResponseEntity<DistanceEntity> getDistance(
            @RequestParam String fromPincode, 
            @RequestParam String toPincode) {
        try {
            DistanceEntity distanceEntity = distanceService.getDistance(fromPincode, toPincode, apiKey);
            return ResponseEntity.ok(distanceEntity);
        } catch (Exception e) {
        	System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
