package com.example;

import jakarta.persistence.*;

@Entity
@Table(name="Todo_User")
public class User {
    @Id
    @GeneratedValue
    public Long id;

    public String surname;

    public String firstname;

    public String email;


}
