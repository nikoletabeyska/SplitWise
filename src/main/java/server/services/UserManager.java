package server.services;

import com.mysql.cj.conf.ConnectionUrlParser;
import database.model.User;
import server.repository.UserRepository;


import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Boolean> loginUser(String username, String password) {
        Map<String, Boolean> result = new HashMap<>();
        if (!isValidString(username) || !isValidString(password)) {
            result.put("Invalid input. Username and password are required.", false);
            return result;
        }

        User user = userRepository.getUserByUsername(username);

        if (user == null || !user.getPassword().equals(password)) {
            result.put("Invalid username or password. Please try again", false);
            return  result;
        }
        logger.log("User logged in", username);
        result.put("Login successful! Welcome, " + username + "!", true);

        return result;
    }

    public static boolean isValidString(String str) {
        return !(str == null || str.trim().isEmpty());
    }


}
