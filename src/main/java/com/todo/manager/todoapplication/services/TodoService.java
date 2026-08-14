package com.todo.manager.todoapplication.services;

import com.todo.manager.todoapplication.entity.Todo;
import com.todo.manager.todoapplication.entity.User;
import com.todo.manager.todoapplication.repository.TodoRepository;
import com.todo.manager.todoapplication.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TodoService {

    private final TodoRepository repository;
    private final UserRepository userRepository;

    public TodoService(
            TodoRepository repository,
            UserRepository userRepository) {

        this.repository = repository;
        this.userRepository = userRepository;
    }

    public Page<Todo> getTodos(Pageable pageable) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return repository.findByUserUsername(username, pageable);
    }

    public Todo createTodo(Todo todo) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        todo.setUser(user);

        return repository.save(todo);
    }

    public List<Todo> getAllTodos() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return repository.findByUserUsername(username);
    }

    public Todo getTodoById(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Todo todo = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Todo not found"
                        ));

        if (!todo.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this Todo"
            );
        }

        return todo;
    }
    public Todo updateTodo(Long id, Todo todo) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Todo existingTodo = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Todo not found"
                        ));

        if (!existingTodo.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to update this Todo"
            );
        }

        existingTodo.setTitle(todo.getTitle());
        existingTodo.setCompleted(todo.isCompleted());

        return repository.save(existingTodo);
    }

    public void deleteTodo(Long id) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        Todo todo = repository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Todo not found"
                        ));

        if (!todo.getUser().getUsername().equals(username)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this Todo"
            );
        }

        repository.delete(todo);
    }

    public List<Todo> searchTodos(String title) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return repository.findByUserUsernameAndTitleContainingIgnoreCase(
                username,
                title
        );
    }


    public List<Todo> filterTodos(boolean completed) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        return repository.findByUserUsernameAndCompleted(
                username,
                completed
        );
    }
}