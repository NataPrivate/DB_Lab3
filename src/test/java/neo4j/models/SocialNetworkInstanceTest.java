package neo4j.models;

import neo4j.db.models.SocialNetworkInstance;
import neo4j.db.models.User;

import org.junit.Test;
import static org.junit.Assert.*;


public class SocialNetworkInstanceTest {
    @Test
    public void testSocialNetworkInstance() {
        SocialNetworkInstance network = new SocialNetworkInstance(10,9);
        assertTrue(network.getUsers().size() == 10);
        assertTrue(network.getGroups().size() == 9);
        int friendRelationCount = 0;
        for (User user : network.getUsers())
            friendRelationCount += user.getFriends().size();
        assertTrue(friendRelationCount > 0);
        int groupRelationCount = 0;
        for (User user : network.getUsers())
            groupRelationCount += user.getGroups().size();
        assertTrue(groupRelationCount > 0);
    }
}