package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 7777;

    public static void main(String[] args) {
        try {
            startClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startClient() throws IOException {
        Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
        System.out.println("Connected to server: " + socket.getInetAddress());

        BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));
        OutputStream outputStream = socket.getOutputStream();

        try {
            while (true) {
                System.out.print("Enter message (type 'exit' to quit): ");
                String userInput = userInputReader.readLine();

                if ("exit".equalsIgnoreCase(userInput)) {
                    break;
                }

                outputStream.write((userInput + "\n").getBytes());
                outputStream.flush();

                // Read and display the server's response
                BufferedReader serverResponseReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String serverResponse = serverResponseReader.readLine();
                System.out.println("Server response: " + serverResponse);
            }
        } finally {
            socket.close();
        }
    }
}
