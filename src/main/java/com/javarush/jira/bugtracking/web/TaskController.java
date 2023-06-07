package com.javarush.jira.bugtracking.web;

import com.javarush.jira.bugtracking.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@Slf4j
@RequestMapping(TaskController.REST_URL)
@AllArgsConstructor
public class TaskController {

    static final String REST_URL = "/";
    private TaskService taskService;

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String updateTags(@PathVariable Long id, @Validated @RequestParam Set<String> tags) {
        log.info("Update tags to Task with id={}", id);
        taskService.save(id, tags);
        return "redirect:/index";
    }

    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String addTags(@PathVariable Long id, @Validated @RequestParam Set<String> tags) {
        log.info("Add tags to Task with id={}", id);
        taskService.save(id, tags);
        return "redirect:/index";
    }

    @DeleteMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteTags(@PathVariable Long id) {
        log.info("Delete tags from Task with id={}", id);
        taskService.delete(id);
        return "redirect:/index";
    }
}
