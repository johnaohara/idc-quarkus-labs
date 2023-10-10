package com.example;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("Todo_User")
@Getter
@Setter
@ToString
public class User {

    @Id
    private Long id;

    public String surname;

    public String firstname;

    public String email;


}
