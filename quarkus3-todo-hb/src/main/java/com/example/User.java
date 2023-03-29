package com.example;

import jakarta.persistence.*;

@Entity
@Table(name="Todo_User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    public Long id;

    public String surname;

    public String firstname;

    public String email;


}
