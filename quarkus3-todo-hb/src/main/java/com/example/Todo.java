package com.example;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "todo_id_seq")
    @SequenceGenerator(name = "todo_id_seq", sequenceName = "todo_id_seq")
    public Long id;


    @NotBlank
    @Column(unique = true)
    public String title;

    public boolean completed;

    @Column(name = "ordering")
    public int order;

    public String url;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    public User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Todo_Categories", joinColumns = @JoinColumn(name = "todo_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    public Set<Category> categories;

}
