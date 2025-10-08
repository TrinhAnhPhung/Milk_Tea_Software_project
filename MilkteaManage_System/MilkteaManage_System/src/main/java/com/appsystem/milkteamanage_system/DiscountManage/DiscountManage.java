package com.appsystem.milkteamanage_system.DiscountManage;

import com.appsystem.milkteamanage_system.Utils.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class DiscountManage extends JPanel {
    private JTable discountTable;
    private DefaultTableModel model;
    private Connection conn;

    public DiscountManage() {
        setLayout(new BorderLayout());
        JLabel title = new JLabel("Quản Lý Khuyến Mãi", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"ID", "Tên", "Mô tả", "Phần trăm", "Ngày bắt đầu", "Ngày kết thúc", "Ngày tạo"};
        model = new DefaultTableModel(columnNames, 0);
        discountTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(discountTable);
        add(scrollPane, BorderLayout.CENTER);


        try {
            conn = DBConnection.getConnection();
            loadDiscountsFromDatabase(); 
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Không kết nối được tới CSDL: " + e.getMessage());
        }

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Thêm");
        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        add(buttonPanel, BorderLayout.SOUTH);
        btnAdd.addActionListener(e -> addDiscount());
        btnEdit.addActionListener(e -> editDiscount());
        btnDelete.addActionListener(e -> deleteDiscount());
    }

    private void loadDiscountsFromDatabase() {
        model.setRowCount(0); // Clear table

        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM Discounts")
        ) {
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("DiscountID"));
                row.add(rs.getString("Name"));
                row.add(rs.getString("Description"));
                row.add(rs.getBigDecimal("DiscountPercent"));
                row.add(rs.getTimestamp("StartDate"));
                row.add(rs.getTimestamp("EndDate"));
                row.add(rs.getTimestamp("CreatedDate"));
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }


    private void addDiscount() {
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();
        JTextField percentField = new JTextField();
        JTextField startField = new JTextField("YYYY-MM-DD");
        JTextField endField = new JTextField("YYYY-MM-DD");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên:"));
        panel.add(nameField);
        panel.add(new JLabel("Mô tả:"));
        panel.add(descField);
        panel.add(new JLabel("Phần trăm:"));
        panel.add(percentField);
        panel.add(new JLabel("Ngày bắt đầu:"));
        panel.add(startField);
        panel.add(new JLabel("Ngày kết thúc:"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Thêm khuyến mãi", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (PreparedStatement stmt = conn.prepareStatement("""
                        INSERT INTO Discounts (Name, Description, DiscountPercent, StartDate, EndDate)
                        VALUES (?, ?, ?, ?, ?)
                    """)) {
                stmt.setString(1, nameField.getText());
                stmt.setString(2, descField.getText());
                stmt.setBigDecimal(3, new BigDecimal(percentField.getText()));
                stmt.setDate(4, Date.valueOf(startField.getText()));
                stmt.setDate(5, Date.valueOf(endField.getText()));
                stmt.executeUpdate();
                loadDiscountsFromDatabase();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                 JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày. Vui lòng sử dụng YYYY-MM-DD.");
            }

        }
    }
    private void editDiscount() {
        int row = discountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để sửa.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        String desc = (String) model.getValueAt(row, 2);
        String percent = model.getValueAt(row, 3).toString();
        
        // FIX: Convert Timestamp from table to a "yyyy-MM-dd" string
        Timestamp startTimestamp = (Timestamp) model.getValueAt(row, 4);
        Timestamp endTimestamp = (Timestamp) model.getValueAt(row, 5);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start = sdf.format(startTimestamp);
        String end = sdf.format(endTimestamp);

        JTextField nameField = new JTextField(name);
        JTextField descField = new JTextField(desc);
        JTextField percentField = new JTextField(percent);
        JTextField startField = new JTextField(start);
        JTextField endField = new JTextField(end);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Tên:"));
        panel.add(nameField);
        panel.add(new JLabel("Mô tả:"));
        panel.add(descField);
        panel.add(new JLabel("Phần trăm:"));
        panel.add(percentField);
        panel.add(new JLabel("Ngày bắt đầu:"));
        panel.add(startField);
        panel.add(new JLabel("Ngày kết thúc:"));
        panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Sửa khuyến mãi", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (PreparedStatement stmt = conn.prepareStatement("""
                UPDATE Discounts
                SET Name = ?, Description = ?, DiscountPercent = ?, StartDate = ?, EndDate = ?
                WHERE DiscountID = ?
            """)) {
                stmt.setString(1, nameField.getText());
                stmt.setString(2, descField.getText());
                stmt.setBigDecimal(3, new BigDecimal(percentField.getText()));
                stmt.setDate(4, Date.valueOf(startField.getText()));
                stmt.setDate(5, Date.valueOf(endField.getText()));
                stmt.setInt(6, id);
                stmt.executeUpdate();
                loadDiscountsFromDatabase();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi sửa: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                 JOptionPane.showMessageDialog(this, "Lỗi định dạng ngày. Vui lòng sử dụng YYYY-MM-DD.");
            }
        }
    }
    private void deleteDiscount() {
        int row = discountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để xoá.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xoá khuyến mãi này?", "Xoá", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM Discounts WHERE DiscountID = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                loadDiscountsFromDatabase();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xoá: " + e.getMessage());
            }
        }
    }
}
