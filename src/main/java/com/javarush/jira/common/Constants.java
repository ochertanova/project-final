package com.javarush.jira.common;

import org.springframework.stereotype.Component;

@Component
public class Constants {
    public static final String TASK_STATUS_CODE_IN_PROGRESS = "in progress";
    public static final String TASK_STATUS_CODE_DONE = "done";
    public static final String TASK_STATUS_CODE_READY = "ready";
    public static final String TASK_STATUS_CODE_ICEBOX = "icebox";
    public static final String TASK_STATUS_CODE_BACKLOG = "backlog";
}
