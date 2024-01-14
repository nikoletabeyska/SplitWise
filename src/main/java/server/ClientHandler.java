package server;

import server.repository.UserRepository;
import server.services.ExpensesService;
import server.services.FriendshipService;
import server.services.GroupService;
import server.services.UserManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private PrintWriter writer;
    private BufferedReader reader;

    private static UserManager userManager;
    private static FriendshipService friendshipService;
    private static GroupService groupService;
    private static ExpensesService expensesService;
    private boolean isLoggedIn;
    private String userUsername;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.userManager = new UserManager();
        this.friendshipService = new FriendshipService();
        this.groupService = new GroupService();
        this.expensesService = new ExpensesService();
        this.isLoggedIn = false;
        this.userUsername = null;
    }

    @Override
    public void run() {
        try {
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Handle client commands
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                String response = handleCommand(inputLine);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private String handleCommand(String command) {
        // Implement command handling logic here
        // You may need to parse the command and interact with other server components
        // For example, you can use UserManager, GroupManager, etc.
        String[] parts = command.split("\\s+");

        if (parts.length < 1) {
            return "Invalid command format";
        }

        String commandType = parts[0];

        switch (commandType) {
            case "register":
                return userManager.registerUser(parts[1], parts[2], userUsername);
            case "login":
                return userManager.loginUser(parts[1], parts[2], isLoggedIn);
            case "add-friend":
                if (isLoggedIn) {
                    return friendshipService.addFriend(userUsername, parts[1]);
                } else {
                    return "This command requires log in.";
                }
                // return handleAddFriend(parts);
            case "create-group":
                if (isLoggedIn) {
                    if(parts.length >=3 ) return "Not enought parameters to create a group";
                    //Should always have a following param
                    int nameParamIndex = 1;
                    //Name should be a valid not null strin
                    if(parts[nameParamIndex] != null && parts[nameParamIndex].trim().isEmpty()== false) {
                        ArrayList<String> usersToAdd = new ArrayList<>();
                        for (int i = 2; i < parts.length; i++) {
                            usersToAdd.add(parts[i]);
                        }
                        //Add self to group
                        usersToAdd.add(this.userUsername);
                        return groupService.createGroup(parts[nameParamIndex],usersToAdd);
                    }
                    else
                    {
                        return "Invalid Name";
                    }
                } else {
                    return "This command requires log in.";
                }
                //  return handleCreateGroup(parts);
            case "split":
                //TODO expects
                if(parts.length <4) return "Not enough parameters";
                if(isLoggedIn)
                    expensesService.split(this.userUsername,parts[3],Double.valueOf(parts[1]),parts[2]);

            case "split-group":
                if(parts.length <4) return "Not enough parameters";
                if(isLoggedIn)
                    return expensesService.splitGroup(this.userUsername, Arrays.copyOfRange(parts,3,parts.length),Double.valueOf(parts[1]),parts[2]);
                return "This command requires log in.";
            case "get-status":
                //  return handleGetStatus(parts);
                // Add more cases for other commands as needed
            default:
                return "Unknown command: " + commandType;
        }

        // For simplicity, let's just echo the received command back to the client
        //System.out.println("Received command from client: " + command);
        //out.println("Server response: " + command);
    }

    private void closeResources() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
