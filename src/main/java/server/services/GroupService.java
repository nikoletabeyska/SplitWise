package server.services;
import database.model.Group;
import database.model.User;
import server.Server;
import server.repository.GroupRepository;
import server.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupService {
    private GroupRepository groupRepository;
    private UserRepository userRepository;
    public GroupService( UserRepository userRepository, GroupRepository groupRepository)
    {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }
    public String createGroup(String groupName, ArrayList<String> users, String userUsername) {
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
        groupRepository.createGroup(new Group(groupName, groupMembers.stream().toList()));
        Server.logger.log("Created new group " + groupName, userUsername);
        return "Group " + groupName + " has been successfully created.";

    }

    public String getGroups(User participant) {
        List<Group> groups = groupRepository.getAllGroups(participant);
        String groupList = "Groups: \n";
        for (Group g : groups) {
            groupList += g.getName() + "\n";
        }
        Server.logger.log("Viewed groups ", participant.getUsername());
        return groupList;
    }

}
