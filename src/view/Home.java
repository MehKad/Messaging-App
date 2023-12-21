package view;

import controller.*;

import java.awt.*;
import javax.swing.*;
import java.util.List;
import java.awt.event.*;

public class Home extends JFrame {

   private JTextArea dataArea;
   private JTextField inputField;
   private JButton sendButton;
   private String username;
   private Server server;

   public Home(String username, Server server) {
      this.username = username;
      this.server = server;
      initComponents();
      this.setTitle("Simple GUI");
      this.setResizable(false);
      this.setVisible(true);
      this.setSize(500, 600);
   }

   private void initComponents() {
      inputField = new JTextField();
      inputField.setPreferredSize(new Dimension(200, 30));
      sendButton = new JButton("Send");
      dataArea = new JTextArea(10, 30);
      dataArea.setEditable(false);

      setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      setLayout(new BorderLayout());

      JPanel topPanel = new JPanel(new FlowLayout());
      JPanel bottomPanel = new JPanel(new BorderLayout());

      topPanel.add(inputField);
      topPanel.add(sendButton);

      bottomPanel.add(new JScrollPane(dataArea), BorderLayout.CENTER);

      add(topPanel, BorderLayout.NORTH);
      add(bottomPanel, BorderLayout.CENTER);

      sendButton.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  sendActionPerformed(e);
               }
            });

      List<String> messages = server.getMessages();
      for (String message : messages) {
         dataArea.append(message + "\n");
      }

      pack();

      SwingUtilities.invokeLater(() -> {
         setVisible(true);
      });
   }

   private void sendActionPerformed(ActionEvent evt) {
      String data = inputField.getText();
      server.database.insertMessage(username, data);

      refreshMessages();

      inputField.setText("");
   }

   private void refreshMessages() {
      dataArea.setText("");

      List<String> messages = server.getMessages();
      for (String message : messages) {
         dataArea.append(message + "\n");
      }
   }

}
