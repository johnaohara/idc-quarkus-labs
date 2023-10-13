package com.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

@Entity
@Table(name="Todo_User")
public class User extends PanacheEntity {

    public String surname;

    public String firstname;

    public String email;


}
