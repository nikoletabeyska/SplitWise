package server.services;

import database.model.User;
import server.repository.UserRepository;

public class UserManager {

    private final UserRepository userRepository;
    private Logger logger;

    public UserManager() {
        userRepository = new UserRepository();
        logger = new Logger();
    }

    public UserManager(UserRepository userRepository, Logger logger) {
        this.userRepository = userRepository;
        this.logger = logger;
    }

    public String registerUser(String username, String password, String userUsername) {
        if (!isValidString(username) || !isValidString(password)) {
            return "Invalid input. Username and password are required.";
        }

        // Check if the username is already taken
        if (userRepository.getUserByUsername(username) != null) {
            return "Username is already taken. Please choose another one.";
        }

        User newUser = new User(username, password);
        userRepository.createUser(newUser);
        userUsername = username;

        logger.log("User registered", username);
        return "User registered successfully!";

    }

    public String loginUser(String username, String password, boolean isLoggedIn) {
        if (!isValidString(username) || !isValidString(password)) {
            return "Invalid input. Username and password are required.";
        }

        User user = userRepository.getUserByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            return "Invalid username or password. Please try again.";
        }

        isLoggedIn = true;
        logger.log("User logged in", username);
        return "Login successful! Welcome, " + username + "!";
    }

    public static boolean isValidString(String str) {
        return !(str == null || str.trim().isEmpty());
    }


}
