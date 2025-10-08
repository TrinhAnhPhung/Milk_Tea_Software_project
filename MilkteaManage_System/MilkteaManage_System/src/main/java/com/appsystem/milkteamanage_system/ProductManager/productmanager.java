/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.appsystem.milkteamanage_system.ProductManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.appsystem.milkteamanage_system.Utils.DBConnection;
import java.sql.Timestamp;
import javax.swing.JDialog;
import com.appsystem.milkteamanage_system.ProductManager.AddProductPanel;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.Color;
import java.awt.Dialog;
import javax.swing.SwingUtilities;

public class productmanager extends javax.swing.JPanel {

    public productmanager() {
        initComponents();
        loadDataFromDatabase();
        tableproduct.getColumnModel().getColumn(0).setCellRenderer(new Utils.ImageRender());
        tableproduct.setShowGrid(true);
        tableproduct.setGridColor(Color.BLACK);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnload = new javax.swing.JButton();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tableproduct = new javax.swing.JTable();

        setPreferredSize(new java.awt.Dimension(900, 700));

        btnload.setText("Load Data");
        btnload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnloadActionPerformed(evt);
            }
        });

        btnAdd.setBackground(new java.awt.Color(102, 255, 102));
        btnAdd.setText("Thêm Sản Phẩm");
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnEdit.setBackground(new java.awt.Color(255, 204, 51));
        btnEdit.setText("Sửa Sản Phẩm");
        btnEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditActionPerformed(evt);
            }
        });

        btnDelete.setBackground(new java.awt.Color(255, 51, 51));
        btnDelete.setText("Xoá Sản Phẩm");
        btnDelete.setToolTipText("");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        tableproduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Hình Ảnh", "Mã SP", "Tên", "Mô Tả", "Giá", "Trạng Thái", "Ngày Tạo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Double.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tableproduct.setRowHeight(100);
        jScrollPane2.setViewportView(tableproduct);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 888, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnload, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(btnAdd)
                        .addGap(28, 28, 28)
                        .addComponent(btnEdit)
                        .addGap(28, 28, 28)
                        .addComponent(btnDelete)
                        .addGap(18, 18, 18)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDelete)
                    .addComponent(btnEdit)
                    .addComponent(btnAdd)
                    .addComponent(btnload))
                .addContainerGap(178, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    private void btnloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnloadActionPerformed
        loadDataFromDatabase();
    }//GEN-LAST:event_btnloadActionPerformed

    private void btnEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditActionPerformed
        // TODO add your handling code here:
        int row = tableproduct.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn sản phẩm để sửa");
            return;
        }
        // Lấy ID
        Object idObj = tableproduct.getValueAt(row, 1);
        int id = idObj instanceof Number
                ? ((Number) idObj).intValue()
                : Integer.parseInt(idObj.toString());

        // Lấy Name, Description an toàn
        String name = tableproduct.getValueAt(row, 2).toString();
        String desc = tableproduct.getValueAt(row, 3).toString();

        // Lấy Price an toàn
        Object priceObj = tableproduct.getValueAt(row, 4);
        double price;

        if (priceObj instanceof Number) {
            price = ((Number) priceObj).doubleValue();
        } else {
            price = Double.parseDouble(priceObj.toString());
        }

        // Lấy Status, ImgPath
        String status = tableproduct.getValueAt(row, 5).toString();
        Timestamp createdDate = (Timestamp) tableproduct.getValueAt(row, 6);
        String img = tableproduct.getValueAt(row, 0).toString();

        // Gọi popup sửa
        EditProductPanel.showEditDialog(
                this,
                id,
                name,
                desc,
                price,
                status,
                createdDate,
                img,
                () -> loadDataFromDatabase()
        );
    }//GEN-LAST:event_btnEditActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        int selectedRow = tableproduct.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Chọn dòng cần xóa!");
            return;
        }
        int id = (int) tableproduct.getValueAt(selectedRow, 1);

        String sql = "DELETE FROM Products WHERE ProductID = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Xóa thành công!");
            loadDataFromDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        AddProductPanel addPanel = new AddProductPanel();

        // Khi đóng panel, reload lại table
        dialog.setContentPane(addPanel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        // Sau khi thêm xong, load lại dữ liệu
        loadDataFromDatabase();

    }//GEN-LAST:event_btnAddActionPerformed
    private void loadDataFromDatabase() {
        String sql = "SELECT ProductID, Name, Description, Price, Status, CreatedDate, ImgPath FROM Products";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            DefaultTableModel model = (DefaultTableModel) tableproduct.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                String imgPath = rs.getString("ImgPath");
                if (imgPath == null || imgPath.trim().isEmpty()) {
                    imgPath = "src/main/Resources/images/default-product.png"; // Ảnh mặc định nếu không có
                }
                model.addRow(new Object[]{
                    imgPath,
                    rs.getInt("ProductID"),
                    rs.getString("Name"),
                    rs.getString("Description"),
                    rs.getDouble("Price"),
                    rs.getString("Status"),
                    rs.getTimestamp("CreatedDate")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnload;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tableproduct;
    // End of variables declaration//GEN-END:variables
}
