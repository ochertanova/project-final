package com.javarush.jira.bugtracking.internal.repository;

import com.javarush.jira.bugtracking.internal.model.Activity;
import com.javarush.jira.common.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ActivityRepository extends BaseRepository<Activity> {

    @Query("SELECT a FROM Activity a WHERE a.author=:authorId and a.task=:taskId and a.statusCode=:statusCode")
    Activity getActivitiesByAuthorAAndTaskAndStatusCode(Long authorId, Long taskId, String statusCode);
}
