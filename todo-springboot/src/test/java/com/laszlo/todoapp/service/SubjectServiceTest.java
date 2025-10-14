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
import com.laszlo.todoapp.repository.SubjectRepository;
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
public class SubjectServiceTest {

  @Mock // Creates a mock object of SubjectRepository
  private SubjectRepository subjectRepository;

  @InjectMocks // Injects the mock into SubjectService (the class we want to test)
  private SubjectService subjectService;

  private Subject subject1;
  private Subject subject2;

  @BeforeEach
  void setUp() {
    subject1 = new Subject(1L, "Work", null);
    subject2 = new Subject(2L, "Personal", null);
  }

  @Test
  void testGetAllSubjects() {
    // Given
    when(subjectRepository.findAll()).thenReturn(Arrays.asList(subject1, subject2));

    // When
    List<Subject> subjects = subjectService.getAllSubjects();

    // Then
    assertNotNull(subjects);
    assertEquals(2, subjects.size());
    assertEquals("Work", subjects.get(0).getName());
    verify(subjectRepository, times(1)).findAll();
  }

  @Test
  void testGetSubjectByIdFound() {
    // Given
    when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject1));

    // When
    Optional<Subject> foundSubject = subjectService.getSubjectById(1L);

    // Then
    assertTrue(foundSubject.isPresent());
    assertEquals("Work", foundSubject.get().getName());
    verify(subjectRepository, times(1)).findById(1L);
  }

  @Test
  void testGetSubjectByIdNotFound() {
    // Given
    when(subjectRepository.findById(anyLong())).thenReturn(Optional.empty());

    // When
    Optional<Subject> foundSubject = subjectService.getSubjectById(99L);

    // Then
    assertFalse(foundSubject.isPresent());
    verify(subjectRepository, times(1)).findById(99L);
  }

  @Test
  void testSaveSubjectNew() {
    // Given
    Subject newSubject = new Subject("New Subject");
    when(subjectRepository.save(newSubject)).thenReturn(new Subject(3L, "New Subject", null));

    // When
    Subject savedSubject = subjectService.saveSubject(newSubject);

    // Then
    assertNotNull(savedSubject.getId());
    assertEquals("New Subject", savedSubject.getName());
    verify(subjectRepository, times(1)).save(newSubject);
  }

  @Test
  void testSaveSubjectUpdateExisting() {
    // Given
    subject1.setName("Updated Work Subject");
    when(subjectRepository.save(subject1)).thenReturn(subject1);

    // When
    Subject updatedSubject = subjectService.saveSubject(subject1);

    // Then
    assertEquals("Updated Work Subject", updatedSubject.getName());
    verify(subjectRepository, times(1)).save(subject1);
  }

  @Test
  void testDeleteSubject() {
    // Given
    doNothing().when(subjectRepository).deleteById(1L);

    // When
    subjectService.deleteSubject(1L);

    // Then
    verify(subjectRepository, times(1)).deleteById(1L);
  }
}
