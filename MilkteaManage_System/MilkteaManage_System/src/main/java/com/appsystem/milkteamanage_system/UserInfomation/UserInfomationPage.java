/*
 * Click https://netbeans.org/projects/samples/downloads/download/Samples%252FJava%252FJava%2520GUI%2520Applications/Licenses/license-default.txt to change this license
 * Click https://netbeans.org/projects/samples/downloads/download/Samples%252FJava%252FJava%2520GUI%2520Applications/GUIForms/JFrame.java to edit this template
 */
package com.appsystem.milkteamanage_system.UserInfomation;

import com.appsystem.milkteamanage_system.Utils.DBConnection;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Admin
 */
public class UserInfomationPage extends javax.swing.JFrame {

    private int staffId;
    private String staffName;
    private JLabel avatarPreview;
    private JTextField fullNameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JLabel roleLabel;
    private JTextField avatarPathField;

    /**
     * Creates new form UserInfomationPage
     */
    public UserInfomationPage(int staffId, String staffName) {
        this.staffId = staffId;
        this.staffName = staffName;
        initComponents();
        loadUserData();
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thông Tin Cá Nhân");
        setSize(600, 650); // Điều chỉnh kích thước do bỏ địa chỉ
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(240, 248, 255), 0, getHeight(), new Color(200, 220, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Thông Tin Cá Nhân");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Center panel for user info
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Avatar section
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        avatarPanel.setOpaque(false);
        avatarPreview = new JLabel();
        avatarPreview.setHorizontalAlignment(SwingConstants.CENTER);
        avatarPreview.setBorder(new LineBorder(new Color(70, 130, 180), 2));
        avatarPreview.setPreferredSize(new Dimension(150, 150));
        avatarPathField = new JTextField("src/main/Resources/images/default-avatar.png");
        avatarPathField.setPreferredSize(new Dimension(250, 30));
        avatarPathField.setEditable(false);
        JButton chooseImageButton = new JButton("Chọn Ảnh");
        chooseImageButton.setBackground(new Color(70, 130, 180));
        chooseImageButton.setForeground(Color.WHITE);
        chooseImageButton.setFocusPainted(false);
        chooseImageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(UserInfomationPage.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    String path = selectedFile.getAbsolutePath();
                    avatarPathField.setText(path);
                    Utils.updateAvatarPreview(avatarPreview, path);
                }
            }
        });

        avatarPanel.add(avatarPreview);
        avatarPanel.add(avatarPathField);
        avatarPanel.add(chooseImageButton);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(avatarPanel, gbc);

        // Full Name
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Họ và Tên:"), gbc);
        gbc.gridx = 1;
        fullNameField = new JTextField(20);
        fullNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(fullNameField, gbc);

        // Phone
        gbc.gridy = 2;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Số Điện Thoại:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(phoneField, gbc);

        // Email
        gbc.gridy = 3;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(emailField, gbc);

        // Role
        gbc.gridy = 4;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Vai Trò:"), gbc);
        gbc.gridx = 1;
        roleLabel = new JLabel();
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(roleLabel, gbc);

        // Change Password Button
        gbc.gridy = 5;
        gbc.gridx = 0;
        centerPanel.add(new JLabel("Đổi Mật Khẩu:"), gbc);
        gbc.gridx = 1;
        JButton changePasswordButton = new JButton("Đổi Mật Khẩu");
        changePasswordButton.setBackground(new Color(255, 183, 77));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showChangePasswordDialog();
            }
        });
        centerPanel.add(changePasswordButton, gbc);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        JButton saveButton = new JButton("Lưu Thay Đổi");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setBackground(new Color(255, 87, 51));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Set Nimbus look and feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT FullName, PhoneNumber, Email, Role, Password, AvatarPath FROM Staffs WHERE StaffID = ?")) {
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                fullNameField.setText(rs.getString("FullName"));
                phoneField.setText(rs.getString("PhoneNumber"));
                emailField.setText(rs.getString("Email"));
                roleLabel.setText(rs.getString("Role"));
                String avatarPath = rs.getString("AvatarPath");
                if (avatarPath == null || avatarPath.trim().isEmpty()) {
                    avatarPath = "src/main/Resources/images/default-avatar.png";
                }
                avatarPathField.setText(avatarPath);
                Utils.updateAvatarPreview(avatarPreview, avatarPath);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveChanges() {
        String newFullName = fullNameField.getText().trim();
        String newPhone = phoneField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newAvatarPath = avatarPathField.getText().trim();

        if (newFullName.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Utils.isValidEmail(newEmail)) {
            JOptionPane.showMessageDialog(this, "Email không đúng định dạng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Utils.isValidPhoneNumber(newPhone)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại phải bắt đầu bằng 0 và có 10-11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE Staffs SET FullName = ?, PhoneNumber = ?, Email = ?, AvatarPath = ? WHERE StaffID = ?")) {
            pstmt.setString(1, newFullName);
            pstmt.setString(2, newPhone);
            pstmt.setString(3, newEmail);
            pstmt.setString(4, newAvatarPath);
            pstmt.setInt(5, staffId);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thay đổi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "Đổi Mật Khẩu", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);

        JLabel oldPasswordLabel = new JLabel("Mật Khẩu Cũ:");
        JPasswordField oldPasswordField = new JPasswordField(15);
        JLabel newPasswordLabel = new JLabel("Mật Khẩu Mới:");
        JPasswordField newPasswordField = new JPasswordField(15);

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String oldPassword = new String(oldPasswordField.getPassword()).trim();
                String newPassword = new String(newPasswordField.getPassword()).trim();

                if (oldPassword.isEmpty() || newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!Utils.isValidPassword(newPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Mật khẩu phải có 6-20 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (!#$%&)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement("SELECT Password FROM Staffs WHERE StaffID = ?")) {
                    pstmt.setInt(1, staffId);
                    ResultSet rs = pstmt.executeQuery();
                    if (rs.next()) {
                        String storedHash = rs.getString("Password");
                        if (BCrypt.checkpw(oldPassword, storedHash.trim())) {
                            String hashedNewPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt(12)).trim();
                            try (PreparedStatement updateStmt = conn.prepareStatement("UPDATE Staffs SET Password = ? WHERE StaffID = ?")) {
                                updateStmt.setString(1, hashedNewPassword);
                                updateStmt.setInt(2, staffId);
                                updateStmt.executeUpdate();
                                JOptionPane.showMessageDialog(dialog, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                                dialog.dispose();
                            }
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Mật khẩu cũ không đúng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(dialog, "Lỗi khi đổi mật khẩu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        dialog.add(oldPasswordLabel);
        dialog.add(oldPasswordField);
        dialog.add(newPasswordLabel);
        dialog.add(newPasswordField);
        dialog.add(new JLabel()); // Placeholder
        dialog.add(saveButton);
        dialog.add(new JLabel()); // Placeholder
        dialog.add(cancelButton);

        dialog.setVisible(true);
    }

    

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserInfomationPage.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserInfomationPage(1, "Nguyen Van A").setVisible(true); // Example staffId and staffName
            }
        });
    }
}