package com.openlab.catalog_service.service;

import com.openlab.catalog_service.model.dto.CarDTO;

import java.util.List;

public interface CarService {
    public CarDTO createCar(CarDTO dto);
    public CarDTO getCarById(String id);
    public List<CarDTO> getAllCars();
    public CarDTO updateCar(String id, CarDTO dto);
    public void deleteCarById(String id);

}
