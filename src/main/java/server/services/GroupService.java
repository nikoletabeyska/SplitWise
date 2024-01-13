package server.services;

import database.model.User;
import server.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;

public class GroupService {
    private final UserRepository userRepository = new UserRepository();

    public String createGroup(String groupName, String[] users, String userUsername) {
        if (!UserManager.isValidString(groupName)) {
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
