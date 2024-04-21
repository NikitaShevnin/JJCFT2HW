package org.sem5Final;

import java.io.*;
import java.net.*;
import java.util.Set;
import java.util.HashSet;
import java.io.PrintWriter;


public class ChatServer {
    private static final int PORT = 12345;
    private static Set<PrintWriter> clients = new HashSet<PrintWriter>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Сервер запущен на порту " + PORT);
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                clients.add(out);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("/private")) {
                        sendPrivateMessage(message);
                    } else {
                        broadcast(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    clients.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            for (PrintWriter client : clients) {
                client.println(message);
            }
        }

        private void sendPrivateMessage(String message) {
            String[] parts = message.split(" ", 3);
            String recipient = parts[1];
            String content = parts[2];

            for (PrintWriter client : clients) {
                if (client != out && client.toString().equals(recipient)) {
                    client.println("Личное сообщение от " + socket.toString() + ": " + content);
                }
            }
        }
    }
}
