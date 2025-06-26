package com.jhipster.demo.task.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.jhipster.demo.task.domain.Task;
import com.jhipster.demo.task.repository.TaskRepository;
import com.jhipster.demo.task.service.dto.TaskDTO;
import com.jhipster.demo.task.service.mapper.TaskMapper;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link TaskService}.
 *
 * Este test verifica la lógica de negocio del servicio de tareas,
 * asegurando que las operaciones CRUD funcionen correctamente
 * y que el mapeo entre entidades y DTOs sea correcto.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        // Preparación común para todos los tests
        task = new Task();
        task.setId(1L);
        task.setDescription("Test task");
        task.setCompleted(false);
        task.setCreatedAt(Instant.now());

        taskDTO = new TaskDTO();
        taskDTO.setId(1L);
        taskDTO.setDescription("Test task");
        taskDTO.setCompleted(false);
        taskDTO.setCreatedAt(task.getCreatedAt());
    }

    @Test
    void shouldSaveNewTask() {
        // Given - Preparamos los datos de entrada
        TaskDTO newTaskDTO = new TaskDTO();
        newTaskDTO.setDescription("Nueva tarea");
        newTaskDTO.setCompleted(false);

        Task newTask = new Task();
        newTask.setDescription("Nueva tarea");
        newTask.setCompleted(false);

        // Configuramos los mocks para simular el comportamiento
        when(taskMapper.toEntity(any(TaskDTO.class))).thenReturn(newTask);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);

        // When - Ejecutamos el método que queremos probar
        TaskDTO result = taskService.save(newTaskDTO);

        // Then - Verificamos que el resultado sea el esperado
        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Test task");
        assertThat(result.getCompleted()).isFalse();

        // Verificamos que se llamaron los métodos correctos
        verify(taskRepository).save(any(Task.class));
        verify(taskMapper).toEntity(newTaskDTO);
        verify(taskMapper).toDto(task);
    }

    @Test
    void shouldFindTaskById() {
        // Given
        Long taskId = 1L;
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskDTO);

        // When
        Optional<TaskDTO> result = taskService.findOne(taskId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(taskId);
        assertThat(result.get().getDescription()).isEqualTo("Test task");
        assertThat(result.get().getCompleted()).isFalse();

        verify(taskRepository).findById(taskId);
        verify(taskMapper).toDto(task);
    }

    @Test
    void shouldReturnEmptyWhenTaskNotFound() {
        // Given
        Long nonExistentTaskId = 999L;
        when(taskRepository.findById(nonExistentTaskId)).thenReturn(Optional.empty());

        // When
        Optional<TaskDTO> result = taskService.findOne(nonExistentTaskId);

        // Then
        assertThat(result).isEmpty();

        verify(taskRepository).findById(nonExistentTaskId);
        // Verificamos que NO se llamó al mapper porque no había tarea
        verify(taskMapper, never()).toDto(any(Task.class));
    }

    @Test
    void shouldDeleteTask() {
        // Given
        Long taskId = 1L;

        // When
        taskService.delete(taskId);

        // Then
        verify(taskRepository).deleteById(taskId);
    }

    @Test
    void shouldUpdateExistingTask() {
        // Given
        TaskDTO updatedTaskDTO = new TaskDTO();
        updatedTaskDTO.setId(1L);
        updatedTaskDTO.setDescription("Tarea actualizada");
        updatedTaskDTO.setCompleted(true);
        updatedTaskDTO.setCreatedAt(task.getCreatedAt());

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setDescription("Tarea actualizada");
        updatedTask.setCompleted(true);
        updatedTask.setCreatedAt(task.getCreatedAt());

        when(taskMapper.toEntity(any(TaskDTO.class))).thenReturn(updatedTask);
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(updatedTaskDTO);

        // When
        TaskDTO result = taskService.save(updatedTaskDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getDescription()).isEqualTo("Tarea actualizada");
        assertThat(result.getCompleted()).isTrue();

        verify(taskRepository).save(updatedTask);
        verify(taskMapper).toEntity(updatedTaskDTO);
        verify(taskMapper).toDto(updatedTask);
    }

    @Test
    void shouldHandlePartialUpdate() {
        // Given
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setDescription("Tarea original");
        existingTask.setCompleted(false);
        existingTask.setCreatedAt(Instant.now());

        Task partialUpdate = new Task();
        partialUpdate.setId(1L);
        partialUpdate.setCompleted(true); // Solo cambiamos el estado

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(taskDTO);

        // When
        Optional<TaskDTO> result = taskService.partialUpdate(taskDTO);

        // Then
        assertThat(result).isPresent();
        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }
}
