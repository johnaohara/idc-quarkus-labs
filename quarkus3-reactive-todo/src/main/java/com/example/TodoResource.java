package com.example;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Sort;

import io.smallrye.mutiny.Uni;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @GET
    public Uni<List<Todo>> getAll() {
        return Todo.listAll();
    }

    @GET
    @Path("/sorted")
    public Uni<List<Todo>> getAllSorted() {
        return Todo.listAll(Sort.by("order"));
    }

    @PATCH
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> update(Todo todo, @PathParam("id") Long id) {
        return Todo.<Todo>findById(id)
          .map(entity -> {
              entity.completed = todo.completed;
              entity.order = todo.order;
              entity.title = todo.title;
              entity.url = todo.url;
              entity.categories = todo.categories;

              return Response.ok(entity).build();
          });
    }

    @POST
    @WithTransaction
    public Uni<Response> createNew(Todo item) {
        return Todo.persist(item)
          .replaceWith(item)
          .map(todo -> Response.status(Status.CREATED).entity(todo).build());
    }


    @DELETE
    @Path("/{id}")
    @WithTransaction
    public Uni<Response> delete(@PathParam("id") Long id) {
        return Todo.deleteById(id)
          .map(v -> Response.noContent().build());
    }


}
