package com.google.maps.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class PincodeEntity {

    @Id
    private String pincode;
    private double latitude;
    private double longitude;
    private String polygonData;
}
