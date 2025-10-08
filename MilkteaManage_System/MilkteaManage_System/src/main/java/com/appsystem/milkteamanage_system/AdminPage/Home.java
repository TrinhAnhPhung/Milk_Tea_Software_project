/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.appsystem.milkteamanage_system.AdminPage;

import com.appsystem.milkteamanage_system.UserManage.UserManage;
import com.appsystem.milkteamanage_system.DiscountManage.DiscountManage;
import com.appsystem.milkteamanage_system.Login;
import com.appsystem.milkteamanage_system.OrderManage.OrderManage;
import com.appsystem.milkteamanage_system.ProductManager.productmanager;
import com.appsystem.milkteamanage_system.Statistic.StatisticManager;
import com.appsystem.milkteamanage_system.UserInfomation.UserInfomationPage;
import com.appsystem.milkteamanage_system.Utils.Utils;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Home extends javax.swing.JFrame {

    private JPanel mainContentPanel;
    private JButton activeButton;
    private JButton dashboardButton;
    private JButton usersButton;
    private JButton productsButton;
    private JButton ordersButton;
    private JButton discountButton;
    private JButton statsButton;
    private JButton userInfomationButton;
    private JButton optionsButton;
    private JButton logoutButton;
    private String staffName;
    private int staffID;

    public Home(String staffName, int staffID) throws IOException {
        this.staffName = staffName;
        this.staffID = staffID;
        setTitle("Hệ thống quản lí bán trà sữa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        JPanel rootPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setPaint(new Color(240, 242, 245));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        setContentPane(rootPanel);

        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(0, 180, 219), 0, getHeight(), new Color(0, 131, 176));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));

        JPanel logoContainer = new JPanel(new BorderLayout(0, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setPaint(new Color(255, 255, 255, 25));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        logoContainer.setOpaque(false);
        logoContainer.setBorder(new EmptyBorder(15, 10, 15, 10));
        logoContainer.setPreferredSize(new Dimension(280, 200));
        logoContainer.setMaximumSize(new Dimension(280, 200));

        JLabel logoImage = new JLabel();
        logoImage.setHorizontalAlignment(SwingConstants.CENTER);
        logoImage.setOpaque(false);
        Utils.updateAvatarPreview(logoImage, "src/main/Resources/images/milk-tea.png");

        JLabel logoLabel = new JLabel("Chào bạn, " + staffName);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoContainer.add(logoImage, BorderLayout.CENTER);
        logoContainer.add(logoLabel, BorderLayout.SOUTH);

        JPanel mainMenu = new JPanel();
        mainMenu.setLayout(new GridLayout(0, 1, 0, 10));
        mainMenu.setOpaque(false);
        mainMenu.setBorder(new EmptyBorder(20, 15, 20, 15));

        dashboardButton = createNavButton("Dashboard", "🏠", e -> {
            showDashboard();
            setActiveButton(dashboardButton);
        });
        mainMenu.add(dashboardButton);

        usersButton = createNavButton("Quản Lí Nhân Viên", "👥", e -> {
            showUsersManage();
            setActiveButton(usersButton);
        });
        mainMenu.add(usersButton);

        productsButton = createNavButton("Quản Lí Hàng Hoá", "📦", e -> {
            showProductsManage();
            setActiveButton(productsButton);
        });
        mainMenu.add(productsButton);

        ordersButton = createNavButton("Quản Lí Đơn Hàng", "📝", e -> {
            showOrdersManage();
            setActiveButton(ordersButton);
        });
        mainMenu.add(ordersButton);

        discountButton = createNavButton("Quản Lí Khuyến Mãi", "🎁️", e -> {
            showDiscountManage();
            setActiveButton(discountButton);
        });
        mainMenu.add(discountButton);

        statsButton = createNavButton("Thống Kê", "📈", e -> {
            showStatisticManage();
            setActiveButton(statsButton);
        });
        mainMenu.add(statsButton);

        JPanel additionalMenu = new JPanel();
        additionalMenu.setLayout(new BoxLayout(additionalMenu, BoxLayout.Y_AXIS));
        additionalMenu.setOpaque(false);
        additionalMenu.setBorder(new EmptyBorder(20, 15, 20, 15));

        userInfomationButton = createNavButton("Thông Tin Cá Nhân", "plus-icon.png", e -> {
            new UserInfomationPage(staffID, staffName).setVisible(true);
            setActiveButton(userInfomationButton);
        });
        additionalMenu.add(userInfomationButton);
        additionalMenu.add(Box.createVerticalStrut(10));

        logoutButton = createNavButton("Đăng Xuất", "logout-icon.png", e -> {
            setActiveButton(logoutButton);
            this.dispose();
            Login lg = new Login();
            lg.setVisible(true);
        });
        additionalMenu.add(logoutButton);

        sidebar.add(logoContainer);
        sidebar.add(mainMenu);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(additionalMenu);

        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(new Color(240, 242, 245));

        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(mainContentPanel, BorderLayout.CENTER);

        showDashboard();
        setActiveButton(dashboardButton);
    }

    private JButton createNavButton(String text, String iconPath, java.awt.event.ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (this == activeButton) {
                    g2d.setColor(new Color(255, 255, 255, 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    g2d.setColor(new Color(255, 210, 0));
                    g2d.fillRect(0, 0, 5, getHeight());
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel tempLabel = new JLabel();
        Utils.updateAvatarPreview(tempLabel, "src/main/Resources/images/" + iconPath);
        if (tempLabel.getIcon() != null) {
            button.setIcon(tempLabel.getIcon());
        } else if (iconPath.length() <= 2 && !iconPath.contains(".")) {
            BufferedImage image = new BufferedImage(30, 30, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
            FontMetrics fm = g2d.getFontMetrics();
            int x = (30 - fm.stringWidth(iconPath)) / 2;
            int y = ((30 - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(iconPath, x, y);
            g2d.dispose();
            button.setIcon(new ImageIcon(image));
        }
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(15);
        if (action != null) {
            button.addActionListener(action);
        }
        return button;
    }

    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.repaint();
        }
        activeButton = button;
        button.repaint();
    }

    private void showDashboard() {
        mainContentPanel.removeAll();
        Dashboard dashboardPanel = new Dashboard();
        mainContentPanel.add(dashboardPanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showUsersManage() {
        mainContentPanel.removeAll();
        UserManage userManagePanel = new UserManage();
        mainContentPanel.add(userManagePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showProductsManage() {
        mainContentPanel.removeAll();
        productmanager productManagePanel = new productmanager();
        mainContentPanel.add(productManagePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showOrdersManage() {
        mainContentPanel.removeAll();
        OrderManage orderManagePanel = new OrderManage();
        mainContentPanel.add(orderManagePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showDiscountManage() {
        mainContentPanel.removeAll();
        DiscountManage discountManagePanel = new DiscountManage();
        mainContentPanel.add(discountManagePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    private void showStatisticManage() {
        mainContentPanel.removeAll();
        StatisticManager statisticManagePanel = new StatisticManager();
        mainContentPanel.add(statisticManagePanel, BorderLayout.CENTER);
        mainContentPanel.revalidate();
        mainContentPanel.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 496, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 348, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Login.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

              
