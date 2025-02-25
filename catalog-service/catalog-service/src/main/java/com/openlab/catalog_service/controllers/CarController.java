package com.openlab.catalog_service.controllers;

import com.openlab.catalog_service.model.dto.CarDTO;
import com.openlab.catalog_service.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDTO createCar(@RequestBody CarDTO dto){
        return carService.createCar(dto);
    }


    @GetMapping("/{id}")
    public CarDTO getCarById(@PathVariable String id){
        return carService.getCarById(id);
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CarDTO> getAllCars(){
        return carService.getAllCars();
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CarDTO updateCar(@PathVariable String id, @RequestBody CarDTO dto){
      return  carService.updateCar(id, dto);
    }



    @DeleteMapping("/{id}")

    public ResponseEntity<String> deleteCar(@PathVariable String id){
        carService.deleteCarById(id);
        return ResponseEntity.ok(" Car with id: " +id + " is successful deleted");
    }
}
