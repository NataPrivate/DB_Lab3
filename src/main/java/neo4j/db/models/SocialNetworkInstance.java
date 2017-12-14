package neo4j.db.models;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Getter
public class SocialNetworkInstance {
    private List<User> users;
    private List<Group> groups;

    public SocialNetworkInstance(int usersCount, int groupsCount) {
        users = getUsers(usersCount);
        groups = getGroups(groupsCount);
    }

    private List<User> getUsers(int usersCount) {
        List<User> allUsers = new ArrayList<>();
        for (int i = 0; i < usersCount; i++)
            allUsers.add(getGeneratedUser(i));

        Random random = new Random();
        int fromIndex;
        int toIndex;
        List<User> friends;
        for (User user: allUsers) {
            fromIndex = random.nextInt(usersCount) + 1;
            toIndex = fromIndex + random.nextInt(usersCount - fromIndex + 1);
            friends = allUsers.subList(fromIndex, toIndex);
            friends.forEach(friend -> {
                if (friend != user)
                    user.makeFriend(friend);
            });
        }
        return allUsers;
    }
    private User getGeneratedUser(int i) {
        StringBuilder login = new StringBuilder("login").append(i);
        StringBuilder location = new StringBuilder("Country").append(i).append(' ')
                                .append("City").append(i);
        Random random = new Random();
        int minAge = 14;
        int age = random.nextInt(70) + minAge;
        List<String> statuses = getStatuses();
        return new User(login.toString(), location.toString(), age,
                        Gender.values()[random.nextInt(2)], statuses);
    }

    private List<String> getStatuses() {
        List<String> statuses = new ArrayList<>();
        Random random = new Random();
        int maxStatusesNumber = 5;
        int statusesCount = random.nextInt(maxStatusesNumber) + 1;
        int maxCharactersNumber = 30;
        int charactersCount;
        StringBuilder status;
        for(; statusesCount > 0; statusesCount--) {
            status = new StringBuilder();
            charactersCount = random.nextInt(maxCharactersNumber) + 1;
            for (int j = charactersCount; j > 0; j--)
                status.append((char)(random.nextInt('z' - 'a') + 'a'));
            statuses.add(status.toString());
        }
        return statuses;
    }

    private List<Group> getGroups(int groupsCount) {
        List<Group> allGroups = new ArrayList<>();
        if (users.size() == 0) {
            for (int i = 0; i < groupsCount; i++)
                allGroups.add(getGeneratedGroup(i, null));
        }
        else {
            Random random = new Random();
            int fromIndex;
            int toIndex;
            int usersCount = users.size();
            for (int i = 0; i < groupsCount; i++) {
                fromIndex = random.nextInt(usersCount) + 1;
                toIndex = fromIndex + random.nextInt(usersCount - fromIndex + 1);
                allGroups.add(getGeneratedGroup(i, users.subList(fromIndex, toIndex)));
            }
        }
        return allGroups;
    }
    private Group getGeneratedGroup(int i, List<User> users) {
        StringBuilder title = new StringBuilder("Title").append(i);
        return new Group(title.toString(), users);
    }
}
