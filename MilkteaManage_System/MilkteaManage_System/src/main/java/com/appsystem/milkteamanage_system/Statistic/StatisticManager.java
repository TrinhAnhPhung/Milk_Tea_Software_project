package com.appsystem.milkteamanage_system.Statistic;

import com.appsystem.milkteamanage_system.Utils.DBConnection;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class StatisticManager extends JPanel {

    // UI Components
    private JLabel totalRevenueCardLabel;
    private JLabel totalOrdersCardLabel;
    private JLabel bestProductCardLabel;
    private JLabel topStaffCardLabel;
    private ChartPanel revenueChartPanel;
    private ChartPanel topProductsChartPanel;
    private JTable staffRevenueTable;
    private JComboBox<String> statsModeComboBox;
    private JComboBox<Integer> dayComboBox, monthComboBox, yearComboBox;
    private JTextField startDateField, endDateField;
    private JLabel dayLabel, monthLabel, yearLabel, startDateLabel, endDateLabel;

    // Data formatting
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private final DecimalFormat df = new DecimalFormat("#,###");

    public StatisticManager() {
        initComponents();
        updateStatistics(); // Load initial data for today
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(240, 242, 245));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // ======== TOP CONTROLS PANEL ========
        JPanel controlsPanel = new JPanel(new BorderLayout(10, 10));
        controlsPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Bảng Điều Khiển Thống Kê");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(50, 50, 50));
        controlsPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setOpaque(false);
        
        statsModeComboBox = new JComboBox<>(new String[]{"Theo ngày", "Theo tháng", "Theo năm", "Theo khoảng thời gian"});
        dayComboBox = new JComboBox<>();
        for (int i = 1; i <= 31; i++) dayComboBox.addItem(i);
        monthComboBox = new JComboBox<>();
        for (int i = 1; i <= 12; i++) monthComboBox.addItem(i);
        yearComboBox = new JComboBox<>();
        
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        for (int i = currentYear - 5; i <= currentYear + 5; i++) yearComboBox.addItem(i);

        dayComboBox.setSelectedItem(cal.get(Calendar.DAY_OF_MONTH));
        monthComboBox.setSelectedItem(cal.get(Calendar.MONTH) + 1);
        yearComboBox.setSelectedItem(currentYear);
        
        String todayStr = sdf.format(cal.getTime());
        startDateField = new JTextField(todayStr, 10);
        endDateField = new JTextField(todayStr, 10);

        dayLabel = new JLabel("Ngày:");
        monthLabel = new JLabel("Tháng:");
        yearLabel = new JLabel("Năm:");
        startDateLabel = new JLabel("Từ:");
        endDateLabel = new JLabel("Đến:");

        JButton updateButton = new JButton("Cập Nhật");
        updateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateButton.setBackground(new Color(0, 123, 255));
        updateButton.setForeground(Color.WHITE);

        filterPanel.add(new JLabel("Chế độ:"));
        filterPanel.add(statsModeComboBox);
        filterPanel.add(dayLabel);
        filterPanel.add(dayComboBox);
        filterPanel.add(monthLabel);
        filterPanel.add(monthComboBox);
        filterPanel.add(yearLabel);
        filterPanel.add(yearComboBox);
        filterPanel.add(startDateLabel);
        filterPanel.add(startDateField);
        filterPanel.add(endDateLabel);
        filterPanel.add(endDateField);
        filterPanel.add(updateButton);
        
        controlsPanel.add(filterPanel, BorderLayout.CENTER);
        
        statsModeComboBox.addActionListener(e -> updateFilterControls());
        updateButton.addActionListener(e -> updateStatistics());
        
        add(controlsPanel, BorderLayout.NORTH);

        // ======== CENTER DASHBOARD PANEL ========
        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 10));
        dashboardPanel.setOpaque(false);

        // Summary Cards
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        summaryPanel.setOpaque(false);
        totalRevenueCardLabel = new JLabel("0đ", SwingConstants.CENTER);
        totalOrdersCardLabel = new JLabel("0", SwingConstants.CENTER);
        bestProductCardLabel = new JLabel("N/A", SwingConstants.CENTER);
        topStaffCardLabel = new JLabel("N/A", SwingConstants.CENTER);
        summaryPanel.add(createCard("TỔNG DOANH THU", totalRevenueCardLabel, new Color(23, 162, 184)));
        summaryPanel.add(createCard("TỔNG ĐƠN HÀNG", totalOrdersCardLabel, new Color(40, 167, 69)));
        summaryPanel.add(createCard("SẢN PHẨM BÁN CHẠY", bestProductCardLabel, new Color(255, 193, 7)));
        summaryPanel.add(createCard("NHÂN VIÊN XUẤT SẮC", topStaffCardLabel, new Color(220, 53, 69)));
        dashboardPanel.add(summaryPanel, BorderLayout.NORTH);

        // Charts and Table Panel
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 15, 15));
        chartsContainer.setOpaque(false);

        // Revenue Chart
        revenueChartPanel = new ChartPanel(null);
        revenueChartPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        chartsContainer.add(revenueChartPanel);
        
        // Right Panel (Pie Chart and Table)
        JPanel rightPanel = new JPanel(new BorderLayout(10,10));
        rightPanel.setOpaque(false);
        
        topProductsChartPanel = new ChartPanel(null);
        topProductsChartPanel.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        topProductsChartPanel.setPreferredSize(new Dimension(300, 300));
        rightPanel.add(topProductsChartPanel, BorderLayout.NORTH);

        staffRevenueTable = new JTable();
        JScrollPane tableScrollPane = new JScrollPane(staffRevenueTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Doanh Thu Theo Nhân Viên"));
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);

        chartsContainer.add(rightPanel);
        dashboardPanel.add(chartsContainer, BorderLayout.CENTER);
        
        add(dashboardPanel, BorderLayout.CENTER);
        updateFilterControls();
    }

    private JPanel createCard(String title, JLabel valueLabel, Color titleColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, titleColor),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(titleColor);
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void updateFilterControls() {
        String mode = (String) statsModeComboBox.getSelectedItem();
        boolean dayVisible = "Theo ngày".equals(mode);
        boolean monthVisible = "Theo ngày".equals(mode) || "Theo tháng".equals(mode);
        boolean yearVisible = "Theo ngày".equals(mode) || "Theo tháng".equals(mode) || "Theo năm".equals(mode);
        boolean rangeVisible = "Theo khoảng thời gian".equals(mode);

        dayLabel.setVisible(dayVisible); dayComboBox.setVisible(dayVisible);
        monthLabel.setVisible(monthVisible); monthComboBox.setVisible(monthVisible);
        yearLabel.setVisible(yearVisible); yearComboBox.setVisible(yearVisible);
        startDateLabel.setVisible(rangeVisible); startDateField.setVisible(rangeVisible);
        endDateLabel.setVisible(rangeVisible); endDateField.setVisible(rangeVisible);
    }
    
    private void updateStatistics() {
        String mode = (String) statsModeComboBox.getSelectedItem();
        int day = (int) dayComboBox.getSelectedItem();
        int month = (int) monthComboBox.getSelectedItem();
        int year = (int) yearComboBox.getSelectedItem();
        String startDateStr = startDateField.getText();
        String endDateStr = endDateField.getText();

        updateSummaryCards(mode, day, month, year, startDateStr, endDateStr);
        updateRevenueChart(mode, day, month, year, startDateStr, endDateStr);
        updateTopProductsChart(mode, day, month, year, startDateStr, endDateStr);
        updateStaffRevenueTable(mode, day, month, year, startDateStr, endDateStr);
    }

    private void updateSummaryCards(String mode, int day, int month, int year, String start, String end) {
        String sql = "";
        String bestProduct = "N/A";
        String topStaff = "N/A";
        double totalRevenue = 0;
        int totalOrders = 0;

        try (Connection conn = DBConnection.getConnection()) {
            // Get Total Revenue and Orders
            StringBuilder revenueSql = new StringBuilder("SELECT SUM(TotalAmount), COUNT(OrderID) FROM Orders WHERE 1=1 ");
            addDateFilter(revenueSql, mode, day, month, year, start, end);
            PreparedStatement pst = conn.prepareStatement(revenueSql.toString());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                totalRevenue = rs.getDouble(1);
                totalOrders = rs.getInt(2);
            }

            // Get Best Product
            StringBuilder productSql = new StringBuilder("SELECT TOP 1 p.Name FROM OrderDetails od JOIN Products p ON od.ProductID = p.ProductID JOIN Orders o ON od.OrderID = o.OrderID WHERE 1=1 ");
            addDateFilter(productSql, mode, day, month, year, start, end);
            productSql.append(" GROUP BY p.Name ORDER BY SUM(od.Quantity) DESC");
            pst = conn.prepareStatement(productSql.toString());
            rs = pst.executeQuery();
            if (rs.next()) bestProduct = rs.getString(1);

            // Get Top Staff
            StringBuilder staffSql = new StringBuilder("SELECT TOP 1 s.FullName FROM Orders o JOIN Staffs s ON o.StaffID = s.StaffID WHERE 1=1 ");
            addDateFilter(staffSql, mode, day, month, year, start, end);
            staffSql.append(" GROUP BY s.FullName ORDER BY SUM(o.TotalAmount) DESC");
            pst = conn.prepareStatement(staffSql.toString());
            rs = pst.executeQuery();
            if (rs.next()) topStaff = rs.getString(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        totalRevenueCardLabel.setText(df.format(totalRevenue) + "đ");
        totalOrdersCardLabel.setText(String.valueOf(totalOrders));
        bestProductCardLabel.setText(bestProduct);
        topStaffCardLabel.setText(topStaff);
    }

    private void updateRevenueChart(String mode, int day, int month, int year, String start, String end) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String title = "Doanh Thu";
        String categoryAxisLabel = "Thời gian";
        StringBuilder sql = new StringBuilder();

        switch (mode) {
            case "Theo ngày":
                title = "Doanh Thu Theo Giờ Trong Ngày";
                categoryAxisLabel = "Giờ";
                sql.append("SELECT DATEPART(hour, OrderDate) as unit, SUM(TotalAmount) as revenue FROM Orders WHERE 1=1 ");
                addDateFilter(sql, mode, day, month, year, start, end);
                sql.append(" GROUP BY DATEPART(hour, OrderDate) ORDER BY unit");
                break;
            case "Theo tháng":
                title = "Doanh Thu Theo Ngày Trong Tháng";
                categoryAxisLabel = "Ngày";
                sql.append("SELECT DAY(OrderDate) as unit, SUM(TotalAmount) as revenue FROM Orders WHERE 1=1 ");
                addDateFilter(sql, mode, day, month, year, start, end);
                sql.append(" GROUP BY DAY(OrderDate) ORDER BY unit");
                break;
            case "Theo năm":
                title = "Doanh Thu Theo Tháng Trong Năm";
                categoryAxisLabel = "Tháng";
                sql.append("SELECT MONTH(OrderDate) as unit, SUM(TotalAmount) as revenue FROM Orders WHERE 1=1 ");
                addDateFilter(sql, mode, day, month, year, start, end);
                sql.append(" GROUP BY MONTH(OrderDate) ORDER BY unit");
                break;
            case "Theo khoảng thời gian":
                title = "Doanh Thu Theo Ngày";
                categoryAxisLabel = "Ngày";
                sql.append("SELECT CAST(OrderDate AS DATE) as unit, SUM(TotalAmount) as revenue FROM Orders WHERE 1=1 ");
                addDateFilter(sql, mode, day, month, year, start, end);
                sql.append(" GROUP BY CAST(OrderDate AS DATE) ORDER BY unit");
                break;
        }

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dataset.addValue(rs.getDouble("revenue"), "Doanh thu", rs.getString("unit"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createBarChart(title, categoryAxisLabel, "Doanh Thu (VND)", dataset, PlotOrientation.VERTICAL, false, true, false);
        revenueChartPanel.setChart(chart);
    }

    private void updateTopProductsChart(String mode, int day, int month, int year, String start, String end) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        StringBuilder sql = new StringBuilder("SELECT TOP 5 p.Name, SUM(od.Quantity) AS TotalSold FROM OrderDetails od JOIN Products p ON od.ProductID = p.ProductID JOIN Orders o ON od.OrderID = o.OrderID WHERE 1=1 ");
        addDateFilter(sql, mode, day, month, year, start, end);
        sql.append(" GROUP BY p.Name ORDER BY TotalSold DESC");

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dataset.setValue(rs.getString("Name"), rs.getInt("TotalSold"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFreeChart chart = ChartFactory.createPieChart("Top 5 Sản Phẩm Bán Chạy", dataset, true, true, false);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));
        topProductsChartPanel.setChart(chart);
    }
    
    private void updateStaffRevenueTable(String mode, int day, int month, int year, String start, String end) {
        DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Nhân Viên", "Doanh Thu"}, 0);
        StringBuilder sql = new StringBuilder("SELECT s.FullName, SUM(o.TotalAmount) as TotalRevenue FROM Orders o JOIN Staffs s ON o.StaffID = s.StaffID WHERE 1=1 ");
        addDateFilter(sql, mode, day, month, year, start, end);
        sql.append(" GROUP BY s.FullName ORDER BY TotalRevenue DESC");

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pst = conn.prepareStatement(sql.toString())) {
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                tableModel.addRow(new Object[]{rs.getString("FullName"), df.format(rs.getDouble("TotalRevenue")) + "đ"});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        staffRevenueTable.setModel(tableModel);
    }

    private void addDateFilter(StringBuilder sql, String mode, int day, int month, int year, String start, String end) {
        switch (mode) {
            case "Theo ngày":
                sql.append(" AND DAY(OrderDate) = ").append(day)
                   .append(" AND MONTH(OrderDate) = ").append(month)
                   .append(" AND YEAR(OrderDate) = ").append(year);
                break;
            case "Theo tháng":
                sql.append(" AND MONTH(OrderDate) = ").append(month)
                   .append(" AND YEAR(OrderDate) = ").append(year);
                break;
            case "Theo năm":
                sql.append(" AND YEAR(OrderDate) = ").append(year);
                break;
            case "Theo khoảng thời gian":
                sql.append(" AND CAST(OrderDate AS DATE) BETWEEN '").append(start).append("' AND '").append(end).append("'");
                break;
        }
    }
}
