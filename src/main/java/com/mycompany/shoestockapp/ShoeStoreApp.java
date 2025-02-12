/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shoestockapp;
//import com.mycompany.shoestockapp.Dashboard.*;


import com.mycompany.shoestockapp.Authentication.RegistrationForm;
import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShoeStoreApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private LoginForm loginForm;
    private RegistrationForm registrationForm;
    private Dashboard dashboard;
    private ProductList productList;

    public ShoeStoreApp() {
        setTitle("Shoe Store Stock Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        productList = new ProductList(this, "");
        loginForm = new LoginForm(this);
        registrationForm = new RegistrationForm(this);
        dashboard = new Dashboard(this);

        mainPanel.add(productList, "productList");
        mainPanel.add(loginForm, "login");
        mainPanel.add(registrationForm, "registration");
        mainPanel.add(dashboard, "dashboard");

        add(mainPanel);

        cardLayout.show(mainPanel, "login");

        setVisible(true);
    }

    public void showDashboard() {
        cardLayout.show(mainPanel, "dashboard");
        dashboard.loadCategories();
    }

    public void showLoginForm() {
        cardLayout.show(mainPanel, "login");
    }

    public void showRegistrationForm() {
        cardLayout.show(mainPanel, "registration");
    }

    public void showProductList(String category) {
        productList = new ProductList(this, category); // Create new instance
        mainPanel.add(productList, "productList");
        cardLayout.show(mainPanel, "productList");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShoeStoreApp());
    }

    class LoginForm extends JPanel {
        private ShoeStoreApp parent;
        private JTextField emailField;
        private JPasswordField passwordField;
        private JButton submitButton;
        private JButton registerButton;

        public LoginForm(ShoeStoreApp parent) {
            this.parent = parent;
            setLayout(new BorderLayout());
            setBackground(Color.LIGHT_GRAY);

            JLabel titleLabel = new JLabel("Login");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(titleLabel, BorderLayout.NORTH);

            JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
            inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
            inputPanel.setBackground(Color.LIGHT_GRAY);

            JLabel emailLabel = new JLabel("Email:");
            emailField = new JTextField();
            JLabel passwordLabel = new JLabel("Password:");
            passwordField = new JPasswordField();

            inputPanel.add(emailLabel);
            inputPanel.add(emailField);
            inputPanel.add(passwordLabel);
            inputPanel.add(passwordField);

            add(inputPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 50, 20, 50));
            buttonPanel.setBackground(Color.LIGHT_GRAY);

            submitButton = new JButton("Submit");
            submitButton.addActionListener(e -> {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (validateLoginInput(email, password)) {
                    try {
                        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "");
                        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE email = ? AND password = ?");
                        stmt.setString(1, email);
                        stmt.setString(2, password);
                        ResultSet rs = stmt.executeQuery();

                        if (rs.next()) {
                            JOptionPane.showMessageDialog(LoginForm.this, "Login Successful!");
                            parent.showDashboard();
                        } else {
                            JOptionPane.showMessageDialog(LoginForm.this, "Invalid email or password.");
                        }

                        conn.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(LoginForm.this, "Database error: " + ex.getMessage());
                    }
                }
            });
            buttonPanel.add(submitButton);

            registerButton = new JButton("Register");
            registerButton.addActionListener(e -> parent.showRegistrationForm());
            buttonPanel.add(registerButton);

            add(buttonPanel, BorderLayout.SOUTH);
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
}
