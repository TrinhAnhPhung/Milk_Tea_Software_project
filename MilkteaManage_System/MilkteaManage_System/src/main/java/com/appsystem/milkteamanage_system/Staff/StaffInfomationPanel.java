/*
 * Staff Information Panel with button-based navigation and enhanced statistics
 */
package com.appsystem.milkteamanage_system.Staff;

import com.appsystem.milkteamanage_system.UserInfomation.UserInfomationPage;
import com.appsystem.milkteamanage_system.OrderManage.OrderManage;
import com.appsystem.milkteamanage_system.Utils.DBConnection;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class StaffInfomationPanel extends javax.swing.JPanel {

    private int staffId;
    private String staffName;
    private JPanel contentPanel;
    private JPanel personalInfoPanel;
    private JPanel orderManagementPanel;
    private JPanel statisticsPanel;
    private JComboBox<String> statsModeComboBox;
    private JComboBox<Integer> dayComboBox;
    private JComboBox<Integer> monthComboBox;
    private JComboBox<Integer> yearComboBox;
    private JTextField startDateField;
    private JTextField endDateField;
    private JComboBox<String> orderTypeComboBox;
    private ChartPanel chartPanel;
    private JLabel totalRevenueLabel;
    private JLabel orderCountLabel;
    private JLabel statusLabel;
    private JLabel dayLabel;
    private JLabel monthLabel;
    private JLabel yearLabel;
    private JLabel startDateLabel;
    private JLabel endDateLabel;

    public StaffInfomationPanel(int staffId, String staffName) {
        this.staffId = staffId;
        this.staffName = staffName;
        initComponents();
        updateStatsControls(); // Ensure controls are in correct state initially
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Topbar with buttons
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        topBar.setBackground(new Color(70, 130, 180));
        String[] tabs = {"Thông tin cá nhân", "Quản lí hóa đơn", "Thống kê"};
        contentPanel = new JPanel(new CardLayout());
        personalInfoPanel = new JPanel(new BorderLayout());
        orderManagementPanel = new JPanel(new BorderLayout());
        statisticsPanel = new JPanel(new BorderLayout());

        for (String tab : tabs) {
            JButton tabButton = new JButton(tab);
            tabButton.setForeground(Color.WHITE);
            tabButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
            tabButton.setBackground(new Color(70, 130, 180));
            tabButton.setFocusPainted(false);
            tabButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            final String panelName = tab.equals("Thông tin cá nhân") ? "Personal" : tab.equals("Quản lí hóa đơn") ? "Order" : "Stats";
            tabButton.addActionListener(e -> {
                ((CardLayout) contentPanel.getLayout()).show(contentPanel, panelName);
                if (panelName.equals("Personal")) {
                    personalInfoPanel.removeAll();
                    personalInfoPanel.add(createPersonalInfoContent(), BorderLayout.CENTER);
                    personalInfoPanel.revalidate();
                    personalInfoPanel.repaint();
                }
            });
            topBar.add(tabButton);
        }
        add(topBar, BorderLayout.NORTH);

        // Content Panels
        contentPanel.add(personalInfoPanel, "Personal");
        contentPanel.add(orderManagementPanel, "Order");
        contentPanel.add(statisticsPanel, "Stats");
        add(contentPanel, BorderLayout.CENTER);

        // Personal Info Tab
        personalInfoPanel.add(createPersonalInfoContent(), BorderLayout.CENTER);

        // Order Management Tab
        OrderManage orderManage = new OrderManage();
        orderManagementPanel.add(orderManage, BorderLayout.CENTER);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT OrderID, StaffID, DiscountID, TotalAmount, TableNumber, OrderType, Status, IsActive, OrderDate FROM Orders WHERE StaffID = ? ORDER BY OrderDate DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, staffId);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = (DefaultTableModel) orderManage.OrderManageTable.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("OrderID"),
                    rs.getInt("StaffID"),
                    rs.getInt("DiscountID"),
                    rs.getDouble("TotalAmount"),
                    rs.getInt("TableNumber"),
                    rs.getString("OrderType"),
                    rs.getString("Status"),
                    rs.getInt("IsActive"),
                    rs.getTimestamp("OrderDate")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu hóa đơn: " + e.getMessage());
        }

        // Statistics Tab
        JPanel statsNorthPanel = new JPanel(new BorderLayout());
        JPanel statsControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        statsModeComboBox = new JComboBox<>(new String[]{"Theo ngày", "Theo tháng", "Theo năm", "Theo khoảng thời gian"});
        statsModeComboBox.addActionListener(e -> updateStatsControls());
        statsControlsPanel.add(new JLabel("Chế độ xem:"));
        statsControlsPanel.add(statsModeComboBox);

        dayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayComboBox.addItem(i);
        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) monthComboBox.addItem(i);
        yearComboBox = new JComboBox<>();
        
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        for (int i = currentYear - 5; i <= currentYear + 5; i++) {
            yearComboBox.addItem(i);
        }

        dayComboBox.setSelectedItem(currentDay);
        monthComboBox.setSelectedItem(currentMonth);
        yearComboBox.setSelectedItem(currentYear);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(cal.getTime());
        startDateField = new JTextField(todayStr, 10);
        endDateField = new JTextField(todayStr, 10);

        orderTypeComboBox = new JComboBox<>(new String[]{"Tất cả", "Uống tại quán", "Mang đi"});
        
        dayLabel = new JLabel("Ngày:");
        statsControlsPanel.add(dayLabel);
        statsControlsPanel.add(dayComboBox);
        
        monthLabel = new JLabel("Tháng:");
        statsControlsPanel.add(monthLabel);
        statsControlsPanel.add(monthComboBox);

        yearLabel = new JLabel("Năm:");
        statsControlsPanel.add(yearLabel);
        statsControlsPanel.add(yearComboBox);

        startDateLabel = new JLabel("Ngày bắt đầu (YYYY-MM-DD):");
        statsControlsPanel.add(startDateLabel);
        statsControlsPanel.add(startDateField);

        endDateLabel = new JLabel("Ngày kết thúc (YYYY-MM-DD):");
        statsControlsPanel.add(endDateLabel);
        statsControlsPanel.add(endDateField);

        statsControlsPanel.add(new JLabel("Loại đơn:"));
        statsControlsPanel.add(orderTypeComboBox);

        JButton updateButton = new JButton("Cập nhật thống kê");
        updateButton.addActionListener(e -> updateStats());
        statsControlsPanel.add(updateButton);

        statsNorthPanel.add(statsControlsPanel, BorderLayout.NORTH);

        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalRevenueLabel = new JLabel("Tổng Doanh Thu: 0 VND");
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        orderCountLabel = new JLabel("Tổng Số Đơn Hàng: 0");
        orderCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel = new JLabel("Chọn chế độ và cập nhật để xem thống kê.");
        statusLabel.setForeground(Color.BLUE);
        summaryPanel.add(statusLabel);
        summaryPanel.add(totalRevenueLabel);
        summaryPanel.add(orderCountLabel);
        statsNorthPanel.add(summaryPanel, BorderLayout.CENTER);

        statisticsPanel.add(statsNorthPanel, BorderLayout.NORTH);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset(); // Start with empty dataset
        JFreeChart chart = ChartFactory.createBarChart(
            "Thống Kê Doanh Thu", "Thời gian", "Doanh Thu (VND)",
            dataset, PlotOrientation.VERTICAL, true, true, false
        );
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 500));
        statisticsPanel.add(chartPanel, BorderLayout.CENTER);

        ((CardLayout) contentPanel.getLayout()).show(contentPanel, "Personal");
    }

    private JPanel createPersonalInfoContent() {
        JPanel infoContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 245, 245), 0, getHeight(), new Color(230, 230, 230));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        infoContent.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        infoContent.setLayout(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(20, 20, 20, 20);
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT PhoneNumber, Email FROM Staffs WHERE StaffID = ?");
            pstmt.setInt(1, staffId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                JLabel phoneLabel = new JLabel("Số Điện Thoại: " + rs.getString("PhoneNumber"));
                phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                gbc.gridy = 0;
                infoContent.add(phoneLabel, gbc);

                JLabel emailLabel = new JLabel("Email: " + rs.getString("Email"));
                emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                gbc.gridy = 1;
                infoContent.add(emailLabel, gbc);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }

        JButton editButton = new JButton("Chỉnh Sửa Thông Tin");
        editButton.setBackground(new Color(76, 175, 80));
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> new UserInfomationPage(staffId, staffName).setVisible(true));
        gbc.gridy = 2;
        infoContent.add(editButton, gbc);

        JButton changePasswordButton = new JButton("Đổi Mật Khẩu");
        changePasswordButton.setBackground(new Color(255, 183, 77));
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.addActionListener(e -> new UserInfomationPage(staffId, staffName).setVisible(true));
        gbc.gridx = 1; gbc.gridy = 2;
        infoContent.add(changePasswordButton, gbc);

        return infoContent;
    }

    private void updateStatsControls() {
        String mode = (String) statsModeComboBox.getSelectedItem();

        // Determine visibility based on the selected mode
        boolean dayVisible = "Theo ngày".equals(mode);
        boolean monthVisible = "Theo ngày".equals(mode) || "Theo tháng".equals(mode);
        boolean yearVisible = "Theo ngày".equals(mode) || "Theo tháng".equals(mode) || "Theo năm".equals(mode);
        boolean rangeVisible = "Theo khoảng thời gian".equals(mode);

        // Set visibility for Day components
        dayLabel.setVisible(dayVisible);
        dayComboBox.setVisible(dayVisible);

        // Set visibility for Month components
        monthLabel.setVisible(monthVisible);
        monthComboBox.setVisible(monthVisible);

        // Set visibility for Year components
        yearLabel.setVisible(yearVisible);
        yearComboBox.setVisible(yearVisible);

        // Set visibility for Date Range components
        startDateLabel.setVisible(rangeVisible);
        startDateField.setVisible(rangeVisible);
        endDateLabel.setVisible(rangeVisible);
        endDateField.setVisible(rangeVisible);

        statusLabel.setText(rangeVisible ? "Nhập ngày theo định dạng YYYY-MM-DD." : "Chọn chế độ và cập nhật để xem thống kê.");
    }

    private DefaultCategoryDataset createStatsDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String mode = (String) statsModeComboBox.getSelectedItem();
        String selectedOrderType = (String) orderTypeComboBox.getSelectedItem();
        boolean hasData = false;

        try (Connection conn = DBConnection.getConnection()) {
            StringBuilder sqlBuilder;
            PreparedStatement pst;

            if ("Theo ngày".equals(mode)) {
                int day = (int) dayComboBox.getSelectedItem();
                int month = (int) monthComboBox.getSelectedItem();
                int year = (int) yearComboBox.getSelectedItem();
                sqlBuilder = new StringBuilder("SELECT DATEPART(hour, OrderDate) as hour, SUM(TotalAmount) as totalRevenue FROM Orders WHERE StaffID = ? AND DAY(OrderDate) = ? AND MONTH(OrderDate) = ? AND YEAR(OrderDate) = ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ? ");
                }
                sqlBuilder.append("GROUP BY DATEPART(hour, OrderDate) ORDER BY hour");
                
                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setInt(2, day);
                pst.setInt(3, month);
                pst.setInt(4, year);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(5, selectedOrderType);
                }
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    hasData = true;
                    dataset.addValue(rs.getDouble("totalRevenue"), "Doanh Thu", rs.getInt("hour ") +"Giờ" );
                }
            } else if ("Theo tháng".equals(mode)) {
                int month = (int) monthComboBox.getSelectedItem();
                int year = (int) yearComboBox.getSelectedItem();
                sqlBuilder = new StringBuilder("SELECT DAY(OrderDate) as day, SUM(TotalAmount) as totalRevenue FROM Orders WHERE StaffID = ? AND MONTH(OrderDate) = ? AND YEAR(OrderDate) = ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ? ");
                }
                sqlBuilder.append("GROUP BY DAY(OrderDate) ORDER BY day");

                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setInt(2, month);
                pst.setInt(3, year);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(4, selectedOrderType);
                }
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    hasData = true;
                    dataset.addValue(rs.getDouble("totalRevenue"), "Doanh Thu", "Ngày " + rs.getInt("day"));
                }
            } else if ("Theo năm".equals(mode)) {
                int year = (int) yearComboBox.getSelectedItem();
                sqlBuilder = new StringBuilder("SELECT MONTH(OrderDate) as month, SUM(TotalAmount) as totalRevenue FROM Orders WHERE StaffID = ? AND YEAR(OrderDate) = ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ? ");
                }
                sqlBuilder.append("GROUP BY MONTH(OrderDate) ORDER BY month");

                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setInt(2, year);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(3, selectedOrderType);
                }
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    hasData = true;
                    dataset.addValue(rs.getDouble("totalRevenue"), "Doanh Thu", "Tháng " + rs.getInt("month"));
                }
            } else if ("Theo khoảng thời gian".equals(mode)) {
                String startDateStr = startDateField.getText().trim();
                String endDateStr = endDateField.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                java.util.Date startUtilDate, endUtilDate;
                try {
                    startUtilDate = sdf.parse(startDateStr);
                    endUtilDate = sdf.parse(endDateStr);
                } catch (Exception e) {
                    statusLabel.setText("Định dạng ngày không hợp lệ. Vui lòng dùng YYYY-MM-DD.");
                    return dataset;
                }
                java.sql.Date startDate = new java.sql.Date(startUtilDate.getTime());
                java.sql.Date endDate = new java.sql.Date(endUtilDate.getTime());

                sqlBuilder = new StringBuilder("SELECT CAST(OrderDate AS DATE) as date, SUM(TotalAmount) as totalRevenue FROM Orders WHERE StaffID = ? AND CAST(OrderDate AS DATE) BETWEEN ? AND ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ? ");
                }
                sqlBuilder.append("GROUP BY CAST(OrderDate AS DATE) ORDER BY date");

                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setDate(2, startDate);
                pst.setDate(3, endDate);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(4, selectedOrderType);
                }
                ResultSet rs = pst.executeQuery();
                while (rs.next()) {
                    hasData = true;
                    dataset.addValue(rs.getDouble("totalRevenue"), "Doanh Thu", rs.getString("date"));
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Lỗi khi tải thống kê: " + e.getMessage());
            e.printStackTrace();
        }
        
        if (!hasData) {
            statusLabel.setText("Không có dữ liệu cho lựa chọn này.");
        }
        return dataset;
    }

    private void updateStats() {
        statusLabel.setText("Đang tải thống kê...");
        DefaultCategoryDataset dataset = createStatsDataset();
        JFreeChart chart = chartPanel.getChart();
        chart.getCategoryPlot().setDataset(dataset);
        
        double totalRevenue = 0.0;
        int orderCount = 0;
        
        for (int i = 0; i < dataset.getRowCount(); i++) {
            for (int j = 0; j < dataset.getColumnCount(); j++) {
                totalRevenue += dataset.getValue(i, j).doubleValue();
            }
        }

        try (Connection conn = DBConnection.getConnection()) {
            String mode = (String) statsModeComboBox.getSelectedItem();
            String selectedOrderType = (String) orderTypeComboBox.getSelectedItem();
            StringBuilder sqlBuilder;
            PreparedStatement pst = null;

            if ("Theo ngày".equals(mode)) {
                int day = (int) dayComboBox.getSelectedItem();
                int month = (int) monthComboBox.getSelectedItem();
                int year = (int) yearComboBox.getSelectedItem();
                sqlBuilder = new StringBuilder("SELECT COUNT(*) as orderCount FROM Orders WHERE StaffID = ? AND DAY(OrderDate) = ? AND MONTH(OrderDate) = ? AND YEAR(OrderDate) = ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ?");
                }
                
                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setInt(2, day);
                pst.setInt(3, month);
                pst.setInt(4, year);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(5, selectedOrderType);
                }
            } else if ("Theo tháng".equals(mode)) {
                int month = (int) monthComboBox.getSelectedItem();
                int year = (int) yearComboBox.getSelectedItem();
                sqlBuilder = new StringBuilder("SELECT COUNT(*) as orderCount FROM Orders WHERE StaffID = ? AND MONTH(OrderDate) = ? AND YEAR(OrderDate) = ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ?");
                }

                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setInt(2, month);
                pst.setInt(3, year);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(4, selectedOrderType);
                }
            } else if ("Theo năm".equals(mode)) {
                int year = (int) yearComboBox.getSelectedItem();
                sqlBuilder = new StringBuilder("SELECT COUNT(*) as orderCount FROM Orders WHERE StaffID = ? AND YEAR(OrderDate) = ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ?");
                }

                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setInt(2, year);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(3, selectedOrderType);
                }
            } else if ("Theo khoảng thời gian".equals(mode)) {
                String startDateStr = startDateField.getText().trim();
                String endDateStr = endDateField.getText().trim();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);
                java.util.Date startUtilDate, endUtilDate;
                try {
                    startUtilDate = sdf.parse(startDateStr);
                    endUtilDate = sdf.parse(endDateStr);
                } catch (Exception e) {
                    statusLabel.setText("Định dạng ngày không hợp lệ. Vui lòng dùng YYYY-MM-DD.");
                    return;
                }
                java.sql.Date startDate = new java.sql.Date(startUtilDate.getTime());
                java.sql.Date endDate = new java.sql.Date(endUtilDate.getTime());

                sqlBuilder = new StringBuilder("SELECT COUNT(*) as orderCount FROM Orders WHERE StaffID = ? AND CAST(OrderDate AS DATE) BETWEEN ? AND ? ");
                if (!"Tất cả".equals(selectedOrderType)) {
                    sqlBuilder.append("AND OrderType = ?");
                }

                pst = conn.prepareStatement(sqlBuilder.toString());
                pst.setInt(1, staffId);
                pst.setDate(2, startDate);
                pst.setDate(3, endDate);
                if (!"Tất cả".equals(selectedOrderType)) {
                    pst.setString(4, selectedOrderType);
                }
            }

            if (pst != null) {
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    orderCount = rs.getInt("orderCount");
                }
            }
        } catch (Exception e) {
            statusLabel.setText("Lỗi khi tính tổng: " + e.getMessage());
            e.printStackTrace();
        }

        DecimalFormat df = new DecimalFormat("#,###");
        totalRevenueLabel.setText("Tổng Doanh Thu: " + df.format(totalRevenue) + " VND");
        orderCountLabel.setText("Tổng Số Đơn Hàng: " + orderCount);
        
        if (orderCount == 0 && dataset.getColumnCount() == 0) {
            statusLabel.setText("Không có dữ liệu cho lựa chọn này.");
        } else {
            statusLabel.setText("Thống kê đã được cập nhật.");
        }
    }
}
