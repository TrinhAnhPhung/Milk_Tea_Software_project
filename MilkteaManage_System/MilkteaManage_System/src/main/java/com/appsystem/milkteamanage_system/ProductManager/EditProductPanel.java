package com.appsystem.milkteamanage_system.ProductManager;

import com.appsystem.milkteamanage_system.Utils.DBConnection;
import com.appsystem.milkteamanage_system.Utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class EditProductPanel extends JPanel {

    private JLabel lblId;
    private JTextField tfName, tfPrice, tfDesc, tfImgPath;
    private JComboBox<String> cbStatus;
    private JButton btnBrowse, btnSave, btnCancel;
    private int productId;
    private JLabel productPicturePreview;

    public EditProductPanel(int id, String name, String desc, Double price, String status, Timestamp createdDate, String imgPath, Runnable onSaveCallback) {
        this.productId = id;

        setLayout(new GridLayout(8, 2, 10, 10));
        setPreferredSize(new Dimension(850, 450)); // Đồng bộ kích thước với AddProductPanel

        lblId = new JLabel(String.valueOf(id));
        tfName = new JTextField(name);
        tfPrice = new JTextField(Double.toString(price));
        tfDesc = new JTextField(desc);
        tfImgPath = new JTextField(imgPath);
        tfImgPath.setPreferredSize(new Dimension(280, 35)); 
        cbStatus = new JComboBox<>(new String[]{"Còn Bán", "Ngừng Bán"});
        cbStatus.setSelectedItem(status != null ? status : "Còn Bán");
        productPicturePreview = new JLabel();
        Utils.updateAvatarPreview(productPicturePreview, imgPath); // Hiển thị ảnh mặc định khi khởi tạo

        btnBrowse = new JButton("Chọn ảnh");
        btnBrowse.addActionListener(e -> chooseImage());

        btnSave = new JButton("Lưu");
        btnSave.setBackground (new java.awt.Color(102,255,102));
        btnSave.addActionListener((ActionEvent evt) -> saveProduct(onSaveCallback));

        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(Color.GRAY);
        btnCancel.addActionListener(e -> SwingUtilities.getWindowAncestor(this).dispose());

        add(new JLabel("ID:"));
        add(lblId);
        add(new JLabel("Tên sản phẩm:"));
        add(tfName);
        add(new JLabel("Mô tả:"));
        add(tfDesc);
        add(new JLabel("Giá:"));
        add(tfPrice);
        add(new JLabel("Trạng thái:"));
        add(cbStatus);
        add(new JLabel("Đường dẫn ảnh:"));
        JPanel imgPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        imgPanel.add(productPicturePreview);
        imgPanel.add(tfImgPath);
        imgPanel.add(btnBrowse);
        add(imgPanel);
        add(btnSave);
        add(btnCancel);
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Images", "jpg", "png", "jpeg", "gif"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            tfImgPath.setText(selectedFile.getAbsolutePath());
            String path = tfImgPath.getText().trim();
            Utils.updateAvatarPreview(productPicturePreview, path);
        }
    }

    private void saveProduct(Runnable onSaveCallback) {
        String name = tfName.getText().trim();
        String priceText = tfPrice.getText().trim();
        String desc = tfDesc.getText().trim();
        String status = (String) cbStatus.getSelectedItem();
        String imgPath = tfImgPath.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ tên và giá sản phẩm!");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Giá phải lớn hơn 0!");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Giá phải là số hợp lệ!\nBạn đã nhập: '" + priceText + "'");
            return;
        }

        String sql = "UPDATE Products SET Name = ?, Description = ?, Price = ?, Status = ?, ImgPath = ? WHERE ProductID = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, name);
            pst.setString(2, desc);
            pst.setDouble(3, price);
            pst.setString(4, status);
            pst.setString(5, imgPath);
            pst.setInt(6, productId);
            pst.executeUpdate();

            JOptionPane.showMessageDialog(this, "Cập nhật sản phẩm thành công!");
            onSaveCallback.run();
            SwingUtilities.getWindowAncestor(this).dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage());
        }
    }

    public static void showEditDialog(Component parent, int id, String name, String desc, Double price, String status, Timestamp createdDate, String imgPath, Runnable onSaveCallback) {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Sửa sản phẩm", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setContentPane(new EditProductPanel(id, name, desc, price, status, createdDate, imgPath, onSaveCallback));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }
}
