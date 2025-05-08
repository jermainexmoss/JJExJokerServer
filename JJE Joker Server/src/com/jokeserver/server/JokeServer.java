package com.jokeserver.server;

import com.jokeserver.controller.MasterController;
import com.jokeserver.service.JokeService;
import com.jokeserver.service.impl.JokeServiceImpl;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JokeServer {
    private static final Logger LOGGER = Logger.getLogger(JokeServer.class.getName());
    private static final int PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;

    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private final MasterController masterController;

    public JokeServer() throws IOException {
        // Initialize service layer
        JokeService jokeService = (JokeService) new JokeServiceImpl();
        masterController = new MasterController(jokeService);

        // Initialize server socket
        serverSocket = new ServerSocket(PORT);
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        LOGGER.log(Level.INFO, "JokeServer started on port {0}", PORT);
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                LOGGER.log(Level.INFO, "New client connected: {0}", clientSocket.getInetAddress().getHostAddress());
                threadPool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error accepting client connections", e);
        } finally {
            shutdown();
        }
    }

    private void shutdown() {
        try {
            threadPool.shutdown();
            serverSocket.close();
            LOGGER.log(Level.INFO, "JokeServer shut down");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error shutting down server", e);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error initializing streams for client: {0}", clientSocket.getInetAddress().getHostAddress());
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    Object request = in.readObject();
                    if (request instanceof Request) {
                        Response response = masterController.handleRequest((Request) request);
                        sendResponse(response);
                    } else {
                        sendResponse(new Response("ERROR", "Invalid request format"));
                    }
                }
            } catch (EOFException e) {
                LOGGER.log(Level.INFO, "Client disconnected: {0}", clientSocket.getInetAddress().getHostAddress());
            } catch (IOException | ClassNotFoundException e) {
                LOGGER.log(Level.SEVERE, "Error handling client request", e);
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Error closing client socket", e);
                }
            }
        }

        private void sendResponse(Response response) {
            try {
                out.writeObject(response);
                out.flush();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error sending response to client", e);
            }
        }
    }

    // Request and Response classes remain the same as before
    public static class Request implements Serializable {
        private final String command;
        private final Object payload;

        public Request(String command, Object payload) {
            this.command = command;
            this.payload = payload;
        }

        public String getCommand() {
            return command;
        }

        public Object getPayload() {
            return payload;
        }
    }

    public static class Response implements Serializable {
        private final String status;
        private final Object data;

        public Response(String status, Object data) {
            this.status = status;
            this.data = data;
        }

        public String getStatus() {
            return status;
        }

        public Object getData() {
            return data;
        }
    }

    public static void main(String[] args) {
        try {
            JokeServer server = new JokeServer();
            server.start();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start JokeServer", e);
        }
    }
}