package com.laszlo.todoapp.controller;

import com.laszlo.todoapp.model.Subject;
import com.laszlo.todoapp.model.TodoItem;
import com.laszlo.todoapp.service.SubjectService;
import com.laszlo.todoapp.service.TodoItemService;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TodoItemController {

  private final TodoItemService todoItemService;
  private final SubjectService subjectService;

  @Autowired
  public TodoItemController(TodoItemService todoItemService, SubjectService subjectService) {
    this.todoItemService = todoItemService;
    this.subjectService = subjectService;
  }

  @GetMapping("/")
  public String listTodoItems(Model model) {
    model.addAttribute("todoItems", todoItemService.getAllTodoItems());
    model.addAttribute("subjects", subjectService.getAllSubjects()); // Pass subjects to the view
    model.addAttribute(
        "currentDate", LocalDate.now()); // Useful for checking overdue status in Thymeleaf
    return "index";
  }

  @GetMapping("/addTodo")
  public String showAddTodoForm(Model model) {
    model.addAttribute("todoItem", new TodoItem());
    model.addAttribute("subjects", subjectService.getAllSubjects()); // Pass subjects for selection
    return "add-todo-form";
  }

  @PostMapping("/addTodo")
  public String addTodoItem(TodoItem todoItem, RedirectAttributes redirectAttributes) {
    todoItemService.saveTodoItem(todoItem);
    redirectAttributes.addFlashAttribute("message", "Todo item added successfully!");
    return "redirect:/";
  }

  @GetMapping("/editTodo/{id}")
  public String showEditTodoForm(@PathVariable Long id, Model model) {
    todoItemService
        .getTodoItemById(id)
        .ifPresentOrElse(
            todoItem -> model.addAttribute("todoItem", todoItem),
            () -> model.addAttribute("message", "Todo item not found!"));
    model.addAttribute("subjects", subjectService.getAllSubjects()); // Pass subjects for selection
    return "edit-todo-form";
  }

  @PostMapping("/editTodo")
  public String editTodoItem(TodoItem todoItem, RedirectAttributes redirectAttributes) {
    todoItemService.saveTodoItem(todoItem);
    redirectAttributes.addFlashAttribute("message", "Todo item updated successfully!");
    return "redirect:/";
  }

  @GetMapping("/deleteTodo/{id}")
  public String deleteTodoItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    todoItemService.deleteTodoItem(id);
    redirectAttributes.addFlashAttribute("message", "Todo item deleted successfully!");
    return "redirect:/";
  }

  // Optional: Basic CRUD for Subjects themselves (can be in a separate SubjectController)
  @GetMapping("/subjects")
  public String listSubjects(Model model) {
    model.addAttribute("subjects", subjectService.getAllSubjects());
    model.addAttribute("subject", new Subject()); // For adding new subject
    return "subjects"; // A new template for subjects
  }

  @PostMapping("/subjects")
  public String addSubject(Subject subject, RedirectAttributes redirectAttributes) {
    subjectService.saveSubject(subject);
    redirectAttributes.addFlashAttribute("message", "Subject added!");
    return "redirect:/subjects";
  }

  @GetMapping("/deleteSubject/{id}")
  public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    subjectService.deleteSubject(id);
    redirectAttributes.addFlashAttribute("message", "Subject deleted!");
    return "redirect:/subjects";
  }
}
