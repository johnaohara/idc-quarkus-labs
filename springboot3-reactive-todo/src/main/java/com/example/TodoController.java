package com.example;

import java.util.LinkedHashSet;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping({ "api", "api/" })
public class TodoController {

    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public TodoController(TodoRepository todoRepository, CategoryRepository categoryRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Flux<Todo> findAll() {
        return todoRepository.findAll()
          .flatMap(this::enrichTodo);
    }

    @GetMapping("/sorted")
    public Flux<Todo> getAllSorted() {
        return todoRepository.findAllByOrderByOrder()
          .flatMap(this::enrichTodo);
    }

    @PatchMapping("/{id}")
    @Transactional
    public Mono<Todo> update(@RequestBody Todo todo, @PathVariable("id") Long id) {
        return this.todoRepository.save(todo)
          .flatMap(this::enrichTodo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Mono<Todo> createNew(@RequestBody Todo todo) {
        return this.todoRepository.save(todo)
          .flatMap(this::enrichTodo);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public Mono<Void> delete(@PathVariable Long id) {
        return this.todoRepository.deleteById(id);
    }

    private Mono<Todo> enrichTodo(Todo todo) {
        // This is needed because Spring Data R2DBC is not a fully-fledged ORM
        // and it does not support entity relationships
        var userMono = this.todoRepository.getUserIdForTodo(todo.getId())
          .flatMap(this.userRepository::findById)
          .defaultIfEmpty(new User());

        var categoriesMono = this.todoRepository.getCategoryIdsForTodo(todo.getId())
          .flatMap(this.categoryRepository::findById)
          .collectList()
          .map(LinkedHashSet::new)
          .defaultIfEmpty(new LinkedHashSet<>());

        // Get the User & Categories in parallel
        // Then merge their results back into the entity
        return Mono.zip(
          userMono,
          categoriesMono,
          (user, categories) -> todo.toBuilder()
            .user(user)
            .categories(categories)
            .build()
        ).defaultIfEmpty(todo);
    }

}
