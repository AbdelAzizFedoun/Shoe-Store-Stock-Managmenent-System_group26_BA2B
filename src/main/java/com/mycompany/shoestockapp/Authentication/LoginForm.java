/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shoestockapp.Authentication;


import com.mycompany.shoestockapp.ShoeStoreApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginForm extends JPanel {
    private ShoeStoreApp parent;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton submitButton;
    private JButton registerButton;

    public LoginForm(ShoeStoreApp parent) {
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));
        setPreferredSize(new Dimension(200, 200));

        JLabel titleLabel = new JLabel("ShoeStock");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        centerPanel.setBackground(new Color(255, 255, 255));
        centerPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        emailField = new JTextField("Email or Phone");
        emailField.setForeground(Color.GRAY);
        emailField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (emailField.getText().equals("Email or Phone")) {
                    emailField.setText("");
                    emailField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent evt) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText("Email or Phone");
                    emailField.setForeground(Color.GRAY);
                }
            }
        });
        emailField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailField.setMaximumSize(new Dimension(100, 5));
        centerPanel.add(emailField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        passwordField = new JPasswordField("Password");
        passwordField.setForeground(Color.GRAY);
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent evt) {
                if (new String(passwordField.getPassword()).equals("Password")) {
                    passwordField.setText("");
                    passwordField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent evt) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setText("Password");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
        passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setMaximumSize(new Dimension(100, 5));
        centerPanel.add(passwordField);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        submitButton = new JButton("Log In");
        submitButton.setBackground(new Color(0, 123, 255));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        submitButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            if (validateLoginInput(email, password)) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "")) {
                    String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, email);
                        stmt.setString(2, password);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                JOptionPane.showMessageDialog(LoginForm.this, "Login Successful!");
                                parent.showDashboard();
                            } else {
                                JOptionPane.showMessageDialog(LoginForm.this, "Invalid email or password.");
                            }
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(LoginForm.this, "Database error: " + ex.getMessage());
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(LoginForm.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        buttonPanel.add(submitButton);

        registerButton = new JButton("Create New Account");
        registerButton.setForeground(Color.GRAY);
        registerButton.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        registerButton.addActionListener(e -> parent.showRegistrationForm());
        buttonPanel.add(registerButton);

        centerPanel.add(buttonPanel);

        add(centerPanel, BorderLayout.CENTER);
    }

    private boolean validateLoginInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}