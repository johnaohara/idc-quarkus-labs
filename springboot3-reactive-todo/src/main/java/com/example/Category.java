package com.example;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table("Category")
@Getter
@Setter
@ToString
public class Category {
    @Id
    private Long id;

    public String name;
}
