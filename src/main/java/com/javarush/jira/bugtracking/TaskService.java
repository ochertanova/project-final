package com.javarush.jira.bugtracking;

import com.javarush.jira.bugtracking.internal.mapper.TaskMapper;
import com.javarush.jira.bugtracking.internal.model.Activity;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.repository.ActivityRepository;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import com.javarush.jira.bugtracking.to.TaskTo;
import com.javarush.jira.common.error.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.javarush.jira.common.Constants.*;

@Service
@Slf4j
public class TaskService extends BugtrackingService<Task, TaskTo, TaskRepository> {
    public TaskService(TaskRepository repository, TaskMapper mapper) {
        super(repository, mapper);
    }

    public List<TaskTo> getAll() {
        return mapper.toToList(repository.getAll());
    }

    private ActivityRepository activityRepository;

    public void save(Long id, Set<String> tags) {
        if (repository.existsById(id)) {
            Optional<Task> task = repository.findById(id);
            task.ifPresent(task1 -> task1.setTags(tags));
        }
    }

    public void delete(Long id) {
        if (repository.existsById(id)) {
            Optional<Task> task = repository.findById(id);
            task.ifPresent(task1 -> task1.setTags(null));
        }
    }

    // TODO: поправить Entity and mapper
    public Long getDayTaskInProgress(Long userId, Long id) {
        Activity activityInProgress = activityRepository.getActivitiesByAuthorAAndTaskAndStatusCode(userId, id, TASK_STATUS_CODE_IN_PROGRESS);
        log.info("activity in progress - " + activityInProgress);
        Activity activityInReady = activityRepository.getActivitiesByAuthorAAndTaskAndStatusCode(userId, id, TASK_STATUS_CODE_READY);
        log.info("activity in ready - " + activityInReady);
        if (activityInProgress != null && activityInReady != null) {
            return getDuration(activityInProgress, activityInReady);
        } else {
            throw new NotFoundException("Task with id=" + id + "is not finished");
        }
    }

    // TODO: поправить Entity and mapper
    public Long getDayTaskInTesting(Long userId, Long id) {
        Activity activityInDone = activityRepository.getActivitiesByAuthorAAndTaskAndStatusCode(userId, id, TASK_STATUS_CODE_DONE);
        log.info("find activity in ready - " + activityInDone);
        Activity activityInReady = activityRepository.getActivitiesByAuthorAAndTaskAndStatusCode(userId, id, TASK_STATUS_CODE_READY);
        log.info("activity in ready - " + activityInReady);
        if (activityInDone != null && activityInReady != null) {
            return getDuration(activityInReady, activityInDone);
        } else {
            throw new NotFoundException("Testing task with id=" + id + "is not finished");
        }
    }

    private Long getDuration(Activity activityFist, Activity activitySecond) {
        Duration duration = Duration.between(activitySecond.getUpdated(), activityFist.getUpdated());
        return duration.toHours();
    }
}
