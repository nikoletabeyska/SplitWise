package server.services;

import database.model.Friendship;
import database.model.User;
import server.repository.FriendshipRepository;
import server.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final Logger logger;

    public FriendshipService() {
        userRepository = new UserRepository();
        friendshipRepository = new FriendshipRepository();
        logger = new Logger();
    }

    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
        this.logger = new Logger();
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

        logger.log("Added " + friendUsername + " as a friend.", userUsername);

        return "User " + friendUsername + " has been successfully added to your friends list.";

    }

    public String getAllFriendsList(String userUsername) {
        User user = userRepository.getUserByUsername(userUsername);
        List<Friendship> friendships = friendshipRepository.getAllFriendships(user);
        String friendsList = "Friends: \n";
        Set<String> friends = new HashSet<>();
        if (friendships == null) {
            friendsList += "You have no friends.";
        } else {
            for (Friendship f : friendships) {
                if (f.getFirstFriend().equals(user)) {
                    friends.add(f.getSecondFriend().getUsername() + "\n");
                } else {
                    friends.add(f.getFirstFriend().getUsername() + "\n");
                }
            }
            for (String s : friends) {
                friendsList += s;
            }
        }

        logger.log("Viewed friend list.", userUsername);
        return friendsList;

    }
}
