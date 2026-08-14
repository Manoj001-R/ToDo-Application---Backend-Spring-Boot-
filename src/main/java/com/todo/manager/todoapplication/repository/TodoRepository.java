package com.todo.manager.todoapplication.repository;

import com.todo.manager.todoapplication.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    List<Todo> findByUserUsername(String username);

    List<Todo> findByUserUsernameAndTitleContainingIgnoreCase(
            String username,
            String title
    );

    List<Todo> findByUserUsernameAndCompleted(
            String username,
            boolean completed
    );

    Page<Todo> findByUserUsername(
            String username,
            Pageable pageable
    );
}