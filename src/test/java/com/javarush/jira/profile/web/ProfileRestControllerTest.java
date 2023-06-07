package com.javarush.jira.profile.web;

import com.javarush.jira.AbstractControllerTest;
import com.javarush.jira.MatcherFactory;
import com.javarush.jira.common.error.NotFoundException;
import com.javarush.jira.profile.ContactTo;
import com.javarush.jira.profile.ProfileTo;
import com.javarush.jira.profile.internal.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.javarush.jira.common.util.JsonUtil.writeValue;
import static com.javarush.jira.login.internal.web.UserTestData.*;
import static com.javarush.jira.profile.web.ProfileRestController.REST_URL;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProfileRestControllerTest extends AbstractControllerTest {

    public static final MatcherFactory.Matcher<Profile> PROFILE_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(Profile.class, "lastLogin", "lastFailedLogin");
    public static final MatcherFactory.Matcher<ProfileTo> TO_MATCHER = MatcherFactory.usingIgnoringFieldsComparator(ProfileTo.class, "lastLogin", "contacts");

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    ProfileMapper profileMapper;

    @Test
    @WithUserDetails(value = ADMIN_MAIL)
    void get_whenProfileIsAdmin() throws Exception {
        Profile profile = profileRepository.getExisted(ADMIN_ID);
        ProfileTo profileToExpected = profileMapper.toTo(profile);

        ResultActions resultAction = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TO_MATCHER.contentJson(profileToExpected));
        ProfileTo profileTo = TO_MATCHER.readFromJson(resultAction);

        Set<ContactTo> contactsExpected = profileToExpected.getContacts();
        Set<ContactTo> contactsActual = profileTo.getContacts();

        assertEquals(contactsExpected.size(), contactsActual.size());
        assertTrue(contactsActual.containsAll(contactsExpected));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void get_whenProfileIsUser() throws Exception {
        Profile profile = profileRepository.getExisted(USER_ID);
        ProfileTo profileToExpected = profileMapper.toTo(profile);

        ResultActions resultAction = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(TO_MATCHER.contentJson(profileToExpected));
        ProfileTo profileTo = TO_MATCHER.readFromJson(resultAction);

        Set<ContactTo> contactsExpected = profileToExpected.getContacts();
        Set<ContactTo> contactsActual = profileTo.getContacts();

        assertEquals(contactsExpected.size(), contactsActual.size());
        assertTrue(contactsActual.containsAll(contactsExpected));
    }

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void get_whenIfProfileNotExisted() throws Exception {
        ResultActions resultAction = perform(MockMvcRequestBuilders.get(REST_URL))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        ProfileTo profileTo = TO_MATCHER.readFromJson(resultAction);
        Profile profile = profileMapper.toEntity(profileTo);
        assertEquals(GUEST_ID, profile.id());
        assertThrows(NotFoundException.class, () -> profileRepository.getExisted(GUEST_ID));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @WithUserDetails(value = USER_MAIL)
    void put_whenUpdateMailNotification() throws Exception {
        long mailNotificationsToUpdate = 1L;
        Profile profileBefore = profileRepository.getExisted(USER_ID);
        ProfileTo profileToBefore = profileMapper.toTo(profileBefore);
        ProfileTo profileToUpdate = profileToBefore;
        profileToUpdate.setMailNotifications(ProfileUtil.maskToNotifications(mailNotificationsToUpdate));

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(profileToUpdate)))
                .andExpect(status().isNoContent())
                .andDo(print());

        Profile profileUpdate = profileRepository.getExisted(USER_ID);
        assertEquals(mailNotificationsToUpdate, profileUpdate.getMailNotifications());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @WithUserDetails(value = USER_MAIL)
    void put_whenUpdateContacts() throws Exception {
        Profile profileBefore = profileRepository.getExisted(USER_ID);
        ProfileTo profileToBefore = profileMapper.toTo(profileBefore);
        ProfileTo profileToUpdate = profileToBefore;
        Set<ContactTo> contactToUpdate = Set.of(new ContactTo("website", "test.com"));
        profileToUpdate.setContacts(contactToUpdate);

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(profileToUpdate)))
                .andExpect(status().isNoContent())
                .andDo(print());

        Profile profileUpdate = profileRepository.getExisted(USER_ID);
        ProfileTo profileToActual = profileMapper.toTo(profileUpdate);

        Set<ContactTo> contactsExpected = profileToBefore.getContacts();
        Set<ContactTo> contactsActual = profileToActual.getContacts();

        assertEquals(contactsExpected.size(), contactsActual.size());
        assertTrue(contactsActual.containsAll(contactsExpected));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @WithUserDetails(value = GUEST_MAIL)
    void put_whenUpdateNewProfileIsEmpty() throws Exception {
        ProfileTo profileToUpdate = new ProfileTo(GUEST_ID, null, null);

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(profileToUpdate)))
                .andExpect(status().isNoContent())
                .andDo(print());

        Profile newProfile = profileRepository.getExisted(GUEST_ID);
        assertEquals(0L, newProfile.getMailNotifications());
        assertEquals(0, newProfile.getContacts().size());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @WithUserDetails(value = GUEST_MAIL)
    void put_whenUpdateNewProfileWithParam() throws Exception {
        ProfileTo profileToUpdate = new ProfileTo(GUEST_ID, null, null);
        Set<String> mailNotification = Set.of("deadline");
        Set<ContactTo> contactsTo = Set.of(new ContactTo("mobile", "+123456"));
        profileToUpdate.setMailNotifications(mailNotification);
        profileToUpdate.setContacts(contactsTo);

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(profileToUpdate)))
                .andExpect(status().isNoContent())
                .andDo(print());

        Profile newProfile = profileRepository.getExisted(GUEST_ID);
        ProfileTo profileToActual = profileMapper.toTo(newProfile);
        assertEquals(ProfileUtil.notificationsToMask(mailNotification), newProfile.getMailNotifications());
        assertEquals(profileToUpdate.getContacts().size(), profileToActual.getContacts().size());
        assertTrue(profileToActual.getContacts().containsAll(profileToUpdate.getContacts()));
    }

    @Test
    @WithUserDetails(value = USER_MAIL)
    void put_whenUpdateWithInvalidId() throws Exception {
        Profile profileBefore = profileRepository.getExisted(USER_ID);
        Profile profileUpdate = profileRepository.getExisted(ADMIN_ID);
        ProfileTo profileToBefore = profileMapper.toTo(profileBefore);
        ProfileTo profileToUpdate = profileMapper.toTo(profileUpdate);

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(profileToUpdate)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("ProfileTo must has id=" + profileToBefore.id())))
                .andDo(print());
    }

    @Test
    @WithUserDetails(value = GUEST_MAIL)
    void put_whenUpdateWithInvalidContacts() throws Exception {
        ProfileTo profileToUpdate = new ProfileTo(GUEST_ID, null, null);
        Set<String> mailNotification = Set.of("deadline");
        profileToUpdate.setMailNotifications(mailNotification);
        Set<ContactTo> contacts = Set.of(new ContactTo("sms", "+1789456"));
        profileToUpdate.setContacts(contacts);

        perform(MockMvcRequestBuilders.put(REST_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(writeValue(profileToUpdate)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(containsString("Value with key sms not found")))
                .andDo(print());
    }
}
