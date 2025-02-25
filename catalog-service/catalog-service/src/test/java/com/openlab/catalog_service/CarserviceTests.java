package com.openlab.catalog_service;

import com.openlab.catalog_service.exception.CarNotFoundException;
import com.openlab.catalog_service.model.Car;
import com.openlab.catalog_service.model.dto.CarDTO;
import com.openlab.catalog_service.model.dto.CarEvent;
import com.openlab.catalog_service.repository.CarRepository;
import com.openlab.catalog_service.service.impl.CarServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CarServiceTests {

    @Mock
    private CarRepository carRepository;

    @Mock
    private KafkaTemplate<String, CarEvent> kafkaTemplate;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);


        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void shouldCreateCar() {

        CarDTO carDTO = new CarDTO(null, "Mercedes GLC", true);


        CarDTO createdCar = carService.createCar(carDTO);


        assertNotNull(createdCar.id());
        assertEquals("Mercedes GLC", createdCar.name());
        assertTrue(createdCar.available());
        verify(carRepository, times(1)).save(any(Car.class));
        verify(kafkaTemplate, times(1)).send(eq("cars"), any(CarEvent.class));
    }

    @Test
    void shouldGetCarById() {
        // Arrange
        Car car = new Car("1", "BMW XX", true);
        when(carRepository.findById("1")).thenReturn(Optional.of(car));

        // Act
        CarDTO carDTO = carService.getCarById("1");

        // Assert
        assertNotNull(carDTO);
        assertEquals("1", carDTO.id());
        assertEquals("BMW XX", carDTO.name());
        assertTrue(carDTO.available());
    }

    @Test
    void shouldThrowExceptionWhenGettingNonExistingCar() {
        // Arrange
        when(carRepository.findById("2")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CarNotFoundException.class, () -> carService.getCarById("2"));
    }

    @Test
    void shouldGetAllCars() {
        // Arrange
        List<Car> mockCars = List.of(
                new Car("1", "BMW XX", true),
                new Car("2", "Toyota Corolla", false)
        );
        when(carRepository.findAll()).thenReturn(mockCars);

        // Act
        List<CarDTO> carDTOList = carService.getAllCars();

        // Assert
        assertEquals(2, carDTOList.size());
        assertEquals("BMW XX", carDTOList.get(0).name());
        assertTrue(carDTOList.get(0).available());
        assertEquals("Toyota Corolla", carDTOList.get(1).name());
        assertFalse(carDTOList.get(1).available());
    }

    @Test
    void shouldUpdateCar() {

        Car car = new Car("1", "BMW XX", true);
        when(carRepository.findById("1")).thenReturn(Optional.of(car));
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarDTO carDTO = new CarDTO("1", "Toyota Corolla", false);


        CarDTO updatedCar = carService.updateCar("1", carDTO);


        assertNotNull(updatedCar);
        assertEquals("1", updatedCar.id());
        assertEquals("Toyota Corolla", updatedCar.name());
        assertFalse(updatedCar.available());
        verify(carRepository, times(1)).save(any(Car.class));
        verify(kafkaTemplate, times(1)).send(eq("cars"), any(CarEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCar() {
        // Arrange
        when(carRepository.findById("1")).thenReturn(Optional.empty());
        CarDTO carDTO = new CarDTO("1", "Toyota Corolla", false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> carService.updateCar("1", carDTO));
        verify(carRepository, never()).save(any(Car.class));
        verify(kafkaTemplate, never()).send(any(), any(CarEvent.class));
    }

    @Test
    void shouldDeleteCar() {

        when(carRepository.existsById("1")).thenReturn(true);


        carService.deleteCarById("1");


        verify(carRepository, times(1)).deleteById("1");
        verify(kafkaTemplate, times(1)).send(eq("cars"), any(CarEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCar() {

        when(carRepository.existsById("1")).thenReturn(false);


        assertThrows(RuntimeException.class, () -> carService.deleteCarById("1"));
        verify(carRepository, never()).deleteById("1");
        verify(kafkaTemplate, never()).send(any(), any(CarEvent.class));
    }
}