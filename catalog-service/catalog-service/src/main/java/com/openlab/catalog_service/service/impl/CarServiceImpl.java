package com.openlab.catalog_service.service.impl;

import com.openlab.catalog_service.exception.CarNotFoundException;
import com.openlab.catalog_service.model.Car;
import com.openlab.catalog_service.model.dto.CarDTO;
import com.openlab.catalog_service.model.dto.CarEvent;
import com.openlab.catalog_service.repository.CarRepository;
import com.openlab.catalog_service.service.CarService;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final KafkaTemplate<String, CarEvent> kafkaTemplate;

    public CarServiceImpl(CarRepository carRepository, KafkaTemplate<String, CarEvent> kafkaTemplate) {
        this.carRepository = carRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public CarDTO createCar(CarDTO dto) {
        Car car = dto.toEntity();
        car.setId(UUID.randomUUID().toString());
        Car carSaved = carRepository.save(car);
        kafkaTemplate.send("cars", new CarEvent(carSaved.getId(),"CREATED", carSaved.isAvailable()));
        return CarDTO.fromEntity(carSaved);
    }

    @Override
    public CarDTO getCarById(String id) {

        Car car = carRepository.findById(id)
                .orElseThrow(()-> new CarNotFoundException("Car not found with id: " +id));
        return CarDTO.fromEntity(car);
    }

    @Override
    public List<CarDTO> getAllCars() {
        return carRepository.findAll().stream().map(CarDTO::fromEntity).toList();
    }

    @Override
    public CarDTO updateCar(String id, CarDTO dto) {
        Car car = carRepository.findById(id)
                .orElseThrow(()-> new CarNotFoundException("Car not found with id: " +id));
        car.setName(dto.name());
        car.setAvailable(dto.available());
        Car carUpdated = carRepository.save(car);
        kafkaTemplate.send("cars", new CarEvent(carUpdated.getId(), "UPDATED", carUpdated.isAvailable()));
        return CarDTO.fromEntity(carUpdated);
    }

    @Override
    public void deleteCarById(String id) {
        if(!carRepository.existsById(id)){
            throw  new CarNotFoundException("Car not found with id: " +id);
        }
      carRepository.deleteById(id);
        kafkaTemplate.send("cars", new CarEvent(id, "DELETED", false));
    }
}
