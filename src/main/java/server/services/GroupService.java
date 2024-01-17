package server.services;
import com.mysql.cj.conf.ConnectionUrlParser;
import database.model.Friendship;
import database.model.Group;
import database.model.User;
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
    public GroupService()
    {
        this.groupRepository = (GroupRepository) RepositoryImplementationMapping.get(GroupRepository.class);
        this.userRepository = (UserRepository) RepositoryImplementationMapping.get(UserRepository.class);
        this.friendshipRepository = (FriendshipRepository) RepositoryImplementationMapping.get(FriendshipRepository.class);
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
            List<Friendship> friendships = friendshipRepository.getAllFriendships(userRepository.getUserByUsername(userUsername));
            if (friendships == null) {
                return "You have no friends to create group. Add the passed parameters to your friends list.";
            }
            //TODO consider business logic for friendship requirements
            groupMembers.add(user);
//            for (Friendship f : friendships) {
//                if (f.getFirstFriend().equals(username) || f.getSecondFriend().equals(username)) {
//                    groupMembers.add(user);
//
//                } else {
//                    return "User with username " + username + " is not your friend. Add him to your friends list to create this group";
//                }
//            }
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
