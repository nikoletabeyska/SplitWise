package server.services;

import com.mysql.cj.conf.ConnectionUrlParser;
import database.model.User;
import org.mindrot.jbcrypt.BCrypt;
import server.RepositoryImplementationMapping;
import server.Server;
import server.repository.UserRepository;


import java.util.HashMap;
import java.util.Map;

public class UserManager {

    private final UserRepository userRepository;

    public UserManager() {
        this.userRepository = (UserRepository) RepositoryImplementationMapping.get(UserRepository.class);
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

        Logger.log("User registered", username);
        return "User registered successfully! Please login using your credentials.";

    }

    public Map<String, Boolean> loginUser(String username, String password) {
        Map<String, Boolean> result = new HashMap<>();
        if (!isValidString(username) || !isValidString(password)) {
            result.put("Invalid input. Username and password are required.", false);
            return result;
        }

        User user = userRepository.getUserByUsername(username);

        try
        {
            if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
                result.put("Invalid username or password. Please try again", false);
                return  result;
            }
        }
        catch (Exception e)
        {
            result.put("Invalid username or password. Please try again", false);
            return  result;
        }
        Logger.log("User logged in", username);
        result.put("Login successful! Welcome, " + username + "!", true);

        return result;
    }

    public static boolean isValidString(String str) {
        return !(str == null || str.trim().isEmpty());
    }


}
