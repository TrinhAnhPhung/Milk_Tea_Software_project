/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.appsystem.milkteamanage_system.Utils;

import com.appsystem.milkteamanage_system.Staff.BillPanel;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Admin
 */
public class Utils {

    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(regex);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone.matches("^0\\d{9,10}$");
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!#$%&])[A-Za-z\\d!#$%&]{6,20}$";
        return password.matches(regex);
    }

    //load ảnh lên table
    static public class ImageRender extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            if (value instanceof String) {
                String imagePath = (String) value;
                ImageIcon imageIcon = new ImageIcon(imagePath);
                if (imageIcon.getImageLoadStatus() == MediaTracker.COMPLETE) {
                    Image image = imageIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH); // Điều chỉnh kích thước ảnh
                    label.setIcon(new ImageIcon(image));
                } else {
                    label.setText("No Image");
                }
            }
            label.setHorizontalAlignment(JLabel.CENTER);
            return label;
        }
    }

    // show ảnh khi chọn ảnh từ file
    static public void updateAvatarPreview(JLabel label, String path) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) {
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH); // Co lại kích thước
            label.setIcon(new ImageIcon(img));
        } else {
            label.setIcon(null);
            label.setText("Không tải được ảnh");
        }
        label.repaint();
    }

    // background của các dòng theo status ( OrderManage)
    static public class CustomTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = (String) table.getValueAt(row, 6); // Cột Status nằm ở index 6
            if ("chưa thanh toán".equalsIgnoreCase(status)) {
                label.setBackground(new Color(255, 182, 193)); // Màu đỏ nhạt (Light Pink)
            } else if ("đã thanh toán".equalsIgnoreCase(status)) {
                label.setBackground(new Color(144, 238, 144)); // Màu xanh nhạt (Light Green)
            }
            // Đổi màu xám khi chọn hàng
            if (isSelected) {
                label.setBackground(Color.LIGHT_GRAY); // Màu xám nhạt khi selected
            }
            return label;
        }
    }

    // màu các row của table , đen trắng cho dễ nhìn 
    public static class TableBackGroundRender extends DefaultTableCellRenderer {

        private static final Color EVEN_ROW_COLOR = new Color(240, 248, 255);
        private static final Color ODD_ROW_COLOR = new Color(220, 231, 245);
        private static final Color SELECTED_ROW_COLOR = new Color(255, 183, 77);
        private static final Color TEXT_COLOR = new Color(44, 62, 80);
        private static final Color SELECTED_TEXT_COLOR = new Color(255, 255, 255);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setOpaque(true);

            if (hasFocus) {
                setBorder(BorderFactory.createLineBorder(SELECTED_ROW_COLOR, 1));
            } else {
                setBorder(null);
            }

            if (isSelected) {
                // Orange background for selected rows
                cell.setBackground(SELECTED_ROW_COLOR);
                cell.setForeground(SELECTED_TEXT_COLOR);
            } else {
                // Alternate row colors
                cell.setBackground(row % 2 == 0 ? EVEN_ROW_COLOR : ODD_ROW_COLOR);
                cell.setForeground(TEXT_COLOR);
            }

            return cell;
        }
    }

    //format tiền VND
    public static String formatCurrency(double amount) {
        return new java.text.DecimalFormat("#,###").format(amount) + "đ";
    }

    // Phương thức tải dữ liệu hóa đơn
    public static void loadBillData(int orderId, BillPanel billPanel, double discountAmount, String discountCodeName) {
        try (Connection conn = DBConnection.getConnection()) {
            // Header
            String hSql = "SELECT o.TableNumber, o.TotalAmount, o.OrderID, s.FullName, o.OrderDate, o.DiscountID FROM Orders o JOIN Staffs s ON s.StaffID = o.StaffID WHERE o.OrderID = ?";
            PreparedStatement h = conn.prepareStatement(hSql);
            h.setInt(1, orderId);
            ResultSet rh = h.executeQuery();
            if (rh.next()) {
                billPanel.setOrderID(String.valueOf(rh.getInt("OrderID")));
                billPanel.setTableNumber(rh.getInt("TableNumber") == 0 ? "—" : String.valueOf(rh.getInt("TableNumber")));
                billPanel.setCreatedTime(rh.getTimestamp("OrderDate").toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy")));
                billPanel.setStaffName(rh.getString("FullName"));
                billPanel.setTotalPrice(formatCurrency(rh.getDouble("TotalAmount")));

                // Handle discount display
                if (discountCodeName != null && discountAmount > 0) {
                    billPanel.setDiscount(discountCodeName + ": -" + formatCurrency(discountAmount) + " đ");
                } else {
                    billPanel.setDiscount("Không có");
                }

                double finalTotal = rh.getDouble("TotalAmount") - discountAmount;
                billPanel.setFinalTotalPrice(formatCurrency(finalTotal) + " đ");
            }

            // Detail
            DefaultTableModel model = new DefaultTableModel(new Object[]{"STT", "Tên Món", "SL", "Đơn Giá", "Thành Tiền"}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            String dSql = "SELECT p.Name, od.Quantity, od.UnitPrice, od.SubTotal FROM OrderDetails od JOIN Products p ON p.ProductID = od.ProductID WHERE od.OrderID = ?";
            PreparedStatement d = conn.prepareStatement(dSql);
            d.setInt(1, orderId);
            ResultSet rd = d.executeQuery();
            int stt = 1;
            while (rd.next()) {
                String name = rd.getString(1);
                int qty = rd.getInt(2);
                double unit = rd.getDouble(3);
                double tot = rd.getDouble(4);
                model.addRow(new Object[]{stt++, name, qty, formatCurrency(unit), formatCurrency(tot)});
            }
            billPanel.setOrderDetailTable(model);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(billPanel, "Lỗi load bill: " + ex.getMessage());
        }
    }

    // Phương thức hiển thị BillPanel như frame xem trước
    public static void displayBillPanel(int orderId, double discountAmount, String discountCodeName, Component parent) {
        BillPanel billPanel = new BillPanel(orderId, discountAmount, discountCodeName, 0.0, 0.0);
        loadBillData(orderId, billPanel, discountAmount, discountCodeName);
        JDialog billDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(parent), "Hóa Đơn #" + orderId, true);
        billDialog.getContentPane().add(billPanel);
        billDialog.pack();
        billDialog.setLocationRelativeTo(parent);
        billDialog.setVisible(true);
    }

    // Phương thức xuất hóa đơn sang PDF dùng chung
    public static void exportBillToPDF(int orderId, String filePath, Component parent) {
        try (Connection conn = DBConnection.getConnection()) {
            // Lấy thông tin hóa đơn
            String hSql = "SELECT o.TableNumber, o.TotalAmount, o.OrderID, s.FullName, o.OrderDate, o.DiscountID "
                    + "FROM Orders o JOIN Staffs s ON s.StaffID = o.StaffID WHERE o.OrderID = ?";
            PreparedStatement h = conn.prepareStatement(hSql);
            h.setInt(1, orderId);
            ResultSet rh = h.executeQuery();
            if (!rh.next()) {
                JOptionPane.showMessageDialog(parent, "Không tìm thấy hóa đơn!");
                return;
            }

            String orderIdStr = String.valueOf(rh.getInt("OrderID"));
            String tableNumber = rh.getInt("TableNumber") == 0 ? "—" : String.valueOf(rh.getInt("TableNumber"));
            String orderDate = rh.getTimestamp("OrderDate").toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
            String staffName = rh.getString("FullName");
            double totalAmount = rh.getDouble("TotalAmount");
            int discountId = rh.getInt("DiscountID");

            // Lấy thông tin chi tiết hóa đơn
            String dSql = "SELECT p.Name, od.Quantity, od.UnitPrice, od.SubTotal FROM OrderDetails od "
                    + "JOIN Products p ON p.ProductID = od.ProductID WHERE od.OrderID = ?";
            PreparedStatement d = conn.prepareStatement(dSql);
            d.setInt(1, orderId);
            ResultSet rd = d.executeQuery();

            // Lấy thông tin khuyến mãi (nếu có)
            String discountCodeName = "Không có";
            double discountAmount = 0;
            if (discountId != 0) {
                String discountSql = "SELECT Name AS CodeName, DiscountPercent FROM Discounts WHERE DiscountID = ?";
                try (PreparedStatement ds = conn.prepareStatement(discountSql)) {
                    ds.setInt(1, discountId);
                    ResultSet dr = ds.executeQuery();
                    if (dr.next()) {
                        discountCodeName = dr.getString("CodeName");
                        double discountPercent = dr.getDouble("DiscountPercent");
                        discountAmount = totalAmount * (discountPercent / 100.0); // Tính discountAmount dựa trên phần trăm
                    }
                }
            }

            double finalTotal = totalAmount - discountAmount;

            // Tạo PDF
            PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Load font DejaVu Sans
            PdfFont font;
            try {
                font = PdfFontFactory.createFont("src/main/Resources/fonts/DejaVuSans.ttf", "Identity-H", PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, "Không tìm thấy font DejaVuSans.ttf: " + e.getMessage());
                font = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            }

            // Tiêu đề
            document.add(new Paragraph("PHIẾU THANH TOÁN")
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16)
                    .setBold());
            document.add(new Paragraph("Mã HĐ: " + orderIdStr)
                    .setFont(font)
                    .setFontSize(12));
            document.add(new Paragraph("Số Bàn: " + tableNumber)
                    .setFont(font)
                    .setFontSize(12));
            document.add(new Paragraph("Thời gian: " + orderDate)
                    .setFont(font)
                    .setFontSize(12));
            document.add(new Paragraph("Thu ngân: " + staffName)
                    .setFont(font)
                    .setFontSize(12));
            document.add(new Paragraph("\n"));

            // Bảng chi tiết hóa đơn
            float[] columnWidths = {50, 200, 50, 100, 100};
            Table table = new Table(columnWidths);
            table.addHeaderCell(new Paragraph("STT").setFont(font));
            table.addHeaderCell(new Paragraph("Tên Món").setFont(font));
            table.addHeaderCell(new Paragraph("SL").setFont(font));
            table.addHeaderCell(new Paragraph("Đơn Giá").setFont(font));
            table.addHeaderCell(new Paragraph("Thành Tiền").setFont(font));

            int stt = 1;
            while (rd.next()) {
                table.addCell(new Paragraph(String.valueOf(stt++)).setFont(font));
                table.addCell(new Paragraph(rd.getString("Name")).setFont(font));
                table.addCell(new Paragraph(String.valueOf(rd.getInt("Quantity"))).setFont(font));
                table.addCell(new Paragraph(formatCurrency(rd.getDouble("UnitPrice"))).setFont(font));
                table.addCell(new Paragraph(formatCurrency(rd.getDouble("SubTotal"))).setFont(font));
            }
            document.add(table);

            // Thông tin tổng kết
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Thành tiền: " + formatCurrency(totalAmount))
                    .setFont(font)
                    .setFontSize(12));
            document.add(new Paragraph("Khuyến mãi: " + discountCodeName + ": -" + formatCurrency(discountAmount) + " đ")
                    .setFont(font)
                    .setFontSize(12));
            document.add(new Paragraph("Tổng tiền: " + formatCurrency(finalTotal) + " đ")
                    .setFont(font)
                    .setFontSize(12)
                    .setBold());
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Cảm ơn Quý Khách!")
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));

            document.close();
            JOptionPane.showMessageDialog(parent, "Hóa đơn đã được xuất thành công tại: " + filePath);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parent, "Lỗi khi xuất PDF: " + ex.getMessage());
        }
    }

    // Phương thức kết hợp chọn file và xuất PDF
    public static void promptAndExportBillToPDF(int orderId, Component parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu Hóa Đơn PDF");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        fileChooser.setSelectedFile(new File("HoaDon_" + orderId + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        if (!filePath.toLowerCase().endsWith(".pdf")) {
            filePath += ".pdf";
        }

        exportBillToPDF(orderId, filePath, parent);
    }
}
