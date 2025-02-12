/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shoestockapp.Interfaces;


import com.mycompany.shoestockapp.ShoeStoreApp;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Dashboard extends JPanel {
    private ShoeStoreApp parent;
    private JList<String> categoryList;
    private DefaultListModel<String> listModel;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton logoutButton;
    private JButton reloadButton;
    private ProductList productList;

    public Dashboard(ShoeStoreApp parent) {
        this.parent = parent;

        setLayout(new BorderLayout());

        // Title Label
        JLabel titleLabel = new JLabel("Product Categories");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Category List
        listModel = new DefaultListModel<>();  // Initialize listModel FIRST
        categoryList = new JList<>(listModel);  // Initialize categoryList SECOND
        categoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        categoryList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedIndex = categoryList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        String selectedCategory = listModel.getElementAt(selectedIndex);
                        parent.showProductList(selectedCategory);
                    }
                }
            }
        });


        loadCategories(); // Load categories from the database
        JScrollPane scrollPane = new JScrollPane(categoryList);
        add(scrollPane, BorderLayout.CENTER);

    }
    
public void loadCategories() {
    listModel.removeAllElements(); // Clear before loading

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "")) {
        try (Statement stmt = conn.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT name, description FROM category")) { // Correct SQL
                while (rs.next()) {
                    String categoryName = rs.getString("name");
//                    String categoryDescription = rs.getString("description");

                    // Add both name and description to the list model (or however you want to display them)
                    listModel.addElement(categoryName);
//                    listModel.addElement(categoryDescription); 
                }
            }
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    }
}

}