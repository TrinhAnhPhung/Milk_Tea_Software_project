/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.appsystem.milkteamanage_system.AdminPage;

import com.appsystem.milkteamanage_system.Utils.DBConnection;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Dashboard extends javax.swing.JPanel {

    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        setLayout(new BorderLayout());

        // Main panel with a light gradient background
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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setPreferredSize(new Dimension(900, 700));

        // Nội dung trung gian với BoxLayout để xếp dọc
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Search bar panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel searchBox = new JPanel(new BorderLayout(5, 0));
        searchBox.setBorder(new LineBorder(new Color(70, 130, 180), 2, true));
        searchBox.setBackground(Color.WHITE);
        JLabel searchIcon = new JLabel(new ImageIcon("src/main/resources/images/search-icon.png"));
        searchIcon.setBorder(new EmptyBorder(0, 10, 0, 0));
        JTextField searchField = new JTextField("Tìm kiếm...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 16));
        searchField.setForeground(new Color(100, 100, 100));
        searchField.setBorder(null);
        searchField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (searchField.getText().equals("Tìm kiếm...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm...");
                    searchField.setForeground(new Color(100, 100, 100));
                }
            }
        });
        searchBox.add(searchIcon, BorderLayout.WEST);
        searchBox.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBox, BorderLayout.CENTER);

        // Stats cards panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel stockCard = createStatsCard("Tổng Lời", "2000$", "Tăng 60%", "src/main/resources/images/box-icon.png",
                new Color(135, 206, 250), new Color(70, 130, 180));
        statsPanel.add(stockCard);

        JPanel profitCard = createStatsCard("Lợi Nhuận", "1500$", "Tăng 25%", "src/main/resources/images/dollar-icon.png",
                new Color(144, 238, 144), new Color(34, 139, 34));
        statsPanel.add(profitCard);

        JPanel visitorsCard = createStatsCard("Lượng đơn hàng", "30051", "Tăng 70%", "src/main/resources/images/users-icon.png",
                new Color(255, 218, 185), new Color(255, 140, 0));
        statsPanel.add(visitorsCard);

        // Charts section
        JPanel chartsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        chartsPanel.setOpaque(false);
        chartsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        chartsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Doanh thu theo tháng
        DefaultCategoryDataset revenueDataset = createRevenueDataset();
        JFreeChart revenueChart = ChartFactory.createBarChart(
                "Doanh Thu Theo Tháng", "Tháng", "Doanh Thu (VND)",
                revenueDataset, PlotOrientation.VERTICAL, true, true, false
        );
        ChartPanel revenueChartPanel = new ChartPanel(revenueChart);
        revenueChartPanel.setPreferredSize(new Dimension(300, 300));
        chartsPanel.add(revenueChartPanel);

        // Sản phẩm bán chạy
        DefaultCategoryDataset topProductsDataset = createTopProductsDataset();
        JFreeChart topProductsChart = ChartFactory.createBarChart(
                "Top 5 Sản Phẩm Bán Chạy", "Sản Phẩm", "Số Lượng",
                topProductsDataset, PlotOrientation.VERTICAL, true, true, false
        );
        ChartPanel topProductsChartPanel = new ChartPanel(topProductsChart);
        topProductsChartPanel.setPreferredSize(new Dimension(300, 300));
        chartsPanel.add(topProductsChartPanel);

        // Số lượng đơn hàng theo ngày
        DefaultCategoryDataset ordersDataset = createOrdersDataset();
        JFreeChart ordersChart = ChartFactory.createLineChart(
                "Đơn Hàng Theo Ngày", "Ngày", "Số Lượng",
                ordersDataset, PlotOrientation.VERTICAL, true, true, false
        );
        ChartPanel ordersChartPanel = new ChartPanel(ordersChart);
        ordersChartPanel.setPreferredSize(new Dimension(300, 300));
        chartsPanel.add(ordersChartPanel);

        // Recent Orders Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel tableTitle = new JLabel("Recent Orders");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 18));
        tableTitle.setForeground(new Color(30, 30, 30));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        String[] columnNames = {"OrderID", "StaffID", "DiscountID", "TotalAmount", "TableNumber"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable userTable = new JTable(tableModel);
        userTable.setFillsViewportHeight(true);
        userTable.setGridColor(new Color(200, 200, 200));
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(new Color(70, 130, 180));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setDefaultRenderer(Object.class, new Utils.TableBackGroundRender());
        userTable.setOpaque(true);
        userTable.setBackground(new Color(70, 130, 180));
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setBorder(new LineBorder(new Color(70, 130, 180), 2));
        tableScrollPane.setOpaque(true);
        tableScrollPane.getViewport().setOpaque(true);
        tableScrollPane.getViewport().setBackground(new Color(70, 130, 180));
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Thêm các panel vào contentPanel
        contentPanel.add(searchPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(chartsPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(tablePanel);

        // Bọc contentPanel trong JScrollPane để có thể cuộn dọc
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Tốc độ cuộn mượt mà hơn

        // Thêm scrollPane vào mainPanel thay vì contentPanel trực tiếp
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private DefaultCategoryDataset createRevenueDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT MONTH(OrderDate) AS Month, SUM(TotalAmount) AS Revenue "
                    + "FROM Orders WHERE YEAR(OrderDate) = YEAR(GETDATE()) GROUP BY MONTH(OrderDate)";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dataset.addValue(rs.getDouble("Revenue"), "Doanh Thu", "Tháng " + rs.getInt("Month"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private DefaultCategoryDataset createTopProductsDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT TOP 5 p.Name, SUM(od.Quantity) AS TotalSold "
                    + "FROM OrderDetails od JOIN Products p ON od.ProductID = p.ProductID "
                    + "GROUP BY p.Name ORDER BY TotalSold DESC";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dataset.addValue(rs.getInt("TotalSold"), "Số Lượng", rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private DefaultCategoryDataset createOrdersDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT DAY(OrderDate) AS Day, COUNT(*) AS OrderCount "
                    + "FROM Orders WHERE MONTH(OrderDate) = MONTH(GETDATE()) AND YEAR(OrderDate) = YEAR(GETDATE()) "
                    + "GROUP BY DAY(OrderDate)";
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                dataset.addValue(rs.getInt("OrderCount"), "Đơn Hàng", "Ngày " + rs.getInt("Day"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataset;
    }

    private JPanel createStatsCard(String title, String value, String subtitle, String iconPath,
            Color gradientStart, Color gradientEnd) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, gradientStart, 0, getHeight(), gradientEnd);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(gradientEnd, 2, true),
                new EmptyBorder(10, 10, 10, 10)));

        JLabel iconLabel = new JLabel(new ImageIcon(iconPath));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(240, 240, 240));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(iconLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(10));

        return card;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Dashboard");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(900, 700));
            frame.add(new Dashboard());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
