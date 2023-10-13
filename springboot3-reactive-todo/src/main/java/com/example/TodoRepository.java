package com.example;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TodoRepository extends R2dbcRepository<Todo,Long> {
    Flux<Todo> findAllByOrderByOrder();

    // These are needed because Spring Data R2DBC is not a fully-fledged ORM
    // and it does not support entity relationships
    @Query("SELECT user_id FROM Todo WHERE id = :todoId")
    Mono<Long> getUserIdForTodo(long todoId);

    @Query("SELECT DISTINCT category_id FROM Todo_Categories WHERE todo_id = :todoId")
    Flux<Long> getCategoryIdsForTodo(long todoId);
}
