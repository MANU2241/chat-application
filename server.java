import java.io.*;
import java.net.*;
import java.util.*;

public class server {
    private static final int PORT = 12345;
    private static Set<Socket> clientSockets = Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Handle client in a separate thread
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Message received: " + message);
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Client disconnected: " + socket.getInetAddress());
            } finally {
                try {
                    clientSockets.remove(socket);
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clientSockets) {
                for (Socket client : clientSockets) {
                    try {
                        PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                        writer.println(message);
                    } catch (IOException e) {
                        System.err.println("Error broadcasting message: " + e.getMessage());
                    }
                }
            }
        }
    }
}

