/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.shoestockapp.Interfaces;


import com.mycompany.shoestockapp.ShoeStoreApp;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductList extends JPanel {
    private ShoeStoreApp parent;
    private String category;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private JButton backButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton updateButton;
    private JButton reloadButton;

    public ProductList(ShoeStoreApp parent, String category) {
        this.parent = parent;
        this.category = category;

        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Shoes in " + category);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Product Table
        tableModel = new DefaultTableModel(new Object[]{"Name", "Price", "Quantity"}, 0); // Column names
        productTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(productTable);
        add(scrollPane, BorderLayout.CENTER);

        loadProducts(); // Load products from the database

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        backButton = new JButton("Back to Categories");
        backButton.addActionListener(e -> parent.showDashboard());
        buttonPanel.add(backButton);

        addButton = new JButton("Add Product");
        addButton.addActionListener(e -> showAddProductDialog());
        buttonPanel.add(addButton);

        deleteButton = new JButton("Delete Product");
        deleteButton.addActionListener(e -> deleteSelectedProduct());
        buttonPanel.add(deleteButton);

        updateButton = new JButton("Update Product");
        updateButton.addActionListener(e -> updateSelectedProduct());
        buttonPanel.add(updateButton);

        reloadButton = new JButton("Reload");
        reloadButton.addActionListener(e -> loadProducts());
        buttonPanel.add(reloadButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        tableModel.setRowCount(0); // Clear existing rows
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "");
            PreparedStatement stmt = conn.prepareStatement("SELECT name, price, quantityInStock FROM shoes WHERE category_id = (SELECT catId FROM category WHERE name = ?)");
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantityInStock");
                tableModel.addRow(new Object[]{name, price, quantity});
            }

            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

private void showAddProductDialog() {
    try {
        String categoryName = this.category;
        int categoryId = getCategoryId(categoryName);

        if (categoryId == -1) {
            JOptionPane.showMessageDialog(this, "Category not found!");
            return;
        }

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();
        JTextField colorField = new JTextField();
        JTextField brandField = new JTextField();
        JTextField sizeField = new JTextField();

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Price:"));
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Quantity:"));
        inputPanel.add(quantityField);
        inputPanel.add(new JLabel("Color:"));
        inputPanel.add(colorField);
        inputPanel.add(new JLabel("Brand:"));
        inputPanel.add(brandField);
        inputPanel.add(new JLabel("Size:"));
        inputPanel.add(sizeField);

        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Add Product", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String brand = brandField.getText();
                String color = colorField.getText();
                int size = Integer.parseInt(sizeField.getText());

                System.out.println("Inserting product:");  // Debugging prints
                System.out.println("Name: " + name);
                System.out.println("Price: " + price);
                System.out.println("Quantity: " + quantity);
                System.out.println("Category ID: " + categoryId);
                System.out.println("Color: " + color);
                System.out.println("Brand: " + brand);
                System.out.println("Size: " + size);



                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "")) {
                    String sql = "INSERT INTO shoes (name, price, quantityInStock, category_id, color, brand, size) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setString(1, name);
                        stmt.setDouble(2, price);
                        stmt.setInt(3, quantity);
                        stmt.setInt(4, categoryId);
                        stmt.setString(5, color);
                        stmt.setString(6, brand);
                        stmt.setInt(7, size);
                        stmt.executeUpdate();
                    }
                    loadProducts(); // Refresh product list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid price or quantity/size. Please enter numbers only.");
            }
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
    }
}


private int getCategoryId(String categoryName) throws SQLException {
    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "")) {
        String sql = "SELECT catId FROM category WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("catId");
                }
            }
        }
    }
    return -1; // Return -1 if category not found
}



private void deleteSelectedProduct() {
    int selectedRow = productTable.getSelectedRow();
    if (selectedRow != -1) {
        String productName = (String) productTable.getValueAt(selectedRow, 0); // Get product name

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete " + productName + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "")) {
                String sql = "DELETE FROM shoes WHERE name = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, productName);
                    stmt.executeUpdate();
                }
                loadProducts(); // Refresh product list
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        }
    } else {
        JOptionPane.showMessageDialog(this, "Please select a product to delete.");
    }
}



private void updateSelectedProduct() {
    int selectedRow = productTable.getSelectedRow();
    if (selectedRow != -1) {
        String oldProductName = (String) productTable.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shoeStock", "root", "")) {
            String sqlSelect = "SELECT price, quantityInStock, color, brand, size FROM shoes WHERE name = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(sqlSelect)) {
                selectStmt.setString(1, oldProductName);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        double currentPrice = rs.getDouble("price");
                        int currentQuantity = rs.getInt("quantityInStock");
                        String currentColor = rs.getString("color");
                        String currentBrand = rs.getString("brand");
                        int currentSize = rs.getInt("size");

                        JPanel inputPanel = new JPanel(new GridLayout(6, 2)); // Corrected layout: 6 rows, 2 columns
                        JTextField nameField = new JTextField(oldProductName);
                        JTextField priceField = new JTextField(String.valueOf(currentPrice));
                        JTextField quantityField = new JTextField(String.valueOf(currentQuantity));
                        JTextField colorField = new JTextField(String.valueOf(currentColor));
                        JTextField brandField = new JTextField(String.valueOf(currentBrand));
                        JTextField sizeField = new JTextField(String.valueOf(currentSize));

                        inputPanel.add(new JLabel("Name:"));
                        inputPanel.add(nameField);
                        inputPanel.add(new JLabel("Price:"));
                        inputPanel.add(priceField);
                        inputPanel.add(new JLabel("Quantity:"));
                        inputPanel.add(quantityField);
                        inputPanel.add(new JLabel("Color:"));
                        inputPanel.add(colorField);
                        inputPanel.add(new JLabel("Brand:"));
                        inputPanel.add(brandField);
                        inputPanel.add(new JLabel("Size:"));
                        inputPanel.add(sizeField);

                        int result = JOptionPane.showConfirmDialog(this, inputPanel, "Update Product", JOptionPane.OK_CANCEL_OPTION);

                        if (result == JOptionPane.OK_OPTION) {
                            try { // Try block for parsing
                                String newProductName = nameField.getText();
                                double newPrice = Double.parseDouble(priceField.getText());
                                int newQuantity = Integer.parseInt(quantityField.getText());
                                String newColor = colorField.getText();
                                String newBrand = brandField.getText();
                                int newSize = Integer.parseInt(sizeField.getText());

                                String sqlUpdate = "UPDATE shoes SET name = ?, price = ?, quantityInStock = ?, color = ?, brand = ?, size = ? WHERE name = ?";
                                try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                                    updateStmt.setString(1, newProductName);
                                    updateStmt.setDouble(2, newPrice);
                                    updateStmt.setInt(3, newQuantity);
                                    updateStmt.setString(4, newColor);
                                    updateStmt.setString(5, newBrand);
                                    updateStmt.setInt(6, newSize);
                                    updateStmt.setString(7, oldProductName);
                                    updateStmt.executeUpdate();
                                }
                                loadProducts();
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(this, "Invalid input for price, quantity, or size. Please enter numbers only.");
                            }
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }

    } else {
        JOptionPane.showMessageDialog(this, "Please select a product to update.");
    }
}
}
