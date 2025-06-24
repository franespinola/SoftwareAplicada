package com.jhipster.demo.task.service.mapper;

import com.jhipster.demo.task.domain.Task;
import com.jhipster.demo.task.service.dto.TaskDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Task} and its DTO {@link TaskDTO}.
 */
@Mapper(componentModel = "spring")
public interface TaskMapper extends EntityMapper<TaskDTO, Task> {}
