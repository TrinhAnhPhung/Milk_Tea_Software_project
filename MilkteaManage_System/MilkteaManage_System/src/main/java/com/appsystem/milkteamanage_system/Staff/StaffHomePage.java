/*
 * Modernized Staff Home Page with centered sidebar buttons, larger tables, and light background
 */
package com.appsystem.milkteamanage_system.Staff;

import com.appsystem.milkteamanage_system.OrderManage.OrderManage;
import com.appsystem.milkteamanage_system.Utils.DBConnection;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public class StaffHomePage extends javax.swing.JFrame {

    private int staffId;
    private String staffName;
    private Map<Integer, JButton> tableButtonMap = new HashMap<>();
    private JPanel rightPanel;
    private StaffInfomationPanel staffInfoPanel;
    private final Color SIDE_MENU_COLOR = new Color(54, 69, 79);
    private final Color BUTTON_COLOR = new Color(74, 92, 105);
    private final Color BUTTON_HOVER_COLOR = new Color(91, 112, 128);
    // FIX: Changed ACTIVE_TABLE_COLOR from red to green as requested
    private final Color ACTIVE_TABLE_COLOR = new Color(40, 167, 69); // Success Green
    private final Color INACTIVE_TABLE_COLOR = new Color(255, 255, 255); // White
    private final Color FONT_COLOR = Color.WHITE;

    public StaffHomePage() {
        initComponents();
        initTableButtonMap();
        checkAndChangeTableButtonBackGround();
        setupRightPanel();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public StaffHomePage(int staffId, String staffName, String orderType) {
        this.staffId = staffId;
        this.staffName = staffName;
        initComponents();
        initTableButtonMap();
        checkAndChangeTableButtonBackGround();
        staffNameDisplay.setText(staffName);
        setupRightPanel();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void setupRightPanel() {
        rightPanel = new JPanel(new java.awt.CardLayout());
        rightPanel.add(tableListPanel, "TableList");
        staffInfoPanel = new StaffInfomationPanel(staffId, staffName);
        rightPanel.add(staffInfoPanel, "StaffInfo");
        OrderManage orderManage = new OrderManage();
        rightPanel.add(orderManage, "OrderManage");
        add(rightPanel, BorderLayout.CENTER);

        ((java.awt.CardLayout) rightPanel.getLayout()).show(rightPanel, "TableList");

        btnCreateOrder.addActionListener(e -> {
            checkAndChangeTableButtonBackGround(); // Refresh colors from DB
            ((java.awt.CardLayout) rightPanel.getLayout()).show(rightPanel, "TableList");
        });
        btnCreateTogoOrder.addActionListener(e -> CheckAndCreateOrder(0, null, staffId, staffName, "Mang đi"));
        btnUserInfomation.addActionListener(e -> ((java.awt.CardLayout) rightPanel.getLayout()).show(rightPanel, "StaffInfo"));
        btnOrderManage.addActionListener(e -> ((java.awt.CardLayout) rightPanel.getLayout()).show(rightPanel, "OrderManage"));
        btnlogOut.addActionListener(e -> {
            // Add logout confirmation if desired
            dispose();
        });
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
        // Main Frame
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Trang Nhân Viên - Quản Lý Quán Trà Sữa");
        setLayout(new BorderLayout());

        // Side Menu
        sideMenu = new JPanel();
        sideMenu.setLayout(new BoxLayout(sideMenu, BoxLayout.Y_AXIS));
        sideMenu.setBackground(SIDE_MENU_COLOR);
        sideMenu.setPreferredSize(new Dimension(280, 0));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Staff Info Section
        JLabel staffTitleLabel = new JLabel("THÔNG TIN NHÂN VIÊN");
        staffTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        staffTitleLabel.setForeground(FONT_COLOR);
        staffTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        staffNameDisplay = new JLabel();
        staffNameDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        staffNameDisplay.setForeground(new Color(200, 200, 200));
        staffNameDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);

        sideMenu.add(staffTitleLabel);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 10)));
        sideMenu.add(staffNameDisplay);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 40)));

        // Menu Buttons
        btnCreateOrder = createMenuButton("Tạo Đơn Tại Quán");
        btnCreateTogoOrder = createMenuButton("Tạo Đơn Mang Đi");
        btnUserInfomation = createMenuButton("Thông Tin Cá Nhân");
        btnOrderManage = createMenuButton("Quản Lí Hoá Đơn");
        btnlogOut = createMenuButton("Đăng Xuất");

        sideMenu.add(btnCreateOrder);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        sideMenu.add(btnCreateTogoOrder);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        sideMenu.add(btnUserInfomation);
        sideMenu.add(Box.createRigidArea(new Dimension(0, 15)));
        sideMenu.add(btnOrderManage);
        sideMenu.add(Box.createVerticalGlue()); // Pushes logout to the bottom
        sideMenu.add(btnlogOut);

        add(sideMenu, BorderLayout.WEST);

        // Table List Panel
        tableListPanel = new JPanel(new GridBagLayout());
        tableListPanel.setBackground(new Color(245, 247, 250));
        tableListPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel tableGridPanel = new JPanel(new GridLayout(3, 3, 40, 40));
        tableGridPanel.setOpaque(false);

        ban1 = createTableButton("Bàn 1", 1);
        ban2 = createTableButton("Bàn 2", 2);
        ban3 = createTableButton("Bàn 3", 3);
        ban4 = createTableButton("Bàn 4", 4);
        ban5 = createTableButton("Bàn 5", 5);
        ban6 = createTableButton("Bàn 6", 6);
        ban7 = createTableButton("Bàn 7", 7);
        ban8 = createTableButton("Bàn 8", 8);
        ban9 = createTableButton("Bàn 9", 9);

        tableGridPanel.add(ban1);
        tableGridPanel.add(ban2);
        tableGridPanel.add(ban3);
        tableGridPanel.add(ban4);
        tableGridPanel.add(ban5);
        tableGridPanel.add(ban6);
        tableGridPanel.add(ban7);
        tableGridPanel.add(ban8);
        tableGridPanel.add(ban9);

        tableListPanel.add(tableGridPanel);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(FONT_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(15, 25, 15, 25));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });
        return button;
    }

    private JButton createTableButton(String text, int tableNumber) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 28));
        button.setPreferredSize(new Dimension(220, 180));
        button.setFocusPainted(false);
        button.setBackground(INACTIVE_TABLE_COLOR);
        button.setForeground(new Color(50, 50, 50));
        button.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.addActionListener(e -> CheckAndCreateOrder(tableNumber, tableButtonMap.get(tableNumber), staffId, staffName, "Uống tại quán"));
        
        Border line = BorderFactory.createLineBorder(new Color(200, 200, 200));
        Border shadow = new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED);
        button.setBorder(BorderFactory.createCompoundBorder(shadow, line));

        return button;
    }

    private void initTableButtonMap() {
        tableButtonMap.put(1, ban1);
        tableButtonMap.put(2, ban2);
        tableButtonMap.put(3, ban3);
        tableButtonMap.put(4, ban4);
        tableButtonMap.put(5, ban5);
        tableButtonMap.put(6, ban6);
        tableButtonMap.put(7, ban7);
        tableButtonMap.put(8, ban8);
        tableButtonMap.put(9, ban9);
    }

    private void checkAndChangeTableButtonBackGround() {
        for (Integer tableNum : tableButtonMap.keySet()) {
            updateTableColor(tableNum, false);
        }
        
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT TableNumber FROM Orders WHERE OrderType = N'Uống tại quán' AND IsActive = 1";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int tableNumber = rs.getInt("TableNumber");
                if (tableNumber > 0) {
                    updateTableColor(tableNumber, true);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật trạng thái bàn: " + e.getMessage());
        }
    }

    public void CheckAndCreateOrder(int tableNumber, JButton btnBan, int staffId, String staffName, String orderType) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT OrderID, Status, TotalAmount FROM Orders WHERE TableNumber = ? AND OrderType = N'Uống tại quán' AND IsActive = 1";
            
            if ("Mang đi".equals(orderType)) {
                 query = "SELECT OrderID, Status, TotalAmount FROM Orders WHERE OrderID = -1";
            }
            
            PreparedStatement checkPst = conn.prepareStatement(query);
             if (!"Mang đi".equals(orderType)) {
                checkPst.setInt(1, tableNumber);
            }
            ResultSet rs = checkPst.executeQuery();

            if (rs.next()) {
                int existOrderID = rs.getInt("OrderID");
                String orderStatus = rs.getString("Status");
                double totalAmount = rs.getDouble("TotalAmount");
                new OrderFrame(existOrderID, tableNumber, staffId, staffName, orderType, orderStatus, totalAmount, btnBan, this).setVisible(true);
                return;
            }

            String sql = "INSERT INTO Orders (StaffID, TableNumber, OrderType, TotalAmount, IsActive) VALUES (?, ?, ?, 0, 1)";
            PreparedStatement pst = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            pst.setInt(1, staffId);
            if (tableNumber == 0) {
                pst.setNull(2, java.sql.Types.INTEGER);
            } else {
                pst.setInt(2, tableNumber);
            }
            pst.setString(3, orderType);
            pst.executeUpdate();

            ResultSet generatedKeys = pst.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newOrderId = generatedKeys.getInt(1);
                String newOrderStatus = "Chưa thanh toán";
                double newTotalAmount = 0.0;
                if(tableNumber != 0) {
                    updateTableColor(tableNumber, true);
                }
                new OrderFrame(newOrderId, tableNumber, staffId, staffName, orderType, newOrderStatus, newTotalAmount, btnBan, this).setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi xử lý bàn: " + e.getMessage());
        }
    }

    public void updateTableColor(int tableNumber, boolean hasActiveOrder) {
        JButton btn = tableButtonMap.get(tableNumber);
        if (btn != null) {
            btn.setBackground(hasActiveOrder ? ACTIVE_TABLE_COLOR : INACTIVE_TABLE_COLOR);
            btn.setForeground(hasActiveOrder ? Color.WHITE : new Color(50, 50, 50));
        }
    }

    public void resetTableButtonColor(int tableNumber) {
        updateTableColor(tableNumber, false);
    }

    // Variables declaration
    private javax.swing.JButton ban1;
    private javax.swing.JButton ban2;
    private javax.swing.JButton ban3;
    private javax.swing.JButton ban4;
    private javax.swing.JButton ban5;
    private javax.swing.JButton ban6;
    private javax.swing.JButton ban7;
    private javax.swing.JButton ban8;
    private javax.swing.JButton ban9;
    private javax.swing.JButton btnCreateOrder;
    private javax.swing.JButton btnCreateTogoOrder;
    private javax.swing.JButton btnOrderManage;
    private javax.swing.JButton btnUserInfomation;
    private javax.swing.JButton btnlogOut;
    private javax.swing.JPanel sideMenu;
    private javax.swing.JLabel staffNameDisplay;
    private javax.swing.JPanel tableListPanel;
    // End of variables declaration
}
