package com.example;

import jakarta.persistence.*;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cat_id_seq")
    @SequenceGenerator(name = "cat_id_seq", sequenceName = "cat_id_seq")
    public Long id;

    public String name;


}
