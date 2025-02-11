/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shoestockapp;

/**
 *
 * @author Kapnang
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationForm extends JPanel {
    private ShoeStoreApp parent;
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> userRoleComboBox;
    private JButton registerButton;
    private JButton loginButton;

    public RegistrationForm(ShoeStoreApp parent) {
        this.parent = parent;

        setLayout(new BorderLayout());
        setBackground(Color.LIGHT_GRAY);

        JLabel titleLabel = new JLabel("Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        inputPanel.setBackground(Color.LIGHT_GRAY);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Small border

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();
        emailField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Small border

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Small border

        JLabel userRoleLabel = new JLabel("User Role:");
        userRoleComboBox = new JComboBox<>(new String[]{"Admin", "Staff"});
        userRoleComboBox.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5)); // Small border


        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(emailLabel);
        inputPanel.add(emailField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);
        inputPanel.add(userRoleLabel);
        inputPanel.add(userRoleComboBox);

        add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
        buttonPanel.setBackground(Color.LIGHT_GRAY);

        registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                String userRole = (String) userRoleComboBox.getSelectedItem();

                if (validateRegistrationInput(name, email, password)) {
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "");
                        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)");
                        stmt.setString(1, name);
                        stmt.setString(2, email);
                        stmt.setString(3, password);
                        stmt.setString(4, userRole);
                        stmt.executeUpdate();

                        JOptionPane.showMessageDialog(RegistrationForm.this, "Registration Successful!");
                        parent.showLoginForm();

                        conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(RegistrationForm.this, "Database error: " + ex.getMessage());
                    }
                }
            }
        });
        buttonPanel.add(registerButton);

        loginButton = new JButton("Back to Login");
        loginButton.addActionListener(e -> parent.showLoginForm());
        buttonPanel.add(loginButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validateRegistrationInput(String name, String email, String password) {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return false;
        }

        if (!isValidName(name)) {
            JOptionPane.showMessageDialog(this, "Invalid name format. Only letters and spaces are allowed.");
            return false;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return false;
        }

        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.");
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

    private boolean isValidName(String name) {
        String regex = "^[a-zA-Z\\s]+$"; // Allows only letters and spaces
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }
}