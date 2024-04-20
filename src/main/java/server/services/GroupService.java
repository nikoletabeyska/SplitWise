package server.services;
import com.mysql.cj.conf.ConnectionUrlParser;
import database.model.Friendship;
import database.model.Group;
import database.model.User;
import server.ClassesInitializer;
import server.RepositoryImplementationMapping;
import server.Server;
import server.repository.FriendshipRepository;
import server.repository.GroupRepository;
import server.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupService {

    private GroupRepository groupRepository;
    private UserRepository userRepository;
    private FriendshipRepository friendshipRepository;
    private FriendshipService friendshipService;

    public GroupService(ClassesInitializer initializer) {
        this.groupRepository = initializer.getGroupRepository();
        this.userRepository = initializer.getUserRepository();
        this.friendshipRepository = initializer.getFriendshipRepository();
        this.friendshipService = initializer.getFriendshipService();
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
            //business logic for friendship requirements
            boolean exists = friendshipService.checkFriendshipExistence(user, userRepository.getUserByUsername(userUsername));
            if (exists) {
                groupMembers.add(user);
            } else {
                return "User with username " + username + " is not in your friend list.";
            }
        }
        if (groupMembers.size() < 3) {
            return "Not enough members to create a group!";
        }
        groupRepository.createGroup(new Group(groupName, groupMembers.stream().toList()));
        Logger.log("Created new group " + groupName, userUsername);
        return "Group " + groupName + " has been successfully created.";
    }

    public String getGroups(User participant) {
        List<Group> groups = groupRepository.getAllGroups(participant);
        String groupList = "Groups: \n";
        for (Group g : groups) {
            groupList += g.getName() + "\n";
        }
        Logger.log("Viewed groups ", participant.getUsername());
        return groupList;
    }
}
