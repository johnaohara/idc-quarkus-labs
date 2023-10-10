package com.example;

import jakarta.persistence.Entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity
public class Category extends PanacheEntity {

    public String name;


}
