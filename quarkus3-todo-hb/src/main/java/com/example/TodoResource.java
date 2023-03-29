package com.example;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Comparator;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @Inject
    EntityManager entityManager;

    @GET
    public List<Todo> getAll() {
        return entityManager.createQuery("from Todo").getResultList();
    }

    @GET
    @Path("/sorted")
    public List<Todo> getAllSorted() {
        List<Todo> todos = this.getAll();
        todos.sort(Comparator.comparingInt(o -> o.order));
        return todos;
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    public Response update(Todo todo, @PathParam("id") Long id) {
        Todo entity = entityManager.find(Todo.class, id);
        entity.completed = todo.completed;
        entity.order = todo.order;
        entity.title = todo.title;
        entity.url = todo.url;
        entity.categories = todo.categories;
        return Response.ok(entity).build();
    }

    @POST
    @Transactional
    public Response createNew(Todo item) {
        entityManager.persist(item);
        return Response.status(Response.Status.CREATED).entity(item).build();
    }


    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Todo entity = entityManager.find(Todo.class, id);
        entityManager.remove(entity);
        return Response.noContent().build();
    }


}
