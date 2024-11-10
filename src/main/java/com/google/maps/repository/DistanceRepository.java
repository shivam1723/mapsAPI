package com.google.maps.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.google.maps.entity.DistanceEntity;

import java.util.Optional;

public interface DistanceRepository extends JpaRepository<DistanceEntity, Long> {
    Optional<DistanceEntity> findByFromPincodeAndToPincode(String fromPincode, String toPincode);
}

