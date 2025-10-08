package com.appsystem.milkteamanage_system.Staff;

import com.appsystem.milkteamanage_system.Utils.DBConnection;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class OrderFrame extends javax.swing.JFrame {

    private StaffHomePage parent;
    private int orderId;
    private int tableNumber;
    private int staffId;
    private String orderStatus;
    private JButton tableButton;
    private String orderType;
    private double totalAmount;
    private double finalTotalAmount;
    private Integer appliedDiscountId;
    private double discountAmount = 0.0;

    // UI Colors & Fonts
    private final Color BG_COLOR_MAIN = new Color(245, 247, 250);
    private final Color BG_COLOR_INFO = new Color(255, 255, 255);
    private final Color BG_COLOR_PRODUCTS = new Color(245, 247, 250);
    private final Color BG_COLOR_DETAILS = new Color(255, 255, 255);
    private final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private final Color DANGER_COLOR = new Color(220, 53, 69);
    private final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    private final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_VALUE = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PRICE = new Font("Segoe UI", Font.BOLD, 14);

    public OrderFrame(int orderId, int tableNumber, int staffId, String staffName, String orderType, String orderStatus, double totalAmount, JButton tableButton, StaffHomePage parent) {
        this.orderId = orderId;
        this.tableNumber = tableNumber;
        this.staffId = staffId;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.finalTotalAmount = totalAmount;
        this.tableButton = tableButton;
        this.parent = parent;

        initComponents();

        orderIDLabel.setText(String.valueOf(orderId));
        staffNameLabel.setText(staffName);
        tableNameLabel.setText(orderType.equals("Mang đi") ? "—" : String.valueOf(tableNumber));
        orderTypeLabel.setText(orderType);
        updateStatusLabel(orderStatus);

        loadProducts();
        loadOrderDetails();

        this.setTitle("Chi Tiết Hoá Đơn #" + orderId);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BG_COLOR_MAIN);

        // Main Info Panel (Left)
        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(BG_COLOR_INFO);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoPanel.setPreferredSize(new Dimension(320, 0));

        // Top part with order info
        JPanel orderInfoDetailsPanel = new JPanel(new GridBagLayout());
        orderInfoDetailsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.anchor = GridBagConstraints.WEST;

        orderIDLabel = createValueLabel("");
        staffNameLabel = createValueLabel("");
        tableNameLabel = createValueLabel("");
        orderTypeLabel = createValueLabel("");
        OrderStatusLabel = createValueLabel("");

        gbc.gridx = 0;
        gbc.gridy = 0;
        orderInfoDetailsPanel.add(createLabel("Mã HĐ:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        orderInfoDetailsPanel.add(orderIDLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        orderInfoDetailsPanel.add(createLabel("Nhân Viên:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        orderInfoDetailsPanel.add(staffNameLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        orderInfoDetailsPanel.add(createLabel("Bàn:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        orderInfoDetailsPanel.add(tableNameLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        orderInfoDetailsPanel.add(createLabel("Loại HĐ:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        orderInfoDetailsPanel.add(orderTypeLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        orderInfoDetailsPanel.add(createLabel("Trạng Thái:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        orderInfoDetailsPanel.add(OrderStatusLabel, gbc);

        TitledBorder infoBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Thông Tin Hoá Đơn", TitledBorder.LEFT, TitledBorder.TOP, FONT_TITLE, Color.DARK_GRAY);
        orderInfoDetailsPanel.setBorder(infoBorder);

        // Bottom part with totals and actions
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.setOpaque(false);

        // Totals
        JPanel totalsPanel = new JPanel(new GridBagLayout());
        totalsPanel.setOpaque(false);
        TitledBorder totalBorder = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Thanh Toán", TitledBorder.LEFT, TitledBorder.TOP, FONT_TITLE, Color.DARK_GRAY);
        totalsPanel.setBorder(totalBorder);

        TotalAmountLabel = createValueLabel("");
        DiscountAmountLabel = createValueLabel(Utils.formatCurrency(0));
        DiscountAmountLabel.setForeground(DANGER_COLOR);
        FinalTotalAmountLabel = createValueLabel("");
        FinalTotalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FinalTotalAmountLabel.setForeground(SUCCESS_COLOR);

        gbc.gridx = 0;
        gbc.gridy = 0;
        totalsPanel.add(createLabel("Tổng:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        totalsPanel.add(TotalAmountLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        totalsPanel.add(createLabel("Khuyến mãi:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        totalsPanel.add(DiscountAmountLabel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        totalsPanel.add(createLabel("Thành Tiền:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        totalsPanel.add(FinalTotalAmountLabel, gbc);

        // Buttons
        JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        btnThanhToan = createActionButton("Thanh Toán", SUCCESS_COLOR);
        btnThanhToan.addActionListener(this::btnThanhToanActionPerformed);

        btnApplyDiscount = createActionButton("Áp Mã Khuyến Mãi", PRIMARY_COLOR);
        btnApplyDiscount.addActionListener(this::btnApplyDiscountActionPerformed);

        btnCancelOrder = createActionButton("Huỷ Hoá Đơn", DANGER_COLOR);
        btnCancelOrder.addActionListener(this::btnCancelOrderActionPerformed);

        btnCloseOrder = createActionButton("Đóng", Color.GRAY);
        btnCloseOrder.addActionListener(e -> this.dispose());

        buttonsPanel.add(btnThanhToan);
        buttonsPanel.add(btnApplyDiscount);
        buttonsPanel.add(btnCancelOrder);
        buttonsPanel.add(btnCloseOrder);

        actionPanel.add(totalsPanel, BorderLayout.NORTH);
        actionPanel.add(buttonsPanel, BorderLayout.CENTER);

        infoPanel.add(orderInfoDetailsPanel, BorderLayout.NORTH);
        infoPanel.add(actionPanel, BorderLayout.SOUTH);

        // Product List Panel (Center)
        ProductListPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        ProductListPanel.setBackground(BG_COLOR_PRODUCTS);
        ProductListPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        jScrollPane1 = new JScrollPane(ProductListPanel);
        jScrollPane1.setBorder(BorderFactory.createEmptyBorder());
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(16);

        // Order Details Panel (Right)
        OrderDetailPanel = new JPanel();
        OrderDetailPanel.setLayout(new BoxLayout(OrderDetailPanel, BoxLayout.Y_AXIS));
        OrderDetailPanel.setBackground(BG_COLOR_DETAILS);
        OrderDetailPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jScrollPane2 = new JScrollPane(OrderDetailPanel);
        jScrollPane2.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(220, 220, 220)));
        jScrollPane2.setPreferredSize(new Dimension(450, 0));

        // Main Layout
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(infoPanel, BorderLayout.WEST);
        getContentPane().add(jScrollPane1, BorderLayout.CENTER);
        getContentPane().add(jScrollPane2, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_VALUE);
        return label;
    }

    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return button;
    }

    private void updateStatusLabel(String status) {
        OrderStatusLabel.setText(status);
        if ("Đã thanh toán".equalsIgnoreCase(status)) {
            OrderStatusLabel.setForeground(SUCCESS_COLOR);
            btnThanhToan.setEnabled(false);
            btnApplyDiscount.setEnabled(false);
            btnCancelOrder.setEnabled(false);
        } else {
            OrderStatusLabel.setForeground(new Color(255, 152, 0)); // Orange for pending
            btnThanhToan.setEnabled(true);
            btnApplyDiscount.setEnabled(true);
            btnCancelOrder.setEnabled(true);
        }
    }

    private void btnCancelOrderActionPerformed(java.awt.event.ActionEvent evt) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn huỷ đơn?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                String sqlDetail = "DELETE FROM OrderDetails WHERE OrderID = ?";
                try (PreparedStatement pst1 = conn.prepareStatement(sqlDetail)) {
                    pst1.setInt(1, orderId);
                    pst1.executeUpdate();
                }
                String sqlOrder = "DELETE FROM Orders WHERE OrderID = ?";
                try (PreparedStatement pst2 = conn.prepareStatement(sqlOrder)) {
                    pst2.setInt(1, orderId);
                    pst2.executeUpdate();
                }
                conn.commit();
                if (parent != null && tableNumber > 0) {
                    parent.resetTableButtonColor(tableNumber);
                }
                this.dispose();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi huỷ đơn: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void btnThanhToanActionPerformed(java.awt.event.ActionEvent evt) {
        // 1. Check if order is empty
        try (Connection conn = DBConnection.getConnection(); PreparedStatement checkStmt = conn.prepareStatement("SELECT COUNT(*) FROM OrderDetails WHERE OrderID = ?")) {
            checkStmt.setInt(1, orderId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                JOptionPane.showMessageDialog(this, "Đơn hàng trống, không thể thanh toán!");
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra đơn hàng: " + e.getMessage());
            return;
        }

        // 2. Get cash from customer
        double cashReceived = 0;
        while (true) {
            String cashStr = JOptionPane.showInputDialog(this, "Thành tiền: " + Utils.formatCurrency(finalTotalAmount) + "\n\nNhập tiền khách đưa:", "Thanh Toán", JOptionPane.PLAIN_MESSAGE);
            if (cashStr == null) { // User cancelled
                return;
            }
            try {
                cashReceived = Double.parseDouble(cashStr);
                if (cashReceived < this.finalTotalAmount) {
                    JOptionPane.showMessageDialog(this, "Số tiền khách đưa phải lớn hơn hoặc bằng thành tiền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    continue; // Ask again
                }
                break; // Valid input, exit loop
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập một số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 3. Calculate change
        double change = cashReceived - this.finalTotalAmount;

        // 4. Proceed with DB update
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String updateSql = "UPDATE Orders SET Status = N'Đã thanh toán', IsActive = 0, DiscountID = ? WHERE OrderID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    if (appliedDiscountId != null) {
                        updateStmt.setInt(1, appliedDiscountId);
                    } else {
                        updateStmt.setNull(1, java.sql.Types.INTEGER);
                    }
                    updateStmt.setInt(2, orderId);
                    updateStmt.executeUpdate();
                }

                conn.commit();
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!\nTiền thối lại: " + Utils.formatCurrency(change));
                updateStatusLabel("Đã thanh toán");
                if (parent != null && tableNumber > 0) {
                    parent.updateTableColor(tableNumber, false);
                }

                // 5. Display bill and set up close action
                BillPanel billPanel = new BillPanel(orderId, discountAmount, getDiscountNameById(appliedDiscountId), cashReceived, change);
                JDialog billDialog = new JDialog(this, "Hóa Đơn #" + orderId, true);
                billDialog.getContentPane().add(billPanel);
                billDialog.pack();
                billDialog.setLocationRelativeTo(this);

                // Add listener to close OrderFrame when bill dialog closes
                billDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosed(java.awt.event.WindowEvent e) {
                        OrderFrame.this.dispose();
                    }
                });

                billDialog.setVisible(true);

            } catch (SQLException ex) {
                conn.rollback();
                JOptionPane.showMessageDialog(this, "Lỗi thanh toán: " + ex.getMessage());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        }
    }

    private void btnApplyDiscountActionPerformed(java.awt.event.ActionEvent evt) {
        String code = JOptionPane.showInputDialog(this, "Nhập mã khuyến mãi:", "Áp Dụng Khuyến Mãi", JOptionPane.PLAIN_MESSAGE);
        if (code == null) {
            return; // User cancelled
        }
        if (code.trim().isEmpty()) {
            // User entered nothing, so we remove any existing discount
            this.appliedDiscountId = null;
            this.discountAmount = 0.0;
            updateTotalAmountLabels();
            JOptionPane.showMessageDialog(this, "Đã gỡ bỏ khuyến mãi.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT DiscountPercent FROM Discounts WHERE DiscountID = ? AND StartDate <= GETDATE() AND EndDate >= GETDATE()";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, code.trim());
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                double percent = rs.getDouble("DiscountPercent");
                this.discountAmount = this.totalAmount * (percent / 100.0);
                updateTotalAmountLabels();
                JOptionPane.showMessageDialog(this, "Áp dụng mã thành công!");
            } else {
                this.appliedDiscountId = null;
                this.discountAmount = 0.0;
                updateTotalAmountLabels();
                JOptionPane.showMessageDialog(this, "Mã khuyến mãi không hợp lệ hoặc đã hết hạn!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra mã khuyến mãi: " + e.getMessage());
        }
    }

    private void loadProducts() {
        ProductListPanel.removeAll();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM Products WHERE Status = N'Còn Bán'";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                JPanel card = createProductCard(
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getDouble("Price"),
                        rs.getString("ImgPath")
                );
                ProductListPanel.add(card);
            }
            ProductListPanel.revalidate();
            ProductListPanel.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải sản phẩm: " + e.getMessage());
        }
    }

    private JPanel createProductCard(int productId, String name, double price, String imgPath) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 224, 224)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setPreferredSize(new Dimension(120, 120));
        try {
            ImageIcon icon = new ImageIcon(imgPath);
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            lblImage.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblImage.setText("No Image");
            lblImage.setFont(FONT_LABEL);
        }

        JLabel lblName = new JLabel(name, JLabel.CENTER);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblPrice = new JLabel(Utils.formatCurrency(price), JLabel.CENTER);
        lblPrice.setFont(FONT_PRICE);
        lblPrice.setForeground(DANGER_COLOR);

        JButton btnAdd = new JButton("Thêm vào đơn");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setBackground(PRIMARY_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            JDialog dialog = new JDialog(OrderFrame.this, "Thêm sản phẩm", true);
            dialog.setContentPane(new AddProductToOrder(orderId, productId, name, price, this::loadOrderDetails));
            dialog.pack();
            dialog.setLocationRelativeTo(OrderFrame.this);
            dialog.setVisible(true);
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false);
                updateOrderTotal(conn);
                conn.commit();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(OrderFrame.this, "Lỗi cập nhật tổng đơn hàng: " + ex.getMessage());
            }
            loadOrderDetails();
        });

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblPrice);

        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPrice.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblImage, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    protected void loadOrderDetails() {
        OrderDetailPanel.removeAll();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT od.OrderDetailID, p.Name, p.ImgPath, od.Quantity, od.UnitPrice, od.SubTotal "
                    + "FROM OrderDetails od JOIN Products p ON od.ProductID = p.ProductID "
                    + "WHERE od.OrderID = ? ORDER BY od.OrderDetailID";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                JPanel item = createOrderItemPanel(
                        rs.getInt("OrderDetailID"),
                        rs.getString("Name"),
                        rs.getInt("Quantity"),
                        rs.getDouble("UnitPrice"),
                        rs.getString("ImgPath")
                );
                OrderDetailPanel.add(item);
                OrderDetailPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi load OrderDetails: " + ex.getMessage());
        }
        OrderDetailPanel.revalidate();
        OrderDetailPanel.repaint();
        updateTotalAmountLabels();
    }

    private JPanel createOrderItemPanel(int detailId, String name, int qty, double unitPrice, String imgPath) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
        itemPanel.setBackground(new Color(248, 249, 250));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel pic = new JLabel();
        pic.setPreferredSize(new Dimension(60, 60));
        pic.setHorizontalAlignment(SwingConstants.CENTER);
        if (imgPath != null && !imgPath.isBlank()) {
            try {
                ImageIcon icon = new ImageIcon(imgPath);
                Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                pic.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                pic.setText("N/A");
            }
        } else {
            pic.setText("N/A");
        }

        itemPanel.add(pic, BorderLayout.WEST);

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel lblPrice = new JLabel(Utils.formatCurrency(unitPrice) + " x " + qty + " = " + Utils.formatCurrency(unitPrice * qty));
        lblPrice.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPrice.setForeground(Color.GRAY);

        centerPanel.add(lblName);
        centerPanel.add(Box.createVerticalStrut(5));
        centerPanel.add(lblPrice);
        itemPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setOpaque(false);

        SpinnerNumberModel model = new SpinnerNumberModel(qty, 1, 99, 1);
        JSpinner spQty = new JSpinner(model);
        spQty.setPreferredSize(new Dimension(50, 28));
        spQty.setFont(FONT_LABEL);
        spQty.addChangeListener(e -> {
            updateQuantity(detailId, (int) spQty.getValue());
        });

        JButton btnDel = new JButton("Xoá");
        btnDel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnDel.setBackground(DANGER_COLOR);
        btnDel.setForeground(Color.WHITE);
        btnDel.setMargin(new Insets(5, 10, 5, 10));
        btnDel.addActionListener(e -> deleteItem(detailId));

        controlPanel.add(new JLabel("SL:"));
        controlPanel.add(spQty);
        controlPanel.add(btnDel);
        itemPanel.add(controlPanel, BorderLayout.EAST);

        return itemPanel;
    }

    private void deleteItem(int detailId) {
        if (JOptionPane.showConfirmDialog(this, "Xoá sản phẩm này?", "Xác nhận", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement pst = conn.prepareStatement("DELETE FROM OrderDetails WHERE OrderDetailID = ?");
            pst.setInt(1, detailId);
            pst.executeUpdate();
            updateOrderTotal(conn);
            conn.commit();
            loadOrderDetails();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi xoá món: " + e.getMessage());
        }
    }

    private void updateQuantity(int detailId, int newQty) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            PreparedStatement pst = conn.prepareStatement("UPDATE OrderDetails SET Quantity = ? WHERE OrderDetailID = ?");
            pst.setInt(1, newQty);
            pst.setInt(2, detailId);
            pst.executeUpdate();
            updateOrderTotal(conn);
            conn.commit();
            loadOrderDetails();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật SL: " + e.getMessage());
        }
    }

    private void updateOrderTotal(Connection conn) throws Exception {
        PreparedStatement pst = conn.prepareStatement("UPDATE Orders SET TotalAmount = (SELECT ISNULL(SUM(SubTotal),0) FROM OrderDetails WHERE OrderID = ?) WHERE OrderID = ?");
        pst.setInt(1, orderId);
        pst.setInt(2, orderId);
        pst.executeUpdate();
    }

    private void updateTotalAmountLabels() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT TotalAmount FROM Orders WHERE OrderID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, orderId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                this.totalAmount = rs.getDouble("TotalAmount");
            }
        } catch (Exception e) {
            this.totalAmount = 0;
            JOptionPane.showMessageDialog(this, "Lỗi tính tổng tiền: " + e.getMessage());
        }

        // Recalculate discount based on the new totalAmount
        if (this.appliedDiscountId != null) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT DiscountPercent FROM Discounts WHERE DiscountID = ?";
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setInt(1, this.appliedDiscountId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    double percent = rs.getDouble("DiscountPercent");
                    this.discountAmount = this.totalAmount * (percent / 100.0);
                }
            } catch (Exception e) {
                this.discountAmount = 0; // Reset on error
            }
        }

        this.finalTotalAmount = this.totalAmount - this.discountAmount;

        TotalAmountLabel.setText(Utils.formatCurrency(this.totalAmount));
        DiscountAmountLabel.setText("- " + Utils.formatCurrency(this.discountAmount));
        FinalTotalAmountLabel.setText(Utils.formatCurrency(this.finalTotalAmount));
    }

    private String getDiscountNameById(Integer discountId) {
        if (discountId == null) {
            return "Không có";
        }
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT Name FROM Discounts WHERE DiscountID = ?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, discountId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("Name");
            }
        } catch (Exception e) {
            // Log error if necessary
        }
        return "Không có";
    }

    // Variables declaration
    private javax.swing.JPanel infoPanel;
    private javax.swing.JLabel FinalTotalAmountLabel;
    private javax.swing.JLabel DiscountAmountLabel;
    private javax.swing.JPanel OrderDetailPanel;
    private javax.swing.JLabel OrderStatusLabel;
    private javax.swing.JPanel ProductListPanel;
    private javax.swing.JLabel TotalAmountLabel;
    private javax.swing.JButton btnCancelOrder;
    private javax.swing.JButton btnCloseOrder;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnApplyDiscount;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel orderIDLabel;
    private javax.swing.JLabel orderTypeLabel;
    private javax.swing.JLabel staffNameLabel;
    private javax.swing.JLabel tableNameLabel;
    // End of variables declaration
}
