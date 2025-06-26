package com.jhipster.demo.task.service.mapper;

import static com.jhipster.demo.task.domain.TaskAsserts.*;
import static com.jhipster.demo.task.domain.TaskTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jhipster.demo.task.domain.Task;
import com.jhipster.demo.task.service.dto.TaskDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link TaskMapper}.
 *
 * Este test verifica que el mapeo entre entidades Task y TaskDTO
 * funcione correctamente en todos los escenarios, incluyendo
 * casos edge como valores nulos y campos opcionales.
 */
class TaskMapperTest {

    private TaskMapper taskMapper;

    @BeforeEach
    void setUp() {
        taskMapper = new TaskMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTaskSample1();
        var actual = taskMapper.toEntity(taskMapper.toDto(expected));
        assertTaskAllPropertiesEquals(expected, actual);
    }

    @Test
    void shouldMapEntityToDTO() {
        // Given
        Task task = new Task();
        task.setId(1L);
        task.setDescription("Tarea de prueba");
        task.setCompleted(false);
        task.setCreatedAt(Instant.parse("2025-06-24T10:00:00Z"));
        task.setTargetDate(Instant.parse("2025-06-25T10:00:00Z"));

        // When
        TaskDTO taskDTO = taskMapper.toDto(task);

        // Then
        assertThat(taskDTO).isNotNull();
        assertThat(taskDTO.getId()).isEqualTo(1L);
        assertThat(taskDTO.getDescription()).isEqualTo("Tarea de prueba");
        assertThat(taskDTO.getCompleted()).isFalse();
        assertThat(taskDTO.getCreatedAt()).isEqualTo(Instant.parse("2025-06-24T10:00:00Z"));
        assertThat(taskDTO.getTargetDate()).isEqualTo(Instant.parse("2025-06-25T10:00:00Z"));
    }

    @Test
    void shouldMapDTOToEntity() {
        // Given
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(2L);
        taskDTO.setDescription("Nueva tarea desde DTO");
        taskDTO.setCompleted(true);
        taskDTO.setCreatedAt(Instant.parse("2025-06-24T12:00:00Z"));
        taskDTO.setTargetDate(Instant.parse("2025-06-26T12:00:00Z"));

        // When
        Task task = taskMapper.toEntity(taskDTO);

        // Then
        assertThat(task).isNotNull();
        assertThat(task.getId()).isEqualTo(2L);
        assertThat(task.getDescription()).isEqualTo("Nueva tarea desde DTO");
        assertThat(task.getCompleted()).isTrue();
        assertThat(task.getCreatedAt()).isEqualTo(Instant.parse("2025-06-24T12:00:00Z"));
        assertThat(task.getTargetDate()).isEqualTo(Instant.parse("2025-06-26T12:00:00Z"));
    }

    @Test
    void shouldHandleNullValues() {
        // Given
        Task nullTask = null;
        TaskDTO nullDTO = null;

        // When & Then
        assertThat(taskMapper.toDto(nullTask)).isNull();
        assertThat(taskMapper.toEntity(nullDTO)).isNull();
    }

    @Test
    void shouldHandleNullOptionalFields() {
        // Given - Task con campo opcional targetDate como null
        Task task = new Task();
        task.setId(3L);
        task.setDescription("Tarea sin fecha objetivo");
        task.setCompleted(false);
        task.setCreatedAt(Instant.now());
        task.setTargetDate(null); // Campo opcional

        // When
        TaskDTO taskDTO = taskMapper.toDto(task);

        // Then
        assertThat(taskDTO).isNotNull();
        assertThat(taskDTO.getId()).isEqualTo(3L);
        assertThat(taskDTO.getDescription()).isEqualTo("Tarea sin fecha objetivo");
        assertThat(taskDTO.getCompleted()).isFalse();
        assertThat(taskDTO.getCreatedAt()).isNotNull();
        assertThat(taskDTO.getTargetDate()).isNull();
    }

    @Test
    void shouldMapInstantFieldsCorrectly() {
        // Given
        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
        Instant future = now.plus(7, ChronoUnit.DAYS);

        Task task = new Task();
        task.setId(4L);
        task.setDescription("Test dates");
        task.setCompleted(false);
        task.setCreatedAt(now);
        task.setTargetDate(future);

        // When
        TaskDTO taskDTO = taskMapper.toDto(task);
        Task mappedBack = taskMapper.toEntity(taskDTO);

        // Then
        assertThat(taskDTO.getCreatedAt()).isEqualTo(now);
        assertThat(taskDTO.getTargetDate()).isEqualTo(future);
        assertThat(mappedBack.getCreatedAt()).isEqualTo(now);
        assertThat(mappedBack.getTargetDate()).isEqualTo(future);
    }

    @Test
    void shouldHandleMaxLengthDescription() {
        // Given - Descripción en el límite máximo (200 caracteres)
        String maxDescription = "a".repeat(200);

        Task task = new Task();
        task.setId(5L);
        task.setDescription(maxDescription);
        task.setCompleted(true);
        task.setCreatedAt(Instant.now());

        // When
        TaskDTO taskDTO = taskMapper.toDto(task);
        Task mappedBack = taskMapper.toEntity(taskDTO);

        // Then
        assertThat(taskDTO.getDescription()).isEqualTo(maxDescription);
        assertThat(taskDTO.getDescription()).hasSize(200);
        assertThat(mappedBack.getDescription()).isEqualTo(maxDescription);
    }

    @Test
    void shouldMapBooleanFieldCorrectly() {
        // Given - Test específico para el campo boolean
        Task completedTask = new Task();
        completedTask.setId(6L);
        completedTask.setDescription("Tarea completada");
        completedTask.setCompleted(true);
        completedTask.setCreatedAt(Instant.now());

        Task pendingTask = new Task();
        pendingTask.setId(7L);
        pendingTask.setDescription("Tarea pendiente");
        pendingTask.setCompleted(false);
        pendingTask.setCreatedAt(Instant.now());

        // When
        TaskDTO completedDTO = taskMapper.toDto(completedTask);
        TaskDTO pendingDTO = taskMapper.toDto(pendingTask);

        // Then
        assertThat(completedDTO.getCompleted()).isTrue();
        assertThat(pendingDTO.getCompleted()).isFalse();
    }

    @Test
    void shouldPreserveIdWhenMapping() {
        // Given
        Task taskWithId = new Task();
        taskWithId.setId(999L);
        taskWithId.setDescription("Test ID preservation");
        taskWithId.setCompleted(false);
        taskWithId.setCreatedAt(Instant.now());

        TaskDTO dtoWithId = new TaskDTO();
        dtoWithId.setId(888L);
        dtoWithId.setDescription("Test ID preservation DTO");
        dtoWithId.setCompleted(true);
        dtoWithId.setCreatedAt(Instant.now());

        // When
        TaskDTO mappedDTO = taskMapper.toDto(taskWithId);
        Task mappedEntity = taskMapper.toEntity(dtoWithId);

        // Then
        assertThat(mappedDTO.getId()).isEqualTo(999L);
        assertThat(mappedEntity.getId()).isEqualTo(888L);
    }

    @Test
    void shouldHandleEmptyDescription() {
        // Given - Descripción vacía (aunque no debería pasar validación)
        Task task = new Task();
        task.setId(8L);
        task.setDescription("");
        task.setCompleted(false);
        task.setCreatedAt(Instant.now());

        // When
        TaskDTO taskDTO = taskMapper.toDto(task);

        // Then
        assertThat(taskDTO.getDescription()).isEmpty();
        assertThat(taskDTO.getDescription()).isEqualTo("");
    }
}
