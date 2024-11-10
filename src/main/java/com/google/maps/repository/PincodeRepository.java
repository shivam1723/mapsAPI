package com.google.maps.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.google.maps.entity.PincodeEntity;

public interface PincodeRepository extends JpaRepository<PincodeEntity, String> {
}

