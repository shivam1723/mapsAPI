package com.google.maps;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

class DistanceServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private DistanceServiceTest distanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDistanceAndDuration_Success() {
        // Arrange
        String origin = "New York, NY";
        String destination = "Los Angeles, CA";
        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin +
                        "&destinations=" + destination + "&key=YOUR_API_KEY";
        
        // Mocking the API response
        String mockResponse = "{\"rows\": [{\"elements\": [{\"distance\": {\"text\": \"2,800 miles\"}," +
                              "\"duration\": {\"text\": \"40 hours\"}}]}]}";
        ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.getForEntity(apiUrl, String.class)).thenReturn(responseEntity);

        // Act
        DistanceResult result = distanceService.getDistanceAndDuration(origin, destination);

        // Assert
        assertNotNull(result);
        assertEquals("2,800 miles", result.getDistance());
        assertEquals("40 hours", result.getDuration());
    }

    @Test
    void testGetDistanceAndDuration_InternalServerError() {
        // Arrange
        String origin = "New York, NY";
        String destination = "Los Angeles, CA";
        String apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin +
                        "&destinations=" + destination + "&key=YOUR_API_KEY";

        // Simulate HTTP 500 error
        when(restTemplate.getForEntity(apiUrl, String.class))
                .thenThrow(new RestClientException("500 Internal Server Error"));

        // Act & Assert
        Exception exception = assertThrows(RestClientException.class, () -> {
            distanceService.getDistanceAndDuration(origin, destination);
        });
        assertEquals("500 Internal Server Error", exception.getMessage());
    }

    @Test
    void testGetDistanceAndDuration_NullOrigin() {
        // Arrange
        String origin = null;
        String destination = "Los Angeles, CA";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            distanceService.getDistanceAndDuration(origin, destination);
        });
        assertEquals("Origin cannot be null or empty", exception.getMessage());
    }

    @Test
    void testGetDistanceAndDuration_EmptyDestination() {
        // Arrange
        String origin = "New York, NY";
        String destination = "";

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            distanceService.getDistanceAndDuration(origin, destination);
        });
        assertEquals("Destination cannot be null or empty", exception.getMessage());
    }
}


