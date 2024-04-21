package org.sem5Final;

import java.io.*;
import java.net.*;


public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        // Создание и запуск нескольких клиентов
        createMultipleClients(2); // Создаем 2 клиента
    }

    // Метод для создания и запуска нескольких клиентов
    private static void createMultipleClients(int numClients) {
        for (int i = 0; i < numClients; i++) {
            String username = "User" + (i + 1); // Генерируем имя пользователя
            ChatClient client = new ChatClient(username);
            client.start();
        }
    }

    private String username;

    public ChatClient(String username) {
        this.username = username;
    }

    public void start() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println(username + ": Пользователь подключен к чату.");

            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            Thread inputThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = userInput.readLine()) != null) {
                        out.println(username + ": " + message);
                        if (message.equalsIgnoreCase("exit")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            inputThread.start();

            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
            }

            inputThread.join();
            socket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
