package server;

import jakarta.persistence.EntityManager;
import server.repository.*;
import server.services.ExpensesService;
import server.services.FriendshipService;
import server.services.GroupService;
import server.services.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.security.SecureRandom;
import org.mindrot.jbcrypt.BCrypt;

public class ClientHandler {

    private PrintWriter writer;
    private BufferedReader reader;

    private static UserManager userManager;
    private static FriendshipService friendshipService;
    private static GroupService groupService;
    private static ExpensesService expensesService;
    private boolean isLoggedIn;
    private String userUsername;

    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private FriendshipRepository friendshipRepository;
    private TransactionRepository transactionRepository;

    public ClientHandler(EntityManager manager) {

        userRepository= new UserRepository(manager);
        groupRepository= new GroupRepository(manager);
        friendshipRepository= new FriendshipRepository(manager);
        transactionRepository= new TransactionRepository(manager);

        this.userManager = new UserManager(userRepository);
        this.friendshipService = new FriendshipService(userRepository,friendshipRepository);
        this.groupService = new GroupService(userRepository,groupRepository);
        this.expensesService = new ExpensesService(userRepository,groupRepository,transactionRepository);
        this.isLoggedIn = false;
        this.userUsername = null;
    }

//    @Override
//    public void run() {
//        try {
//            writer = new PrintWriter(clientSocket.getOutputStream(), true);
//            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//            // Handle client commands
//            String inputLine;
//            while ((inputLine = reader.readLine()) != null) {
//                String response = handleCommand(inputLine);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            closeResources();
//        }
//    }
    /*@Override
    public void run()
    {

    }*/

    public String handleCommand(String command) {
        String[] parts = command.split("\\s+");

        if (parts.length < 1) {
            return "Invalid command format";
        }

        String commandType = parts[0];

        switch (commandType) {
            case "register":
                if (parts.length != 3) {
                    return "Not enough parameters to register. Username and password required.";
                }
                // Generate a random salt for each user
                String hashedPassword = hashPassword(parts[2]);
                return userManager.registerUser(parts[1], hashedPassword, userUsername);
                //return userManager.registerUser(parts[1], hashedPassword, salt, userUsername);
                //return userManager.registerUser(parts[1], parts[2], userUsername);
            case "login":
                //.attach(user);
                if (parts.length != 3) {
                    return "Not enough parameters to register. Username and password required.";
                }
                Map<String,Boolean> result = userManager.loginUser(parts[1],parts[2]);
                Map.Entry<String, Boolean> singleEntry = result.entrySet().iterator().next();
                String key = singleEntry.getKey();
                Boolean value = singleEntry.getValue();
                if (value) {
                    this.isLoggedIn = true;
                    this.userUsername = parts[1];
                }
                return key;
            case "add-friend":
                if (isLoggedIn) {
                    return friendshipService.addFriend(userUsername, parts[1]);
                } else {
                    return "This command requires log in.";
                }
            case "create-group":
                if (isLoggedIn) {
                    if (parts.length <= 3) return "Not enought parameters to create a group";
                    //Should always have a following param
                    int nameParamIndex = 1;
                    //Name should be a valid not null strin
                    if (parts[nameParamIndex] != null && parts[nameParamIndex].trim().isEmpty()== false) {
                        ArrayList<String> usersToAdd = new ArrayList<>();
                        for (int i = 2; i < parts.length; i++) {
                            usersToAdd.add(parts[i]);
                        }
                        //Add self to group
                        usersToAdd.add(this.userUsername);
                        return groupService.createGroup(parts[nameParamIndex], usersToAdd, this.userUsername);
                    }
                    else {
                        return "Invalid Name";
                    }
                } else {
                    return "This command requires log in.";
                }
            case "split":
                if (parts.length < 4) return "Not enough parameters";
                if (isLoggedIn) {
                    return expensesService.split(this.userUsername, parts[2], Double.valueOf(parts[1]), parts[3]);
                } else {
                    return "This command requires log in.";
                }
            case "split-group":
                if (parts.length < 4) return "Not enough parameters";
                if (isLoggedIn) {
                    return expensesService.splitGroup(this.userUsername, parts[1],
                        Double.valueOf(parts[2]), parts[3]);
                } else {
                    return "This command requires log in.";
                }
            case "get-status":
                if (isLoggedIn) {
                    return expensesService.getStatus(this.userUsername);
                } else {
                    return "This command requires log in.";
                }
            default:
                return "Unknown command: " + commandType;
        }

        // For simplicity, let's just echo the received command back to the client
        //System.out.println("Received command from client: " + command);
        //out.println("Server response: " + command);
    }

    private static String hashPassword(String password) {
        // Generate a random salt and hash the password with BCrypt
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

}
