package neo4j;

import neo4j.db.Neo4jHandler;
import neo4j.db.models.SocialNetworkInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class Neo4jHandlerTest {
    private Neo4jHandler handler;

    @Before
    public void init() {
        handler = new Neo4jHandler();
    }

    @Test
    public void testNeo4jHandler() {
        testCreateGraph();
        testMatches();
    }

    public void testCreateGraph() {
        handler.createGraph(new SocialNetworkInstance(9, 6));
        assertEquals(15, handler.getNodesCount());
        assertTrue(handler.getRelationshipsCount() > 0);
    }

    public void testMatches() {
        String userLogin = "login6";
        assertNotNull(handler.getOrderedLoginsList());
        assertNotNull(handler.getOrderedByAgeMaleLoginAgeList());
        assertNotNull(handler.getOrderedFriendsList(userLogin));
        assertNotNull(handler.getOrderedFriendsOfFriendsList(userLogin));
        assertNotNull(handler.getOrderedLoginFriendsCount());

        assertNotNull(handler.getOrderedGroups());
        assertNotNull(handler.getOrderedGroupsOfPerson(userLogin));
        assertNotNull(handler.getOrderedGroupsSubscribersCount());
        assertNotNull(handler.getOrderedUsersGroupsCount());
        assertNotNull(handler.getFriendsOfFriendsSumGroupsCount(userLogin));

        int minStatusLength = 8;
        assertNotNull(handler.getUserStatuses(userLogin));
        assertNotNull(handler.getOrderedUsersAverageStatusesLength());
        assertNotNull(handler.getStatusesWithLengthMoreThan(minStatusLength));
        assertNotNull(handler.getOrderedUsersStatusesCount());
        assertNotNull(handler.getFriendsOfFriendsStatuses(userLogin));
    }

    @After
    public void finish() {
        handler.close();
    }
}