package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;

public class ClientHandler {

    private PrintWriter writer;
    private BufferedReader reader;
    private boolean isLoggedIn;
    private String userUsername;
    private ClassesInitializer initializer;
    private static String HELP_COMMANDS;

    static {
        HELP_COMMANDS = """
            You can use the following commands:
            - login <username> <password>
            - register <name> <username> <password>
            - add-friend <username>
            - create-group <group_name> <username> <username> ... <username>
            - split <amount> <username> <reason_for_payment>
            - split-group <group_name> <amount> <reason_for_payment>
            - get-status (check all your obligations with this command)
            - pay <value> <username>
            - pay-group <value> <group-name>
            """;
    }

    public ClientHandler(ClassesInitializer initializer) {
        this.isLoggedIn = false;
        this.userUsername = null;
        this.initializer = initializer;
    }

    public String handleCommand(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length < 1) {
            return "Invalid command format";
        }

        String commandType = parts[0];

        switch (commandType) {
            case "help":
                return HELP_COMMANDS;
            case "register":
                if (parts.length != 3) {
                    return "Not enough parameters to register. Username and password required.";
                }
                String hashedPassword = hashPassword(parts[2]);
                return initializer.getUserManager().registerUser(parts[1], hashedPassword, userUsername);
            case "login":
                if (parts.length != 3) {
                    return "Not enough parameters to login. Username and password required.";
                }
                Map<String, Boolean> result = initializer.getUserManager().loginUser(parts[1], parts[2]);
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
                    return initializer.getFriendshipService().addFriend(userUsername, parts[1]);
                } else {
                    return "This command requires log in.";
                }
            case "show-friends":
                if (isLoggedIn) {
                    return initializer.getFriendshipService().getAllFriendsList(userUsername);
                } else {
                    return "This command requires log in.";
                }
            case "create-group":
                if (isLoggedIn) {
                    if (parts.length < 3) return "Not enought parameters to create a group";
                    //Should always have a following param
                    int nameParamIndex = 1;
                    //Name should be a valid not null string
                    if (parts[nameParamIndex] != null && parts[nameParamIndex].trim().isEmpty()== false) {
                        ArrayList<String> usersToAdd = new ArrayList<>();
                        for (int i = 2; i < parts.length; i++) {
                            usersToAdd.add(parts[i]);
                        }
                        //Add self to group
                        usersToAdd.add(this.userUsername);
                        return initializer.getGroupService().createGroup(parts[nameParamIndex], usersToAdd, this.userUsername);
                    } else {
                        return "Invalid Name";
                    }
                } else {
                    return "This command requires log in.";
                }
            case "split":
                if (parts.length < 4) return "Not enough parameters";
                if (isLoggedIn) {
                    return initializer.getExpensesService().split(this.userUsername, parts[2], Double.valueOf(parts[1]), parts[3]);
                } else {
                    return "This command requires log in.";
                }
            case "split-group":
                if (parts.length < 4) return "Not enough parameters";
                if (isLoggedIn) {
                    return initializer.getExpensesService().splitGroup(this.userUsername, parts[1],
                        Double.valueOf(parts[2]), parts[3]);
                } else {
                    return "This command requires log in.";
                }
            case "get-status":
                if (isLoggedIn) {
                    return initializer.getExpensesService().getStatus(this.userUsername);
                } else {
                    return "This command requires log in.";
                }
            case "logout":
                if (isLoggedIn) {
                    this.isLoggedIn = false;
                    this.userUsername = null;
                    return "You have successfully logged out.";
                } else {
                    return "This command requires log in.";
                }
            case "pay": // pay 10 miki
                if (parts.length != 3) return "Wrong count parameters";
                if (isLoggedIn) {
                    return initializer.getExpensesService().pay(this.userUsername, Double.valueOf(parts[1]), parts[2]);
                } else {
                    return "This command requires log in.";
                }
            case "pay-group": // pay 10 koko
                if (parts.length != 3) return "Wrong count parameters";
                if (isLoggedIn) {
                    return initializer.getExpensesService().payGroup(this.userUsername, Double.valueOf(parts[1]), parts[2]);
                } else {
                    return "This command requires log in.";
                }
            default:
                return "Unknown command: " + commandType;
        }

    }

    private static String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }

}
