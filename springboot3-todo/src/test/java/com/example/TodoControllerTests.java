package com.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
class TodoControllerTests {
	private static final int DEFAULT_ORDER = 0;
	private static final Map<Long, Todo> TODOS = new HashMap<>();

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@Test
	@Order(DEFAULT_ORDER)
	public void createNew() throws Exception {
		for (Todo todo : createTodos()) {
			var newTodo = this.objectMapper.readValue(this.mockMvc.perform(
				post("/api")
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(this.objectMapper.writeValueAsString(todo))
			)
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
				.andReturn().getResponse().getContentAsString(), Todo.class);

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
	public void getAll(String path) throws Exception {
		var todosToCheck = createTodos();
		var todos = this.objectMapper.readValue(this.mockMvc.perform(get(path))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
			.andReturn().getResponse().getContentAsString(), Todo[].class);

		assertThat(todos)
			.hasSameSizeAs(todosToCheck)
			.usingRecursiveFieldByFieldElementComparatorOnFields("title", "completed", "order")
			.containsExactlyElementsOf(todosToCheck);

		Arrays.stream(todos)
			.forEach(todo -> TODOS.put(todo.getId(), todo));
	}

	@Test
	@Order(DEFAULT_ORDER + 2)
	public void getAllSorted() throws Exception {
		var todosToCheck = TODOS.values().stream()
			.sorted(Comparator.comparingInt(Todo::getOrder))
			.collect(Collectors.toList());

		var todos = this.objectMapper.readValue(this.mockMvc.perform(get("/api/sorted"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
			.andReturn().getResponse().getContentAsString(), Todo[].class);

		assertThat(todos)
			.hasSameSizeAs(todosToCheck)
			.usingRecursiveFieldByFieldElementComparatorOnFields("title", "completed", "order")
			.containsExactlyElementsOf(todosToCheck);
	}

	@Test
	@Order(DEFAULT_ORDER + 3)
	public void update() throws Exception {
		var todo = TODOS.values().stream().findFirst().get();
		todo.setUrl("somewhere.com");

		var updatedTodo = this.objectMapper.readValue(
			this.mockMvc.perform(
				patch("/api/{id}", todo.getId())
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(this.objectMapper.writeValueAsString(todo))
			)
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
				.andReturn().getResponse().getContentAsString(), Todo.class);

		assertThat(updatedTodo)
			.isNotNull()
			.usingRecursiveComparison()
			.isEqualTo(todo);
	}

	@Test
	@Order(DEFAULT_ORDER + 4)
	public void delete() throws Exception {
		for (Todo todo : TODOS.values()) {
			this.mockMvc.perform(MockMvcRequestBuilders.delete("/api/{id}", todo.getId()))
				.andExpect(status().isOk());
		}

		this.mockMvc.perform(get("/api"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.size()").value(0));
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