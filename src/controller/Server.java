package controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
   private static final int PORT = 3000;
   public Database database;

   public Server() {
      this.database = new Database();
   }

   public static void main(String[] args) {
      new Server().startServer();
   }

   public void startServer() {
      try (ServerSocket serverSocket = new ServerSocket(PORT)) {
         System.out.println("Server started. Listening on port " + PORT);
         ExecutorService executorService = Executors.newCachedThreadPool();

         while (true) {
            Socket clientSocket = serverSocket.accept();
            System.out.println("New client connected: " + clientSocket.getInetAddress());
            executorService.submit(new ClientHandler(clientSocket, this));
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public List<String> getMessages() {
      return database.getMessages();
   }

   public int authenticateUser(String username, String password) {
      return database.authenticateUser(username, password);
   }

   public static String getTimeAgo(Timestamp timestamp) {
      long currentTime = System.currentTimeMillis();
      long timeDifference = currentTime - timestamp.getTime();

      if (timeDifference < 60_000) {
         return "Just now";
      } else if (timeDifference < 3_600_000) {
         return timeDifference / 60_000 + " minute" + (timeDifference < 120_000 ? "" : "s") + " ago";
      } else if (timeDifference < 86_400_000) {
         return timeDifference / 3_600_000 + " hour" + (timeDifference < 7_200_000 ? "" : "s") + " ago";
      } else {
         return timeDifference / 86_400_000 + " day" + (timeDifference < 172_800_000 ? "" : "s") + " ago";
      }
   }

   private static class ClientHandler implements Runnable {
      private Socket clientSocket;
      private Server server;

      public ClientHandler(Socket clientSocket, Server server) {
         this.clientSocket = clientSocket;
         this.server = server;
      }

      @Override
      public void run() {
         try (PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
               BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String username = reader.readLine();
            String password = reader.readLine();

            int authenticationResult = server.authenticateUser(username, password);

            if (authenticationResult == 1) {
               writer.println(true);

               List<String> messages = server.getMessages();
               for (String message : messages) {
                  writer.println(message);
               }

               while (true) {
                  String clientMessage = reader.readLine();
                  if (clientMessage.equals("exit")) {
                     break;
                  }

                  server.database.insertMessage(username, clientMessage); // <-- Fix here
               }

            } else {
               writer.println(false);
            }

         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }
}
