package controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Database {
   public List<String> getMessages() {
      List<String> messages = new ArrayList<>();

      try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/chat", "root", "")) {
         String query = "SELECT content, usename, time FROM messages";
         try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
               String text = resultSet.getString("content");
               String sender = resultSet.getString("usename");
               Timestamp timestamp = resultSet.getTimestamp("time");
               String timeAgo = Server.getTimeAgo(timestamp);
               messages.add(sender + ": " + text + " (" + timeAgo + ")");
            }
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
      return messages;
   }

   public void insertMessage(String username, String message) {
      Timestamp timestamp = new Timestamp(System.currentTimeMillis());

      try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/chat", "root", "")) {
         String query = "INSERT INTO messages (usename, content, time) VALUES (?, ?, ?)";
         try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, message);
            preparedStatement.setTimestamp(3, timestamp);
            preparedStatement.executeUpdate();
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

   public int authenticateUser(String username, String password) {
      try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/chat", "root", "");
            PreparedStatement preparedStatement = connection
                  .prepareStatement("SELECT count(*) FROM users WHERE username=? AND password=?")) {

         preparedStatement.setString(1, username);
         preparedStatement.setString(2, password);

         try (ResultSet resultSet = preparedStatement.executeQuery()) {
            if (resultSet.next()) {
               return resultSet.getInt(1);
            }
         }

      } catch (SQLException e) {
         e.printStackTrace();
      }
      return 0;
   }
}
