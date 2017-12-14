package neo4j.db.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class User {
    private String login;
    private String location;
    private int age;
    private Gender gender;
    private List<User> friends;
    private List<Group> groups;
    private List<String> statuses;

    public User(String login, String location, int age, Gender gender) {
        this.login = login;
        this.location = location;
        this.age = age;
        this.gender = gender;

        friends = new ArrayList<>();
        groups = new ArrayList<>();
        statuses = new ArrayList<>();
    }
    public User(String login, String location, int age, Gender gender, List<String> statuses) {
        this(login, location, age, gender);
        statuses.forEach(this::setStatus);
    }

    public void makeFriend(User anotherUser) {
        if (anotherUser != null && anotherUser != this && !friends.contains(anotherUser)) {
            friends.add(anotherUser);
            anotherUser.getFriends().add(this);
        }
    }

    public void setStatus(String status) {
        if (!status.isEmpty())
            getStatuses().add(status);
    }

    public void subscribeToGroup(Group group) {
        if (group != null && !groups.contains(group)) {
            getGroups().add(group);
            group.subscribe(this);
        }
    }
}
