package com.snakegame.server;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {
    private static final Map<String, Integer> scores = new ConcurrentHashMap<>();
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                System.out.println("Received: " + clientMessage);

                if (clientMessage.startsWith("SCORE")) {
                    String[] parts = clientMessage.split(" ");
                    String playerName = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    scores.put(playerName, score);
                    out.println("Score received for " + playerName);
                } else if (clientMessage.equals("GET_SCORES")) {
                    String scoresList = scores.entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + ": " + entry.getValue())
                            .collect(Collectors.joining(", "));
                    out.println("Scores: " + scoresList);
                } else {
                    out.println("Unknown command");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
