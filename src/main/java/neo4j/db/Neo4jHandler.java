package neo4j.db;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import java.io.File;
import java.util.List;

import neo4j.db.models.*;


public class Neo4jHandler {
    private final GraphDatabaseService graphDB;

    public Neo4jHandler() {
        graphDB = new GraphDatabaseFactory().newEmbeddedDatabase(new File("data/socialNetwork"));
    }

    public void createGraph(SocialNetworkInstance network) {
        createUsersNodes(network.getUsers());
        setUsersRelations(network.getUsers());
        createGroupsNodes(network.getGroups());
        setUsersAndGroupsRelations(network.getGroups());
    }

    private void createUsersNodes(List<User> users) {
        try (Transaction tx = graphDB.beginTx()) {
            users.forEach((user) -> {
                if (graphDB.findNode(Label.label("User"), "login", user.getLogin()) == null) {
                    Node userNode = graphDB.createNode(Label.label("User"));
                    userNode.setProperty("login", user.getLogin());
                    userNode.setProperty("location", user.getLocation());
                    userNode.setProperty("age", user.getAge());
                    userNode.setProperty("gender", user.getGender().name());
                    String[] statuses = new String[user.getStatuses().size()];
                    for (int i = 0 ; i < statuses.length; i++)
                        statuses[i] = user.getStatuses().get(i);
                    userNode.setProperty("statuses", statuses);
                }
            });
            tx.success();
        }
    }
    private void setUsersRelations(List<User> users) {
        try (Transaction tx = graphDB.beginTx()) {
            Node currentUserNode;
            Node friendNode;
                for (User user: users) {
                    currentUserNode = graphDB.findNode(Label.label("User"), "login", user.getLogin());
                    if (currentUserNode != null)
                        for (User friend: user.getFriends()) {
                            friendNode = graphDB.findNode(Label.label("User"), "login", friend.getLogin());
                            if (friendNode != null)
                                currentUserNode.createRelationshipTo(friendNode, RelationshipType.withName("friend"));
                        }
                }
            tx.success();
        }
    }

    private void createGroupsNodes(List<Group> groups) {
        try (Transaction tx = graphDB.beginTx()) {
            groups.forEach((group) -> {
                if (graphDB.findNode(Label.label("Group"), "title", group.getTitle()) == null)
                    graphDB.createNode(Label.label("Group"))
                            .setProperty("title", group.getTitle());
            });
            tx.success();
        }
    }
    private void setUsersAndGroupsRelations(List<Group> groups) {
        try (Transaction tx = graphDB.beginTx()) {
            Node currentGroupNode;
            Node userNode;
            for (Group group: groups) {
                currentGroupNode = graphDB.findNode(Label.label("Group"), "title", group.getTitle());
                if (currentGroupNode != null)
                    for (User subscriber: group.getSubscribers()) {
                        userNode = graphDB.findNode(Label.label("User"), "login", subscriber.getLogin());
                        if (userNode != null)
                            userNode.createRelationshipTo(currentGroupNode, RelationshipType.withName("member"));
                    }
            }
            tx.success();
        }
    }

    public int getNodesCount() {
        int nodesCount = 0;
        try (Transaction tx = graphDB.beginTx()) {
            ResourceIterator iterator = graphDB.getAllNodes().iterator();
            for (; iterator.hasNext(); nodesCount++, iterator.next()) ;
            tx.success();
        }
        return nodesCount;
    }

    public int getRelationshipsCount() {
        int relationsCount = 0;
        try (Transaction tx = graphDB.beginTx()) {
            ResourceIterator iterator = graphDB.getAllRelationships().iterator();
            for (; iterator.hasNext(); relationsCount++, iterator.next()) ;
            tx.success();
        }
        return relationsCount;
    }

    //region MATCH
    public String getOrderedLoginsList() {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                    .append("RETURN user.login AS Login ")
                    .append("ORDER BY Login");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedByAgeMaleLoginAgeList() {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                    .append("WHERE user.gender = 'MALE' ")
                    .append("RETURN user.age AS Age, user.login AS Login ")
                    .append("ORDER BY Age DESC");
        return  getMatchResultAsString(query.toString());
    }

    public String getOrderedFriendsList(String userLogin) {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                    .append("-[:friend]-> (friend:User) ")
                    .append("WHERE user.login = \'" + userLogin + "\' ")
                    .append("RETURN friend.login AS Login ")
                    .append("ORDER BY Login");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedFriendsOfFriendsList(String userLogin) {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                    .append("-[:friend]-> (firstHandFriend:User) ")
                    .append("-[:friend]-> (secondHandFriend:User) ")
                    .append("WHERE user.login = \'").append(userLogin).append("\' ")
                    .append("RETURN DISTINCT secondHandFriend.login AS Login ")
                    .append("ORDER BY Login");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedLoginFriendsCount() {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                .append("OPTIONAL MATCH (user:User)-[:friend]-> (friend:User) ")
                .append("RETURN user.login AS Login, length(collect(friend)) AS FriendsCount ")
                .append("ORDER BY Login");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedGroups() {
        StringBuilder query = new StringBuilder("MATCH (group:Group) ")
                    .append("RETURN group.title AS Title ")
                    .append("ORDER BY Title");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedGroupsOfPerson(String userLogin) {
        StringBuilder query = new StringBuilder("MATCH (user:User)")
                    .append("-[:member]-> (group:Group) ")
                    .append("WHERE user.login = \'" + userLogin + "\' ")
                    .append("RETURN group.title AS Title ")
                    .append("ORDER BY Title");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedGroupsSubscribersCount() {
        StringBuilder query = new StringBuilder("MATCH (group:Group) ")
                .append("OPTIONAL MATCH (user:User) -[:member]-> (group:Group) ")
                .append("RETURN group.title AS Title, length(collect(user)) AS SubscribersCount ")
                .append("ORDER BY SubscribersCount DESC");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedUsersGroupsCount() {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                .append("OPTIONAL MATCH (user)-[:member]-> (group) ")
                .append("RETURN user.login AS Login, length(collect(group)) AS GroupsCount ")
                .append("ORDER BY GroupsCount DESC");
        return getMatchResultAsString(query.toString());
    }

    public String getFriendsOfFriendsSumGroupsCount(String userLogin) {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                    .append("-[:friend]-> (firstHandFriend:User) ")
                    .append("-[:friend]-> (secondHandFriend:User) ")
                    .append("-[:member]-> (group:Group) ")
                    .append("WHERE user.login = \'").append(userLogin).append("\' ")
                    .append("RETURN count(group) AS FriendsOfFriendsGroupsCount");
        return getMatchResultAsString(query.toString());
    }

    public String getUserStatuses(String userLogin) {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                    .append("WHERE user.login = \'" + userLogin + "\' ")
                    .append("RETURN user.statuses AS Statuses ")
                    .append("ORDER BY Statuses");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedUsersAverageStatusesLength() {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                .append("WITH user.login as Login, EXTRACT(status IN user.statuses| size(status)) as statusesLength ")
                .append("UNWIND statusesLength as statusesLengthCol ")
                .append("RETURN  Login, round(AVG(statusesLengthCol)) as AverageStatusesLength ")
                .append("ORDER BY AverageStatusesLength DESC");
        return getMatchResultAsString(query.toString());
    }

    public String getStatusesWithLengthMoreThan(int length) {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                .append("RETURN FILTER(status IN user.statuses ")
                .append("WHERE size(status) > ").append(length).append(") as Statuses ")
                .append("ORDER BY Statuses");
        return getMatchResultAsString(query.toString());
    }

    public String getOrderedUsersStatusesCount() {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                .append("RETURN user.login AS Login, size(user.statuses) AS StatusesCount ")
                .append("ORDER BY StatusesCount DESC");
        return getMatchResultAsString(query.toString());
    }

    public String getFriendsOfFriendsStatuses(String userLogin) {
        StringBuilder query = new StringBuilder("MATCH (user:User) ")
                    .append("-[:friend]-> (firstHandFriend:User) ")
                    .append("-[:friend]-> (secondHandFriend:User) ")
                    .append("WHERE user.login = \'" + userLogin + "\' ")
                    .append("RETURN DISTINCT secondHandFriend.statuses AS Statuses ")
                    .append("ORDER BY Statuses");
        return getMatchResultAsString(query.toString());
    }

    private String getMatchResultAsString(String query) {
        Result resultIterator;
        try (Transaction tx = graphDB.beginTx()) {
            resultIterator = graphDB.execute(query);
            tx.success();
        }
        return resultIterator.resultAsString();
    }
    //endregion

    public void close() {
        graphDB.shutdown();
    }
}
