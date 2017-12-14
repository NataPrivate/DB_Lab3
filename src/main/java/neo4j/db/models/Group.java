package neo4j.db.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


@Getter
public class Group {
    private String title;
    private List<User> subscribers;

    public Group(String title) {
        this.title = title;
        subscribers = new ArrayList<>();
    }
    public Group(String title, List<User> subscribers) {
        this(title);
        if (subscribers != null)
            subscribers.forEach(this::subscribe);
    }

    public void subscribe(User user) {
        if (user != null && !subscribers.contains(user)) {
            subscribers.add(user);
            user.subscribeToGroup(this);
        }
    }
}
