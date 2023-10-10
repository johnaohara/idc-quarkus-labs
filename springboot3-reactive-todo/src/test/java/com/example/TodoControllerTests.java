package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient(timeout = "PT15M")
@TestMethodOrder(OrderAnnotation.class)
@Import(ContainersConfig.class)
class TodoControllerTests {
	private static final int DEFAULT_ORDER = 0;
	private static final Map<Long, Todo> TODOS = new HashMap<>();

	@Autowired
	WebTestClient webTestClient;

	@Test
	@Order(DEFAULT_ORDER)
	public void createNew() {
		for (Todo todo : createTodos()) {
			var newTodo = this.webTestClient.post()
				.uri("/api")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.bodyValue(todo)
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
				.expectBody(Todo.class)
				.returnResult().getResponseBody();

			assertThat(newTodo)
				.isNotNull()
				.usingRecursiveComparison()
				.ignoringFields("id")
				.isEqualTo(todo);

			assertThat(newTodo.getId())
				.isNotNull()
				.isPositive();
		}
	}

	@ParameterizedTest
	@ValueSource(strings = { "/api", "/api/" })
	@Order(DEFAULT_ORDER + 1)
	public void getAll(String path) {
		var todosToCheck = createTodos();
		var todos = this.webTestClient.get()
			.uri(path)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.expectBodyList(Todo.class)
			.returnResult().getResponseBody();

		assertThat(todos)
			.hasSameSizeAs(todosToCheck)
			.usingRecursiveFieldByFieldElementComparatorOnFields("title", "completed", "order")
			.containsExactlyElementsOf(todosToCheck)
			.allSatisfy(todo ->
				assertThat(todo.getId())
					.isNotNull()
					.isPositive()
			);

		todos.forEach(todo -> TODOS.put(todo.getId(), todo));
	}

	@Test
	@Order(DEFAULT_ORDER + 2)
	public void getAllSorted() {
		var todosToCheck = TODOS.values().stream()
			.sorted(Comparator.comparingInt(Todo::getOrder))
			.collect(Collectors.toList());

		var todos = this.webTestClient.get()
			.uri("/api/sorted")
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.expectBodyList(Todo.class)
			.returnResult().getResponseBody();

		assertThat(todos)
			.hasSameSizeAs(todosToCheck)
			.usingRecursiveFieldByFieldElementComparatorOnFields("title", "completed", "order")
			.containsExactlyElementsOf(todosToCheck)
			.allSatisfy(todo ->
				assertThat(todo.getId())
					.isNotNull()
					.isPositive()
			);
	}

	@Test
	@Order(DEFAULT_ORDER + 3)
	public void update() {
		var todo = TODOS.values().stream().findFirst().get();
		todo.setUrl("somewhere.com");

		var updatedTodo = this.webTestClient.patch()
			.uri("/api/{id}", todo.getId())
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.bodyValue(todo)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
			.expectBody(Todo.class)
			.returnResult().getResponseBody();

		assertThat(updatedTodo)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(todo);
	}

	@Test
	@Order(DEFAULT_ORDER + 4)
	public void delete() {
		for (Todo todo : TODOS.values()) {
			this.webTestClient.delete()
				.uri("/api/{id}", todo.getId())
				.exchange()
				.expectStatus().isOk();
		}

		this.webTestClient.get()
			.uri("/api")
			.exchange()
			.expectStatus().isOk()
			.expectBodyList(Todo.class)
			.hasSize(0);
	}

	private static List<Todo> createTodos() {
		var todo1 = new Todo();
		todo1.setTitle("Go on vacation");
		todo1.setCompleted(false);
		todo1.setOrder(1);

		var todo2 = new Todo();
		todo2.setTitle("Write tests");
		todo2.setCompleted(true);
		todo2.setOrder(0);

		return List.of(todo1, todo2);
	}
}