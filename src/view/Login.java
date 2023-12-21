package view;

import javax.swing.*;

import controller.Server;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;

public class Login extends JFrame {
   private JTextField usernameField;
   private JPasswordField passwordField;
   private JButton loginButton;

   public Login() {
      initComponents();
      this.setTitle("Login Page");
      this.setResizable(false);
      this.setVisible(true);
   }

   private void initComponents() {
      usernameField = new JTextField();
      usernameField.setPreferredSize(new Dimension(200, 30));

      passwordField = new JPasswordField();
      passwordField.setPreferredSize(new Dimension(200, 30));

      loginButton = new JButton("Login");

      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new FlowLayout());

      JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

      panel.add(createLabelAndFieldPanel("Username:", usernameField));
      panel.add(createLabelAndFieldPanel("Password:", passwordField));
      panel.add(loginButton);

      add(panel);

      loginButton.addActionListener(
            new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                  loginActionPerformed(e);
               }
            });
      pack();
   }

   private JPanel createLabelAndFieldPanel(String labelText, JComponent field) {
      JPanel labelAndFieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
      JLabel label = new JLabel(labelText);
      label.setPreferredSize(new Dimension(80, 30));
      labelAndFieldPanel.add(label);
      labelAndFieldPanel.add(field);
      return labelAndFieldPanel;
   }

   private void loginActionPerformed(ActionEvent e) {
      String username = usernameField.getText();
      char[] passwordChars = passwordField.getPassword();
      String password = new String(passwordChars);

      passwordField.setText("");

      new Thread(() -> {
         try (Socket socket = new Socket("localhost", 3000);
               PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
               BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println(username);
            writer.println(password);

            boolean result = Boolean.parseBoolean(reader.readLine());

            SwingUtilities.invokeLater(() -> {
               if (result) {
                  Server server = new Server();
                  Home home = new Home(username, server);
                  home.setVisible(true);
                  dispose();
               } else {
                  JOptionPane.showMessageDialog(Login.this, "Login failed", "Error", JOptionPane.ERROR_MESSAGE);
               }
            });

         } catch (IOException evt) {
            evt.printStackTrace();
         }
      }).start();
   }

}
