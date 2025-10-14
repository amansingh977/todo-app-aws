package com.laszlo.todoapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.laszlo.todoapp.model.Subject;
import com.laszlo.todoapp.model.TodoItem;
import com.laszlo.todoapp.repository.TodoItemRepository;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TodoItemServiceTest {

  @Mock private TodoItemRepository todoItemRepository;

  @InjectMocks private TodoItemService todoItemService;

  private TodoItem todo1;
  private TodoItem todo2;
  private Subject subject1;

  @BeforeEach
  void setUp() {
    subject1 = new Subject(1L, "Work", null);
    todo1 = new TodoItem(1L, "Learn Spring Boot", LocalDate.of(2025, 7, 10), subject1);
    todo2 = new TodoItem(2L, "Build Todo App", LocalDate.of(2025, 6, 20), subject1);
  }

  @Test
  void testGetAllTodoItems() {
    when(todoItemRepository.findAll()).thenReturn(Arrays.asList(todo1, todo2));
    List<TodoItem> todoItems = todoItemService.getAllTodoItems();
    assertNotNull(todoItems);
    assertEquals(2, todoItems.size());
    assertEquals("Learn Spring Boot", todoItems.get(0).getDescription());
    verify(todoItemRepository, times(1)).findAll();
  }

  @Test
  void testGetTodoItemByIdFound() {
    when(todoItemRepository.findById(1L)).thenReturn(Optional.of(todo1));
    Optional<TodoItem> foundTodo = todoItemService.getTodoItemById(1L);
    assertTrue(foundTodo.isPresent());
    assertEquals("Learn Spring Boot", foundTodo.get().getDescription());
    verify(todoItemRepository, times(1)).findById(1L);
  }

  @Test
  void testGetTodoItemByIdNotFound() {
    when(todoItemRepository.findById(anyLong())).thenReturn(Optional.empty());
    Optional<TodoItem> foundTodo = todoItemService.getTodoItemById(99L);
    assertFalse(foundTodo.isPresent());
    verify(todoItemRepository, times(1)).findById(99L);
  }

  @Test
  void testSaveTodoItemNew() {
    TodoItem newTodo = new TodoItem("Buy groceries", LocalDate.of(2025, 7, 1), subject1);

    when(todoItemRepository.save(newTodo))
        .thenReturn(new TodoItem(3L, "Buy groceries", LocalDate.of(2025, 7, 1), subject1));

    TodoItem savedTodo = todoItemService.saveTodoItem(newTodo);

    assertNotNull(savedTodo.getId());
    assertEquals("Buy groceries", savedTodo.getDescription());
    assertEquals(LocalDate.of(2025, 7, 1), savedTodo.getDueDate());
    assertNotNull(savedTodo.getSubject());
    assertEquals("Work", savedTodo.getSubject().getName());
    verify(todoItemRepository, times(1)).save(newTodo);
  }

  @Test
  void testSaveTodoItemUpdateExisting() {
    todo1.setDescription("Learn Spring Boot Advanced");
    todo1.setDueDate(LocalDate.of(2025, 7, 15));
    when(todoItemRepository.save(todo1)).thenReturn(todo1);

    TodoItem updatedTodo = todoItemService.saveTodoItem(todo1);

    assertEquals("Learn Spring Boot Advanced", updatedTodo.getDescription());
    assertEquals(LocalDate.of(2025, 7, 15), updatedTodo.getDueDate());
    verify(todoItemRepository, times(1)).save(todo1);
  }

  @Test
  void testDeleteTodoItem() {
    doNothing().when(todoItemRepository).deleteById(1L);
    todoItemService.deleteTodoItem(1L);
    verify(todoItemRepository, times(1)).deleteById(1L);
  }
}
