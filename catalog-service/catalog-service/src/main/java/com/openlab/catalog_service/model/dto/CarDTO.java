package com.openlab.catalog_service.model.dto;

import com.openlab.catalog_service.model.Car;

public record CarDTO(String id, String name, boolean available) {
    public static CarDTO fromEntity( Car car){
        return new CarDTO(car.getId(), car.getName(), car.isAvailable());
    }


    public  Car toEntity(){
        return new Car(id, name, available);
    }
}
