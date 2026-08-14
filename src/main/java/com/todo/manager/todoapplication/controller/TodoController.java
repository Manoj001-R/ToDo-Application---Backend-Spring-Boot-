package com.todo.manager.todoapplication.controller;

import com.todo.manager.todoapplication.dto.TodoResponse;
import com.todo.manager.todoapplication.entity.Todo;
import com.todo.manager.todoapplication.services.TodoService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/todos")
@SecurityRequirement(name = "Bearer Authentication")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    // CREATE TODO
    @PostMapping
    public TodoResponse createTodo(
            @Valid @RequestBody Todo todo) {

        Todo savedTodo = service.createTodo(todo);

        return convertToResponse(savedTodo);
    }

    // GET ALL USER TODOS
    @GetMapping
    public List<TodoResponse> getAllTodos() {

        return service.getAllTodos()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // GET TODO BY ID
    @GetMapping("/{id}")
    public TodoResponse getTodoById(
            @PathVariable Long id) {

        Todo todo = service.getTodoById(id);

        return convertToResponse(todo);
    }

    // UPDATE TODO
    @PutMapping("/{id}")
    public TodoResponse updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody Todo todo) {

        Todo updatedTodo = service.updateTodo(id, todo);

        return convertToResponse(updatedTodo);
    }

    // DELETE TODO
    @DeleteMapping("/{id}")
    public String deleteTodo(
            @PathVariable Long id) {

        service.deleteTodo(id);

        return "Todo deleted successfully";
    }

    // SEARCH TODOS
    @GetMapping("/search")
    public List<TodoResponse> searchTodos(
            @RequestParam String title) {

        return service.searchTodos(title)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // FILTER TODOS
    @GetMapping("/filter")
    public List<TodoResponse> filterTodos(
            @RequestParam boolean completed) {

        return service.filterTodos(completed)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // PAGINATION
    @GetMapping("/page")
    public Page<TodoResponse> getTodos(
            Pageable pageable) {

        return service.getTodos(pageable)
                .map(this::convertToResponse);
    }

    // CONVERT ENTITY TO DTO
    private TodoResponse convertToResponse(Todo todo) {

        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.isCompleted()
        );
    }
}