package com.appsystem.milkteamanage_system.Utils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {
    private static HikariDataSource dataSource = null;
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());

    // Khởi tạo connection pool
    static {
        try {
            // Đọc tệp database.properties
            Properties props = new Properties();
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
                if (input == null) {
                    throw new IOException("Không tìm thấy tệp database.properties");
                }
                props.load(input);
            }

            // Cấu hình HikariCP
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setDriverClassName(props.getProperty("db.driver"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.pool.maxSize", "10")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("db.pool.minIdle", "2")));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

            // Khởi tạo data source
            dataSource = new HikariDataSource(config);
            LOGGER.info("Khởi tạo connection pool thành công!");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đọc tệp database.properties", e);
            throw new ExceptionInInitializerError(e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi khởi tạo connection pool", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    // Lấy kết nối từ pool
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Connection pool chưa được khởi tạo!");
        }
        return dataSource.getConnection();
    }

    // Đóng connection pool (gọi khi ứng dụng tắt)
    public static void closeDataSource() {
        if (dataSource != null) {
            dataSource.close();
            LOGGER.info("Đóng connection pool thành công!");
        }
    }
}