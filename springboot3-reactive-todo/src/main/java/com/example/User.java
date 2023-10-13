package com.example;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table("Todo_User")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private Long id;

    public String surname;

    public String firstname;

    public String email;


}
