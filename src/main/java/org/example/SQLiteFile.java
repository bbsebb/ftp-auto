package org.example;

import java.sql.*;

public class SQLiteFile implements AutoCloseable{

    private final Connection conn;
    public SQLiteFile() throws SQLException {
        this("files.db");
    }

    public SQLiteFile(String fileName) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found", e);
        }
        String url = "jdbc:sqlite:" + fileName;
        this.conn = DriverManager.getConnection(url);
        this.createNewDatabase();
    }

    public void createNewDatabase() throws SQLException {
            if (conn != null) {
                try (Statement stmt = conn.createStatement()) {
                    // Création de la table 'files'
                    String sql = "CREATE TABLE IF NOT EXISTS files (name TEXT PRIMARY KEY)";
                    stmt.execute(sql);
                }
            }
    }

    public void saveFileName(String fileName) {
        String sql = "INSERT INTO files(name) VALUES(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fileName);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean fileNameExists(String fileName) {
        String sql = "SELECT name FROM files WHERE name = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, fileName);
            ResultSet rs = pstmt.executeQuery();
            // Si le fichier est trouvé, return true
            return rs.next();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public void close() throws Exception {
        if(conn != null) {
            conn.close();
        }
    }
}
