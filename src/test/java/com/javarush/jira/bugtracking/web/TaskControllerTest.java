package com.javarush.jira.bugtracking.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.bugtracking.internal.model.Task;
import com.javarush.jira.bugtracking.internal.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.javarush.jira.login.internal.web.UserTestData.ADMIN_ID;
import static com.javarush.jira.login.internal.web.UserTestData.ADMIN_MAIL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest extends AbstractControllerTest {
    @Autowired
    private TaskRepository taskRepository;

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void post_addTags() throws Exception {
        Set<String> tags = Set.of("qa", "lt");
        Task task = taskRepository.getExisted(ADMIN_ID);
        assertEquals(0, task.getTags().size());

        perform(MockMvcRequestBuilders.post("/" + ADMIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("id", ADMIN_ID)
                .param("tags", "qa", "lt"))
                .andExpect(status().isNoContent())
                .andDo(print());

        Task taskAfterUpdate = taskRepository.getExisted(ADMIN_ID);
        Set<String> tagsFromDB = taskAfterUpdate.getTags();

        assertEquals(tags.size(), tagsFromDB.size());
        assertTrue(tagsFromDB.containsAll(tags));

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void put_updateTags() throws Exception {
        Set<String> tags = Set.of("qa", "lt");
        Task task = taskRepository.getExisted(ADMIN_ID);
        task.setTags(tags);
        Set<String> tagsNew = Set.of("qa", "dev", "back");

        perform(MockMvcRequestBuilders.put("/" + ADMIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("id", ADMIN_ID)
                .param("tags", "qa", "dev", "back"))
                .andExpect(status().isNoContent())
                .andDo(print());

        Task taskAfterUpdate = taskRepository.getExisted(ADMIN_ID);
        Set<String> tagsFromDB = taskAfterUpdate.getTags();

        assertEquals(tagsNew.size(), tagsFromDB.size());
        assertTrue(tagsFromDB.containsAll(tagsNew));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @WithUserDetails(value = ADMIN_MAIL)
    void deleteTags() throws Exception {
        Set<String> tags = Set.of("qa", "lt");
        Task task = taskRepository.getExisted(ADMIN_ID);
        task.setTags(tags);

        perform(MockMvcRequestBuilders.delete("/" + ADMIN_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("id", ADMIN_ID))
                .andExpect(status().isNoContent())
                .andDo(print());

        Task taskAfterUpdate = taskRepository.getExisted(ADMIN_ID);
        Set<String> tagsFromDB = taskAfterUpdate.getTags();

        assertNull(tagsFromDB);
    }
}
