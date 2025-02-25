package com.openlab.catalog_service.repository;

import com.openlab.catalog_service.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, String> {
}
