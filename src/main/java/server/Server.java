package server;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import server.services.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;


public class Server {
    public static final int PORT = 7777;
    private static final String SERVER_HOST = "localhost";
    private static Map <SocketChannel, ClientHandler> clients = new HashMap<>();
    private static final String PERSISTENCE_UNIT_NAME = "SplitWisePersistenceUnit";
    private static EntityManagerFactory entityManagerFactory = null;
    private static EntityManager manager = null;
    public static Logger logger = new Logger();

    public static void main(String[] args) throws IOException {
        //startServer();
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        manager = entityManagerFactory.createEntityManager();
        startServerChannel();
    }
    public static void startServerChannel() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(SERVER_HOST,PORT));
            serverSocketChannel.configureBlocking(false);

            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("Server started on port 7777...");

            while (true) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

                while (keyIterator.hasNext()) {
                    SelectionKey key = keyIterator.next();
                    keyIterator.remove();

                    if (key.isAcceptable()) {
                        handleAcceptableEvent(serverSocketChannel, selector);
                    } else if (key.isReadable()) {
                        handleReadableEvent((SocketChannel) key.channel());
                    }
                }
            }
        }
        catch (IOException e) {
            //e.printStackTrace();
        }
    }

    private static void handleAcceptableEvent(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        clients.put(clientChannel,new ClientHandler(manager));

        System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private static void handleReadableEvent(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            System.out.println("Connection closed by client.");
        } else if (bytesRead > 0) {
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            String receivedMessage = new String(data);
            if(receivedMessage.equals("get-status")){
                System.out.println("Neshto");
            }
            //TODO fix bug
            //Response WORKING
            //System.out.println("Received message from client: " + receivedMessage);
           // sendResponse(clientChannel,receivedMessage,buffer);
            //Respone NOT WORKING
            ClientHandler handlerForConnection = clients.get(clientChannel);
            sendResponse(clientChannel, handlerForConnection.handleCommand(receivedMessage), buffer);


        }
    }

    private static void sendResponse(SocketChannel clientChannel, String message, ByteBuffer buffer) throws IOException {
        buffer.clear();
        buffer.put(new String(message).getBytes());
        buffer.flip();

        try {
            while (buffer.hasRemaining()) {
                clientChannel.write(buffer);
                System.out.println("Buffer content: " + Arrays.toString(buffer.array()));
            }
        } catch (IOException e) {
            // Handle the exception (e.g., client disconnected)
            clientChannel.close();
            System.out.println("Client disconnected");
        }
    }

    private static void startServer() throws IOException {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        ServerSocket serverSocket = serverSocketChannel.socket();
        serverSocket.bind(new InetSocketAddress(SERVER_HOST,PORT));

        System.out.println("Server is running...");


        while (true) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                ClientHandler clientHandler = new ClientHandler(manager);
                //start new thread for every client
                //new Thread(clientHandler).start();

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



