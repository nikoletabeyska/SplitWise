package server.services;

import com.mysql.cj.x.protobuf.MysqlxDatatypes;
import database.model.Group;
import database.model.User;
import server.repository.GroupRepository;
import server.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GroupService {
     private GroupRepository groupRepository ;
    private UserRepository userRepository ;

    public void GroupService()
    {
        groupRepository=new GroupRepository("TestPersistenceUnit");
        userRepository=new UserRepository();
    }

    public String createGroup(String groupName, ArrayList<String> users) {
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
        return "Group " + groupName + " has been successfully created.";

    }

    public String getFriends() {
        return "";
    }

    public String getGroups(User participant) {
        groupRepository.getAllGroups(participant);
        return "";
    }

}
