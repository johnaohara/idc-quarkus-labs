package com.example;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;

import io.restassured.http.ContentType;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class ExampleResourceTests {
	@Test
	public void hello() {
		get("/").then()
			.statusCode(200)
			.contentType(ContentType.TEXT)
			.body(is("Hello, World from Quarkus running in UNKNOWN !!!"));
	}
}