package com.google.maps.service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.entity.DistanceEntity;
import com.google.maps.repository.DistanceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class DistanceService {

    @Autowired
    private DistanceRepository distanceRepository;

    private static final String GOOGLE_MAPS_API_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";

    @Cacheable(value = "distanceCache", key = "#fromPincode + '-' + #toPincode")
    public DistanceEntity getDistance(String fromPincode, String toPincode, String apiKey) throws Exception {
        Optional<DistanceEntity> cachedResult = distanceRepository.findByFromPincodeAndToPincode(fromPincode, toPincode);
        if (cachedResult.isPresent()) {
            return cachedResult.get();
        }

        // Fetch data from Google Maps API
        DistanceEntity distanceEntity = callGoogleMapsAPI(fromPincode, toPincode, apiKey);

        // Save to database
        return distanceRepository.save(distanceEntity);
    }

    private DistanceEntity callGoogleMapsAPI(String fromPincode, String toPincode, String apiKey) throws Exception {
        String url = GOOGLE_MAPS_API_URL + "&origins=" + fromPincode + "&destinations=" + toPincode + "&key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(url, String.class);
        
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(result);
        JsonNode rows = root.path("rows");

        if (rows.isArray() && rows.size() > 0) {
            JsonNode elements = rows.get(0).path("elements").get(0);
            double distance = elements.path("distance").path("value").asDouble() / 1000;  // Convert meters to kilometers
            String duration = elements.path("duration").path("text").asText();
            String route = fromPincode + " -> " + toPincode;

            DistanceEntity distanceEntity = new DistanceEntity();
            distanceEntity.setFromPincode(fromPincode);
            distanceEntity.setToPincode(toPincode);
            distanceEntity.setDistance(distance);
            distanceEntity.setDuration(duration);
            distanceEntity.setRoute(route);

            return distanceEntity;
        } else {
            throw new Exception("Unable to retrieve distance and duration");
        }
    }
}

