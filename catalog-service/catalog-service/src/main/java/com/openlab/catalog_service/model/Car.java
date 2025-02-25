package com.openlab.catalog_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Setter
@Getter

@Table(name = "cars")
public class Car implements Serializable {
    @Id
    private String id;

    private String name;
    private boolean available;


    public Car() {
    }

    public Car(String id, String name, boolean available) {
        this.id = id;
        this.name = name;
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
