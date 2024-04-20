package server;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import server.services.Logger;

import java.awt.image.DataBufferDouble;
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
    private static Map<SocketChannel, ClientHandler> clients = new HashMap<>();
    private static final String PERSISTENCE_UNIT_NAME = "SplitWisePersistenceUnit";
    private static final int BUFFER_CAPACITY = 1024;

    private static EntityManagerFactory entityManagerFactory = null;
    //static entity manager used for a global connection with the database
    public static EntityManager manager = null;
    private static ClassesInitializer initializer;

    public static void main(String[] args) throws IOException {
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        manager = entityManagerFactory.createEntityManager();
        initializer = ClassesInitializer.getInstance(manager);

        startServerChannel();
    }

    public static void startServerChannel() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            ServerSocket serverSocket = serverSocketChannel.socket();
            serverSocket.bind(new InetSocketAddress(SERVER_HOST, PORT));
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleAcceptableEvent(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        clients.put(clientChannel, new ClientHandler(initializer));

        System.out.println("Accepted connection from " + clientChannel.getRemoteAddress());
    }

    private static void handleReadableEvent(SocketChannel clientChannel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
        int bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            clientChannel.close();
            System.out.println("Connection closed by client.");
        } else if (bytesRead > 0) {
            buffer.flip();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            String receivedMessage = new String(data);

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
            }
        } catch (IOException e) {
            clientChannel.close();
            System.out.println("Client disconnected");
        }
    }
}



