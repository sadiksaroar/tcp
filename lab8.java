// Multi-threaded Server and Client Implementation in Java

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Server Class
class MultiThreadedServer {
    private static final int PORT = 5000;
    private static final int MAX_CLIENTS = 5;

    public static void main(String[] args) {
        ExecutorService clientHandlerPool = Executors.newFixedThreadPool(MAX_CLIENTS);
        int clientCount = 0;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started. Waiting for clients...");

            while (clientCount < MAX_CLIENTS) {
                Socket clientSocket = serverSocket.accept();
                clientCount++;
                System.out.println("Client " + clientCount + " connected.");
                clientHandlerPool.execute(new ClientHandler(clientSocket));
            }

            System.out.println("Server reached max client limit. Shutting down...");
            clientHandlerPool.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String received = in.readLine();
                System.out.println("Received: " + received);

                // Convert to uppercase and send back
                String response = received.toUpperCase();
                out.println(response);
                System.out.println("Sent: " + response);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    System.out.println("Client disconnected.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

// Client Class
class Client {
    public static void main(String[] args) {
        final String SERVER_ADDRESS = "localhost";
        final int SERVER_PORT = 5000;

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message = "Hello Server";
            System.out.println("Sending: " + message);
            out.println(message);

            String response = in.readLine();
            System.out.println("Received: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

