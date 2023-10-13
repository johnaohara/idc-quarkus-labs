package com.example;

import static io.restassured.RestAssured.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.quarkus.test.junit.QuarkusTest;

import io.restassured.http.ContentType;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
class TodoResourceTests {
	private static final int DEFAULT_ORDER = 0;
	private static final Map<Long, Todo> TODOS = new HashMap<>();

	@Test
	@Order(DEFAULT_ORDER)
	public void createNew() {
		createTodos().forEach(todo -> {
			var newTodo = given()
				.contentType(ContentType.JSON)
				.body(todo)
				.when().post("/api").then()
				.statusCode(Status.CREATED.getStatusCode())
				.contentType(ContentType.JSON)
				.extract().as(Todo.class);

			assertThat(newTodo)
				.isNotNull()
				.usingRecursiveComparison()
				.ignoringFields("id")
				.isEqualTo(todo);

			assertThat(newTodo.id)
				.isNotNull()
				.isPositive();
		});
	}

	@ParameterizedTest
	@ValueSource(strings = { "/api", "/api/" })
	@Order(DEFAULT_ORDER + 1)
	public void getAll(String path) {
		var todosToCheck = createTodos();
		var todos = get(path).then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.JSON)
			.extract().body()
			.jsonPath().getList(".", Todo.class);

		assertThat(todos)
			.hasSameSizeAs(todosToCheck)
			.usingRecursiveFieldByFieldElementComparatorOnFields("title", "completed", "order")
			.containsExactlyElementsOf(todosToCheck)
			.allSatisfy(todo ->
				assertThat(todo.id)
					.isNotNull()
					.isPositive()
			);

		todos.stream()
			.forEach(todo -> TODOS.put(todo.id, todo));
	}

	@Test
	@Order(DEFAULT_ORDER + 2)
	public void getAllSorted() {
		var todosToCheck = TODOS.values().stream()
			.sorted(Comparator.<Todo>comparingInt(todo -> todo.order))
			.collect(Collectors.toList());

		var todos = get("/api/sorted").then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.JSON)
			.extract().body()
			.jsonPath().getList(".", Todo.class);

		assertThat(todos)
			.hasSameSizeAs(todosToCheck)
			.usingRecursiveFieldByFieldElementComparatorOnFields("title", "completed", "order")
			.containsExactlyElementsOf(todosToCheck)
			.allSatisfy(todo ->
				assertThat(todo.id)
					.isNotNull()
					.isPositive()
			);
	}

	@Test
	@Order(DEFAULT_ORDER + 3)
	public void update() {
		var todo = TODOS.values().stream().findFirst().get();
		todo.url = "somewhere.com";

		var updatedTodo = given()
			.contentType(ContentType.JSON)
			.body(todo)
			.when().patch("/api/{id}", todo.id).then()
			.statusCode(Status.OK.getStatusCode())
			.contentType(ContentType.JSON)
			.extract().as(Todo.class);

		assertThat(updatedTodo)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(todo);
	}

	@Test
	@Order(DEFAULT_ORDER + 4)
	public void delete() {
		TODOS.values().forEach(todo ->
			given().delete("/api/{id}", todo.id).then()
				.statusCode(Status.NO_CONTENT.getStatusCode())
		);

		get("/api").then()
			.statusCode(Status.OK.getStatusCode())
			.body("$.size()", is(0));
	}

	private static List<Todo> createTodos() {
		var todo1 = new Todo();
		todo1.title = "Go on vacation";
		todo1.completed = false;
		todo1.order = 1;

		var todo2 = new Todo();
		todo2.title = "Write tests";
		todo2.completed = true;
		todo2.order = 0;

		return List.of(todo1, todo2);
	}
}