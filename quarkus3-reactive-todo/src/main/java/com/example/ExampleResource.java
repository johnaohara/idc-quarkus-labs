package com.example;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.common.annotation.NonBlocking;

@Path("/")
public class ExampleResource {

    @Inject
    @ConfigProperty(name = "app.runtime")
    String runtime;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @NonBlocking
    public String hello() {
        return String.format("Hello, World from Quarkus running in %s !!!",runtime);
    }
}