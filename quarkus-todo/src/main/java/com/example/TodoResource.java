package com.example;

import io.quarkus.panache.common.Sort;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @GET
    public List<Todo> getAll() {
        return Todo.listAll(Sort.by("order"));
    }

    @GET
    @Path("/{id}")
    public Todo get(@PathParam("id") Long id) {
        return Todo.findById(id);
    }

    @PATCH
    @Path("/{id}")
    @Transactional
    public Response update(Todo todo, @PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        entity.completed = todo.completed;
        entity.order = todo.order;
        entity.title = todo.title;
        entity.url = todo.url;
        return Response.ok(entity).build();
    }

    @POST
    @Transactional
    public Response createNew(Todo item) {
        item.persist();
        return Response.status(Response.Status.CREATED).entity(item).build();
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        entity.delete();
        return Response.noContent().build();
    }
}
