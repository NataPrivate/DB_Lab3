package neo4j.models;

import neo4j.db.models.Gender;
import neo4j.db.models.Group;
import neo4j.db.models.User;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.*;


public class GroupTest {
    @Test
    public void testGroup() {
        User user1 = new User("User1", "Ukraine, Kiev", 23, Gender.MALE);
        Group group1 = new Group("Social Group 1");
        group1.subscribe(user1);
        assertTrue(group1.getSubscribers().size() == 1);
        assertTrue(group1.getSubscribers().contains(user1));
        assertTrue(user1.getGroups().contains(group1));

        User user2 = new User("User2", "Ukraine, Kiev", 43, Gender.MALE);
        Group group2 = new Group("Social Group 2", new ArrayList<>(Arrays.asList(user1, user2)));
        assertTrue(group2.getTitle().equals("Social Group 2"));
        assertTrue(group2.getSubscribers().size() == 2);
        assertTrue(group2.getSubscribers().contains(user1) && group2.getSubscribers().contains(user2));
        assertTrue(user1.getGroups().containsAll(Arrays.asList(group1, group2)));
    }
}