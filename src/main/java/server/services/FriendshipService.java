package server.services;

import database.model.Friendship;
import database.model.User;
import server.RepositoryImplementationMapping;
import server.Server;
import server.repository.FriendshipRepository;
import server.repository.UserRepository;

import java.util.List;

public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendshipService() {
        this.userRepository = (UserRepository) RepositoryImplementationMapping.get(UserRepository.class);
        this.friendshipRepository = (FriendshipRepository) RepositoryImplementationMapping.get(FriendshipRepository.class);
    }

    public String addFriend(String userUsername, String friendUsername) {
        if (!UserManager.isValidString(friendUsername)) {
            return "Invalid input. Username is required.";
        }

        User user = userRepository.getUserByUsername(userUsername);
        User friend = userRepository.getUserByUsername(friendUsername);
        if (friend == null) {
            return "User with username " + friendUsername + " does not exist.";
        }
        Friendship friendship = new Friendship(user, friend);
        friendshipRepository.addNewFriendship(friendship);

        Logger.log("Added " + friendUsername + " as a friend.", userUsername);

        return "User " + friendUsername + " has been successfully added to your friends list.";

    }

    public String getAllFriendsList(String userUsername) {
        User user = userRepository.getUserByUsername(userUsername);
        List<Friendship> friendships = friendshipRepository.getAllFriendships(user);
        String friendsList = "Friends: \n";
        for (Friendship f : friendships) {
            if (f.getFirstFriend().equals(user)) {
                friendsList += f.getSecondFriend().getUsername() + "\n";
            } else {
                friendsList += f.getFirstFriend().getUsername() + "\n";
            }
        }

        Logger.log("Viewed friend list.", userUsername);
        return friendsList;
    }

}
