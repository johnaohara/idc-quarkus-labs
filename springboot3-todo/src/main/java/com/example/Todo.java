package com.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;

import jakarta.validation.constraints.NotBlank;

import jakarta.xml.bind.annotation.XmlElement;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter @Setter
    private Long id;

    @NotBlank
    @Column(unique = true)
    @Getter @Setter
    private String title;

    @Getter @Setter
    private boolean completed;

    @Column(name = "ordering")
    @Getter @Setter
    private int order;

    @Getter @Setter
    private String url;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="user_id")
    public User user;

    @XmlElement
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "Todo_Categories", joinColumns = @JoinColumn(name = "todo_id"),
               inverseJoinColumns = @JoinColumn(name = "category_id"))
    public Set<Category> categories;


    protected Set<Category> getCategoriesInternal() {
        if(this.categories==null) {
            this.categories = new HashSet<>();
        }
        return this.categories;
    }

    @XmlElement
    public List<Category> getCategories() {
        return new ArrayList<>(getCategoriesInternal());
    }

    public void addCategory(Category category) {
        getCategoriesInternal().add(category);
    }

    public Todo() {
    }

}
