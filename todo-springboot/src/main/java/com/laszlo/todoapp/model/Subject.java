package com.laszlo.todoapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  // Relationship: A Subject can have many TodoItems
  // mappedBy indicates the field in TodoItem that owns the relationship
  // cascade = CascadeType.ALL means operations (like delete) on Subject will cascade to associated
  // TodoItems
  // orphanRemoval = true means if a TodoItem is disassociated from a Subject, it will be removed
  // from the DB
  @OneToMany(
      mappedBy = "subject",
      cascade = jakarta.persistence.CascadeType.ALL,
      orphanRemoval = true)
  private Set<TodoItem> todoItems = new HashSet<>(); // Initialize to prevent NullPointerException

  // You might want a constructor that just takes the name
  public Subject(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name; // Useful for Thymeleaf select box display
  }
}
