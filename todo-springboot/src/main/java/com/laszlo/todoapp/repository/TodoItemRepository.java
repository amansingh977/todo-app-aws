package com.laszlo.todoapp.repository;

import com.laszlo.todoapp.model.TodoItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Long> {
  // Additional query methods can be defined here if needed
}
