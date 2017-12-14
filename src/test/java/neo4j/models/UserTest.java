package neo4j.models;

import neo4j.db.models.Gender;
import neo4j.db.models.Group;
import neo4j.db.models.User;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;


public class UserTest {
    @Test
    public void testUser() {
        User user1 = new User("user1", "Ukraine, Dorf", 35, Gender.MALE);
        assertEquals(user1.getLogin(), "user1");
        assertEquals(user1.getLocation(), "Ukraine, Dorf");
        assertEquals(user1.getAge(), 35);
        assertTrue(user1.getGender().equals(Gender.MALE) && user1.getGender().equals(Gender.valueOf("MALE")));

        User user2 = new User("user2", "Ukraine, Stadt", 39, Gender.FEMALE,
                new ArrayList<>(Arrays.asList("status1", "status2", "status3")));
        assertEquals(user2.getGender(), Gender.FEMALE);
        assertTrue(user2.getStatuses().size() == 3);
        assertTrue(user2.getStatuses().containsAll(Arrays.asList("status1", "status2", "status3")));
    }

    @Test
    public void makeFriend() throws Exception {
        User user1 = new User("user1", "Ukraine, Dorf", 35, Gender.MALE);
        user1.makeFriend(user1);
        assertTrue(user1.getFriends().size() == 0);
        User user2 = new User("user2", "Ukraine, Dorf", 35, Gender.FEMALE);
        user1.makeFriend(user2);
        assertTrue(user2.getFriends().size() == 1);
        assertTrue(user2.getFriends().contains(user1));
    }

    @Test
    public void subscribeToGroup() throws Exception {
        User user1 = new User("user1", "Ukraine, Dorf", 35, Gender.MALE);
        Group group1 = new Group("group1");
        user1.subscribeToGroup(group1);
        assertTrue(user1.getGroups().size() == 1);
        assertTrue(user1.getGroups().contains(group1));
        assertTrue(group1.getSubscribers().size() == 1);
        assertTrue(group1.getSubscribers().contains(user1));
    }

    @Test
    public void setStatus() throws Exception {
        User user1 = new User("user1", "Ukraine, Dorf", 35, Gender.MALE);
        user1.setStatus("status1");
        assertTrue(user1.getStatuses().size() == 1);
        assertTrue(user1.getStatuses().contains("status1"));
    }
}