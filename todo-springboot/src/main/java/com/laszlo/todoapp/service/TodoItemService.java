package com.laszlo.todoapp.service;

import com.laszlo.todoapp.model.TodoItem;
import com.laszlo.todoapp.repository.TodoItemRepository;
import java.util.List; // New import
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired; // New import
import org.springframework.stereotype.Service;

@Service
public class TodoItemService {

  private final TodoItemRepository todoItemRepository;

  @Autowired
  public TodoItemService(TodoItemRepository todoItemRepository) {
    this.todoItemRepository = todoItemRepository;
  }

  public List<TodoItem> getAllTodoItems() {
    return todoItemRepository.findAll();
  }

  public Optional<TodoItem> getTodoItemById(Long id) {
    return todoItemRepository.findById(id);
  }

  public TodoItem saveTodoItem(TodoItem todoItem) {
    return todoItemRepository.save(todoItem);
  }

  public void deleteTodoItem(Long id) {
    todoItemRepository.deleteById(id);
  }
}
