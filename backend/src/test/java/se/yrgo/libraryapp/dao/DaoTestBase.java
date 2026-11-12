package se.yrgo.libraryapp.dao;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;

/**
 * Ger varje test en egen, tom H2-databas (MySQL-läge) med tabellerna som DAO-klasserna använder.
 * Om projektet redan har ett riktigt schema kan du läsa in det här i stället för SCHEMA.
 */
abstract class DaoTestBase {

    private static final String[] SCHEMA = {
        "CREATE TABLE user (id INT AUTO_INCREMENT PRIMARY KEY, user VARCHAR(50) UNIQUE NOT NULL, "
            + "realname VARCHAR(100) NOT NULL, password_hash VARCHAR(255) NOT NULL)",
        "CREATE TABLE role (id INT PRIMARY KEY, role VARCHAR(20) NOT NULL)",
        "CREATE TABLE user_role (user_id INT NOT NULL, role_id INT NOT NULL)",
        "CREATE TABLE session (id VARCHAR(36) PRIMARY KEY, user_id INT NOT NULL, created TIMESTAMP NOT NULL)",
        "CREATE TABLE dewey_decimal_system (code INT PRIMARY KEY, class VARCHAR(100) NOT NULL)",
        "CREATE TABLE book_edition (isbn VARCHAR(20) PRIMARY KEY, title VARCHAR(200) NOT NULL, "
            + "author VARCHAR(100) NOT NULL, dds_code INT NOT NULL)",
        "CREATE TABLE book (book_id INT AUTO_INCREMENT PRIMARY KEY, isbn VARCHAR(20) NOT NULL)",
        "CREATE TABLE book_loan (book_id INT PRIMARY KEY, borrower_id INT NOT NULL, return_date DATE NOT NULL)",
        "INSERT INTO role VALUES (1, 'ADMIN'), (2, 'USER')"
    };

    protected DataSource ds;

    @BeforeEach
    void createDatabase() {
        JdbcDataSource h2 = new JdbcDataSource();
        h2.setURL("jdbc:h2:mem:" + UUID.randomUUID()
                + ";MODE=MySQL;DB_CLOSE_DELAY=-1");
        ds = h2;
        execute(SCHEMA);
    }

    protected void execute(String... statements) {
        try (Connection conn = ds.getConnection(); Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                stmt.execute(sql);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected int count(String query) {
        try (Connection conn = ds.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new IllegalStateException(ex);
        }
    }
}