package server.services;

import database.model.Friendship;
import database.model.User;
import server.repository.FriendshipRepository;
import server.repository.UserRepository;

import java.util.List;

public class FriendshipService {
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public FriendshipService() {
        userRepository = new UserRepository();
        friendshipRepository = new FriendshipRepository();
    }

    public FriendshipService(UserRepository userRepository, FriendshipRepository friendshipRepository) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
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

        return "User " + friendUsername + " has been successfully added to your friends list.";

    }

    public String getAllFriendsList(String userUsername) {
        User user = userRepository.getUserByUsername(userUsername);
        List<Friendship> friendships = friendshipRepository.getAllFriendships(user);
        String friendsList = "";
        boolean isFirst = false;

        /*
        for (Friendship f : friendships) {
            isFirst = false;
            if (f.getFirstFriend().equals(user)) {
                isFirst = true;
            }
            if (f.getAmountOwnedByFirstToSecond() > 0) {
                friendsList +=
                    "* " + f.getSecondFriend().getUsername() + ": " + getOweMessage(isFirst, f.getAmountOwnedByFirstToSecond()) + "\n";
            }
            if (f.getAmountOwnedBySecondToFirst() > 0) {
                friendsList +=
                    "* " + f.getSecondFriend().getUsername() + ": " +  getOweMessage(!isFirst, f.getAmountOwnedBySecondToFirst()) + "\n";
            }
        }

         */

        return friendsList;
    }

    private String getOweMessage(boolean isOwe, double amount) {
        if (isOwe) {
            return "You owe " + amount + " LV";
        } else {
            return "Owes you " + amount + " LV";
        }
    }


}
