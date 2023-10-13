package com.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlElement;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Todo {

    @Id
    @Getter @Setter
    private Long id;

    @NotBlank
    @Getter @Setter
    private String title;

    @Getter @Setter
    private boolean completed;

    @Column("ordering")
    @Getter @Setter
    private int order;

    @Getter @Setter
    private String url;

    @Getter @Setter
    private Long userId;

    @Transient
    @Getter(onMethod_ = @XmlElement) @Setter
    private User user;

    @Transient
    private Set<Category> categories;

    @XmlElement
    public List<Category> getCategories() {
        return Optional.ofNullable(this.categories)
          .map(ArrayList::new)
          .orElseGet(ArrayList::new);
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = Optional.ofNullable(this.categories)
          .orElseGet(LinkedHashSet::new);

        Optional.ofNullable(categories)
          .ifPresent(this.categories::addAll);
    }
}
