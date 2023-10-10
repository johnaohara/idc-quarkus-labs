package com.example;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table("Todo")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Todo {

    @Id
    private Long id;

    @NotBlank
    private String title;

    private boolean completed;

    @Column("ordering")
    private int order;

    private String url;

    private Long userId;

    @Transient
    private User user;

    @Transient
    private Set<Category> categories;

}
