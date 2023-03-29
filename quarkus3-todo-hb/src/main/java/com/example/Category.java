package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Category {

    @Id
    @GeneratedValue
    public Long id;

    public String name;


}
