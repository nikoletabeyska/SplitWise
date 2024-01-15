package server.services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    private static final String OPERATION_LOG_FILE = "operations_log.txt";
    private static final String ERROR_LOG_FILE = "error_log.txt";
    private static final int MAX_BUFFER_SIZE = 100;
    private final StringBuilder logBuffer = new StringBuilder();
    private final StringBuilder errorBuffer = new StringBuilder();

    public synchronized void log(String message, String user) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String logEntry = '[' + timestamp + ']' + " " + message + " by " + user;

        logBuffer.append(logEntry).append("\n");
        if (logBuffer.length() >= MAX_BUFFER_SIZE) {
            flushBuffer(OPERATION_LOG_FILE, logBuffer);
        }
    }

    public synchronized void logError(String errorMessage, Exception exception) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //String errorEntry = "[" + timestamp + "] ERROR by " + username + ": " + errorMessage;
        String errorEntry = "";
        if (exception != null) {
            errorEntry = "Exception: " + exception.toString() + "\n";
        }
        errorBuffer.append(errorEntry).append("\n");

        // Flush error buffer if it reaches the maximum size
        if (errorBuffer.length() >= MAX_BUFFER_SIZE) {
            flushBuffer(ERROR_LOG_FILE, errorBuffer);
        }
    }

    private void flushBuffer(String filename, StringBuilder buffer) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.print(buffer.toString());
            buffer.setLength(0); // Clear the buffer
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }

}
