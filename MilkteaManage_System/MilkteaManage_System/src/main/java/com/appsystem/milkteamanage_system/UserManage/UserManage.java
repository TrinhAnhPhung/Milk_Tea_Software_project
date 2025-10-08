/*
 * Click nbfs://SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.appsystem.milkteamanage_system.UserManage;

import com.appsystem.milkteamanage_system.Utils.DBConnection;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author Admin
 */
public class UserManage extends javax.swing.JPanel {

    private JTable userTable;
    private DefaultTableModel tableModel;

    /**
     * Creates new form UserManage
     */
    public UserManage() {
        setLayout(new BorderLayout());

        // Initialize tableModel first
        String[] columnNames = {"Avatar", "ID", "Tên", "Số Điện Thoại", "Email", "Vai Trò", "Username", "Password"};
        tableModel = new DefaultTableModel(columnNames, 0);

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
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setPreferredSize(new Dimension(900, 700));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setOpaque(false);
        JLabel headerLabel = new JLabel("Quản Lý Người Dùng");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(new Color(44, 62, 80));
        headerPanel.add(headerLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setOpaque(false);
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField searchField = new JTextField("Nhập tên người dùng hoặc email...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setForeground(new Color(100, 100, 100));
        searchLabel.setBorder(BorderFactory.createEmptyBorder());
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Nhập tên người dùng hoặc email...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(44, 62, 80));
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Nhập tên người dùng hoặc email...");
                    searchField.setForeground(new Color(100, 100, 100));
                }
            }
        });
        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.setBackground(new Color(70, 130, 180));
        searchButton.setForeground(Color.WHITE);
        searchButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.equals("Nhập tên người dùng hoặc email...") && !query.isEmpty()) {
                filterTable(query);
            } else {
                loadStaffData();
            }
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setGridColor(new Color(200, 200, 200));
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        userTable.getTableHeader().setBackground(new Color(70, 130, 180));
        userTable.getTableHeader().setForeground(Color.BLACK);
        userTable.setDefaultRenderer(Object.class, new Utils.TableBackGroundRender());
        userTable.setOpaque(true);
        userTable.setBackground(new Color(250, 240, 230));
        userTable.setRowHeight(100); // Tăng chiều cao hàng để hiển thị ảnh

        // render ảnh 
        userTable.getColumnModel().getColumn(0).setCellRenderer(new Utils.ImageRender());

        // thao tác khi click vào các row user
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem editItem = new JMenuItem("Sửa");
        editItem.setFont(new Font("Arial", Font.PLAIN, 14));
        editItem.addActionListener(e -> showEditDialog());
        JMenuItem deleteItem = new JMenuItem("Xóa");
        deleteItem.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteItem.addActionListener(e -> deleteStaff());
        contextMenu.add(editItem);
        contextMenu.add(deleteItem);

        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showContextMenu(e);
                }
            }

            private void showContextMenu(MouseEvent e) {
                int row = userTable.rowAtPoint(e.getPoint());
                if (row >= 0 && row < userTable.getRowCount()) {
                    userTable.setRowSelectionInterval(row, row);
                    contextMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setBorder(new LineBorder(new Color(70, 130, 180), 2));
        tableScrollPane.setOpaque(true);
        tableScrollPane.getViewport().setOpaque(true);
        tableScrollPane.getViewport().setBackground(new Color(250, 240, 230));

        centerPanel.add(searchPanel);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(tableScrollPane);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JButton addButton = new JButton("Thêm Người Dùng");
        addButton.setFont(new Font("Arial", Font.PLAIN, 14));
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        addButton.addActionListener(e -> showCreateDialog());
        JButton editButton = new JButton("Sửa Người Dùng");
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));
        editButton.setBackground(new Color(255, 183, 77));
        editButton.setForeground(Color.WHITE);
        editButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        editButton.addActionListener(e -> showEditDialog());
        JButton deleteButton = new JButton("Xóa Người Dùng");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        deleteButton.addActionListener(e -> deleteStaff());
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Load data after all components are initialized
        loadStaffData();
        userTable.setShowGrid(true);
        userTable.setGridColor(Color.LIGHT_GRAY);
    }

    private void loadStaffData() {
        if (tableModel == null) {
            System.err.println("tableModel is null. Initializing...");
            String[] columnNames = {"Avatar", "ID", "Tên", "Số Điện Thoại", "Email", "Vai Trò", "Username", "Password"};
            tableModel = new DefaultTableModel(columnNames, 0);
            if (userTable != null) {
                userTable.setModel(tableModel);
            }
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT StaffID, FullName, PhoneNumber, Email, Role, Username, Password, AvatarPath FROM Staffs"); ResultSet rs = pstmt.executeQuery()) {

            tableModel.setRowCount(0);

            while (rs.next()) {
                int staffId = rs.getInt("StaffID");
                String avatarPath = rs.getString("AvatarPath"); // Lấy từ cột AvatarPath
                if (avatarPath == null || avatarPath.trim().isEmpty()) {
                    avatarPath = "src/main/Resources/images/default-avatar.png"; // Ảnh mặc định nếu không có
                }
                Object[] row = {
                    avatarPath, // Đường dẫn ảnh cho cột Avatar
                    staffId,
                    rs.getString("FullName"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Email"),
                    rs.getString("Role"),
                    rs.getString("Username"),
                    rs.getString("Password")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu nhân viên: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterTable(String query) {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                "SELECT StaffID, FullName, PhoneNumber, Email, Role, Username, Password, AvatarPath FROM Staffs "
                + "WHERE FullName LIKE ? OR Email LIKE ?")) {
            pstmt.setString(1, "%" + query + "%");
            pstmt.setString(2, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();

            tableModel.setRowCount(0);
            while (rs.next()) {
                int staffId = rs.getInt("StaffID");
                String avatarPath = rs.getString("AvatarPath");
                if (avatarPath == null || avatarPath.trim().isEmpty()) {
                    avatarPath = "src/main/Resources/images/default-avatar.png"; // Ảnh mặc định nếu không có
                }
                Object[] row = {
                    avatarPath,
                    staffId,
                    rs.getString("FullName"),
                    rs.getString("PhoneNumber"),
                    rs.getString("Email"),
                    rs.getString("Role"),
                    rs.getString("Username"),
                    rs.getString("Password")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCreateDialog() {
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Thêm Người Dùng", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(850, 450); // Tăng kích thước để hiển thị ảnh và các thành phần
        dialog.setLocationRelativeTo(this);

        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "staff"});
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        passwordField.setText("Trasuangon123!");
        passwordField.setEnabled(false);
        JTextField avatarPathField = new JTextField("src/main/Resources/images/default-avatar.png");
        avatarPathField.setPreferredSize(new Dimension(250, 25)); // Kích thước đủ để hiển thị đường dẫn
        JLabel avatarPreview = new JLabel(); // JLabel để hiển thị ảnh preview
        Utils.updateAvatarPreview(avatarPreview, "src/main/Resources/images/default-avatar.png"); // Hiển thị ảnh mặc định
        JButton chooseImageButton = new JButton("Chọn Ảnh");
        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                avatarPathField.setText(path);
                Utils.updateAvatarPreview(avatarPreview, path); // Cập nhật preview khi chọn ảnh
            }
        });

        dialog.add(new JLabel("Họ Tên:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Số Điện Thoại:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Vai Trò:"));
        dialog.add(roleCombo);
        dialog.add(new JLabel("Tên Đăng Nhập:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Mật Khẩu:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Đường Dẫn Ảnh:"));
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        avatarPanel.add(avatarPreview);
        avatarPanel.add(avatarPathField);
        avatarPanel.add(chooseImageButton);
        dialog.add(avatarPanel);

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(e -> {
            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            String username = usernameField.getText().trim();
            String defaultPassword = "Trasuangon123!";
            String avatarPath = avatarPathField.getText().trim();

            if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty() || username.isEmpty() || avatarPath.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Email không đúng định dạng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidPhoneNumber(phone)) {
                JOptionPane.showMessageDialog(dialog, "Số điện thoại phải bắt đầu bằng 0 và có 10-11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String hashedPassword = BCrypt.hashpw(defaultPassword, BCrypt.gensalt(12));

            try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT INTO Staffs (FullName, PhoneNumber, Email, Role, Username, Password, AvatarPath) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                pstmt.setString(1, fullName);
                pstmt.setString(2, phone);
                pstmt.setString(3, email);
                pstmt.setString(4, role);
                pstmt.setString(5, username);
                pstmt.setString(6, hashedPassword);
                pstmt.setString(7, avatarPath);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Thêm người dùng thành công! Mật khẩu mặc định: Trasuangon123!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadStaffData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm người dùng: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để sửa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int staffId = (int) tableModel.getValueAt(selectedRow, 1); // ID now at index 1 due to Avatar column
        String fullName = (String) tableModel.getValueAt(selectedRow, 2);
        String phone = (String) tableModel.getValueAt(selectedRow, 3);
        String email = (String) tableModel.getValueAt(selectedRow, 4);
        String role = (String) tableModel.getValueAt(selectedRow, 5);
        String username = (String) tableModel.getValueAt(selectedRow, 6);
        String currentHashedPassword = (String) tableModel.getValueAt(selectedRow, 7);
        String avatarPath = (String) tableModel.getValueAt(selectedRow, 0);

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Sửa Người Dùng", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new GridLayout(8, 2, 10, 10));
        dialog.setSize(850, 450); // Tăng kích thước để hiển thị ảnh và các thành phần
        dialog.setLocationRelativeTo(this);

        JTextField fullNameField = new JTextField(fullName);
        JTextField phoneField = new JTextField(phone);
        JTextField emailField = new JTextField(email);
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "staff"});
        roleCombo.setSelectedItem(role);
        JTextField usernameField = new JTextField(username);
        JPasswordField passwordField = new JPasswordField();
        JTextField avatarPathField = new JTextField(avatarPath);
        avatarPathField.setPreferredSize(new Dimension(250, 25)); // Kích thước đủ để hiển thị đường dẫn
        JLabel avatarPreview = new JLabel(); // JLabel để hiển thị ảnh preview
        Utils.updateAvatarPreview(avatarPreview, avatarPath); // Hiển thị ảnh hiện tại
        JButton chooseImageButton = new JButton("Chọn Ảnh");
        chooseImageButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            int result = fileChooser.showOpenDialog(dialog);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();
                avatarPathField.setText(path);
                Utils.updateAvatarPreview(avatarPreview, path); // Cập nhật preview khi chọn ảnh
            }
        });

        dialog.add(new JLabel("Họ Tên:"));
        dialog.add(fullNameField);
        dialog.add(new JLabel("Số Điện Thoại:"));
        dialog.add(phoneField);
        dialog.add(new JLabel("Email:"));
        dialog.add(emailField);
        dialog.add(new JLabel("Vai Trò:"));
        dialog.add(roleCombo);
        dialog.add(new JLabel("Tên người dùng:"));
        dialog.add(usernameField);
        dialog.add(new JLabel("Mật Khẩu:"));
        dialog.add(passwordField);
        dialog.add(new JLabel("Đường Dẫn Ảnh:"));
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        avatarPanel.add(avatarPreview); // Thêm ảnh preview bên trái
        avatarPanel.add(avatarPathField); // Thêm text field
        avatarPanel.add(chooseImageButton); // Thêm nút chọn ảnh bên phải
        dialog.add(avatarPanel);

        JButton saveButton = new JButton("Lưu");
        saveButton.addActionListener(e -> {
            String newFullName = fullNameField.getText().trim();
            String newPhone = phoneField.getText().trim();
            String newEmail = emailField.getText().trim();
            String newRole = (String) roleCombo.getSelectedItem();
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword()).trim();
            String newAvatarPath = avatarPathField.getText().trim();

            if (newFullName.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty() || newUsername.isEmpty() || newAvatarPath.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidEmail(newEmail)) {
                JOptionPane.showMessageDialog(dialog, "Email không đúng định dạng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isValidPhoneNumber(newPhone)) {
                JOptionPane.showMessageDialog(dialog, "Số điện thoại phải bắt đầu bằng 0 và có 10-11 chữ số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!newPassword.isEmpty() && !isValidPassWord(passwordField)) {
                JOptionPane.showMessageDialog(dialog, "Mật khẩu phải có 6-20 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt (!#$%&)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String hashedPassword = newPassword.isEmpty() ? currentHashedPassword : BCrypt.hashpw(newPassword, BCrypt.gensalt(12));

            try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE Staffs SET FullName = ?, PhoneNumber = ?, Email = ?, Role = ?, Username = ?, Password = ?, AvatarPath = ? WHERE StaffID = ?")) {
                pstmt.setString(1, newFullName);
                pstmt.setString(2, newPhone);
                pstmt.setString(3, newEmail);
                pstmt.setString(4, newRole);
                pstmt.setString(5, newUsername);
                pstmt.setString(6, hashedPassword);
                pstmt.setString(7, newAvatarPath);
                pstmt.setInt(8, staffId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadStaffData();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, "Lỗi khi cập nhật: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);
        dialog.setVisible(true);
    }

    private void deleteStaff() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một người dùng để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int staffId = (int) tableModel.getValueAt(selectedRow, 1); // ID now at index 1 due to Avatar column
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa người dùng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement("DELETE FROM Staffs WHERE StaffID = ?")) {
                pstmt.setInt(1, staffId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadStaffData();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Validate data
    private boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("^0\\d{9,10}$");
    }

    private boolean isValidPassWord(JPasswordField password) {
        String passwordText = new String(password.getPassword());
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!#$%&])[A-Za-z\\d!#$%&]{6,20}$";
        return passwordText.matches(regex);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("User Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(900, 700));
            frame.add(new UserManage());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
