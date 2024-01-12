package server.services;

import database.model.User;
import server.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

public class GroupService {
    private final UserRepository userRepository = new UserRepository();

    public String addFriend(String userUsername, String friendUsername) {
        if (friendUsername == null || friendUsername.trim().isEmpty()) {
            return "Invalid input. Username is required.";
        }

        User friend = userRepository.getUserByUsername(friendUsername);
        if (friend == null) {
            return "User with such username does not exist.";
        }

        //add to friendhip table or to group table

        return "User " + friendUsername + " has been successfully added to your friends list.";

    }

    public String createGroup(String groupName, String[] users, String userUsername) {
        if (groupName == null || groupName.trim().isEmpty()) {
            return "Invalid input. Group name is required.";
        }
        Set<User> groupMembers = new HashSet<>();
        for (String username : users) {
            User user = userRepository.getUserByUsername(username);
            if (user == null) {
                return "User with username " + username + " does not exist.";
            }
            groupMembers.add(user);
        }

        //groupMembers.add(userUsername);

         // create group with the users

        return "Group " + groupName + " has been successfully created.";

    }

    public String getFriends() {
        return "";
    }

    public String getGroups() {
        return "";
    }

}
