package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class Server {
    public static final int PORT = 7777;
    private static final String SERVER_HOST = "localhost";

    public static void main(String[] args) throws IOException {
        startServer();
    }

    private static void startServer() throws IOException {

        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Server is running...");

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(socket);
                //start new thread for every client
                new Thread(clientHandler).start();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (serverSocket != null) {
                        serverSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received from client: " + message);
                // Echo the message back to the client
                clientSocket.getOutputStream().write((message + "\n").getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}




