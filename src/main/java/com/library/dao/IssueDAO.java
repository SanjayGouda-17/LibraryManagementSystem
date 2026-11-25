package com.library.dao;

import com.library.model.IssuedBook;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Book Issuance and Return operations.
 * Implements JDBC Transaction Management (setAutoCommit(false), commit, rollback)
 * and concurrency-safe conditional SQL updates.
 */
public class IssueDAO {

    /**
     * Issues a book to a member using JDBC Transaction Management.
     * Steps:
     * 1. Validate that member exists.
     * 2. Validate that book exists.
     * 3. Start transaction: conn.setAutoCommit(false)
     * 4. Decrement available_quantity using conditional update: WHERE available_quantity > 0
     *    (Guarantees concurrency safety so two simultaneous requests cannot issue the last copy)
     * 5. Insert new record in issued_books with status = 'ISSUED'
     * 6. Commit transaction.
     * 7. Rollback if any failure occurs.
     */
    public boolean issueBook(int bookId, int memberId, Date issueDate) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // 1. Verify member exists
            String checkMemberSql = "SELECT member_id FROM members WHERE member_id = ?";
            try (PreparedStatement psMember = conn.prepareStatement(checkMemberSql)) {
                psMember.setInt(1, memberId);
                try (ResultSet rs = psMember.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Member with ID " + memberId + " does not exist.");
                    }
                }
            }

            // 2. Verify book exists
            String checkBookSql = "SELECT book_id, available_quantity FROM books WHERE book_id = ?";
            try (PreparedStatement psBook = conn.prepareStatement(checkBookSql)) {
                psBook.setInt(1, bookId);
                try (ResultSet rs = psBook.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Book with ID " + bookId + " does not exist.");
                    }
                    int available = rs.getInt("available_quantity");
                    if (available <= 0) {
                        throw new IllegalStateException("Book is currently out of stock (0 copies available).");
                    }
                }
            }

            // 3. Begin Transaction
            conn.setAutoCommit(false);

            // 4. Decrement available_quantity with concurrency protection
            String updateQtySql = "UPDATE books SET available_quantity = available_quantity - 1 WHERE book_id = ? AND available_quantity > 0";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateQtySql)) {
                psUpdate.setInt(1, bookId);
                int rowsUpdated = psUpdate.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new IllegalStateException("Failed to reserve book copy. It may have just been issued to another user.");
                }
            }

            // 5. Insert transaction record into issued_books
            String insertSql = "INSERT INTO issued_books (book_id, member_id, issue_date, status) VALUES (?, ?, ?, 'ISSUED')";
            try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                psInsert.setInt(1, bookId);
                psInsert.setInt(2, memberId);
                psInsert.setDate(3, issueDate);
                psInsert.executeUpdate();
            }

            // 6. Commit transaction
            conn.commit();
            return true;

        } catch (SQLException | RuntimeException e) {
            // 7. Rollback transaction on any error
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Transaction rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit mode
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Returns an issued book using JDBC Transaction Management.
     * Steps:
     * 1. Find transaction and verify its status is 'ISSUED'.
     * 2. Begin transaction: conn.setAutoCommit(false)
     * 3. Update transaction status to 'RETURNED' and set return_date.
     * 4. Increment available_quantity in books table.
     * 5. Commit transaction.
     * 6. Rollback if any failure occurs.
     */
    public boolean returnBook(int issueId, Date returnDate) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // 1. Check transaction and verify status
            int bookId = 0;
            String checkSql = "SELECT book_id, status FROM issued_books WHERE issue_id = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setInt(1, issueId);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (!rs.next()) {
                        throw new IllegalArgumentException("Transaction ID " + issueId + " not found.");
                    }
                    String currentStatus = rs.getString("status");
                    if ("RETURNED".equalsIgnoreCase(currentStatus)) {
                        throw new IllegalStateException("Book transaction #" + issueId + " is already marked as RETURNED. Duplicate return rejected.");
                    }
                    bookId = rs.getInt("book_id");
                }
            }

            // 2. Begin Transaction
            conn.setAutoCommit(false);

            // 3. Update transaction record
            String updateTxSql = "UPDATE issued_books SET status = 'RETURNED', return_date = ? WHERE issue_id = ? AND status = 'ISSUED'";
            try (PreparedStatement psTx = conn.prepareStatement(updateTxSql)) {
                psTx.setDate(1, returnDate);
                psTx.setInt(2, issueId);
                int rows = psTx.executeUpdate();
                if (rows == 0) {
                    throw new IllegalStateException("Failed to update transaction status. It may have already been returned.");
                }
            }

            // 4. Increment available_quantity
            String updateQtySql = "UPDATE books SET available_quantity = available_quantity + 1 WHERE book_id = ? AND available_quantity < total_quantity";
            try (PreparedStatement psQty = conn.prepareStatement(updateQtySql)) {
                psQty.setInt(1, bookId);
                psQty.executeUpdate();
            }

            // 5. Commit Transaction
            conn.commit();
            return true;

        } catch (SQLException | RuntimeException e) {
            // 6. Rollback on failure
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Return rollback failed: " + ex.getMessage());
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing connection: " + ex.getMessage());
                }
            }
        }
    }

    /**
     * Retrieves currently active issued book transactions (status = 'ISSUED')
     * with Book and Member details using SQL JOIN.
     */
    public List<IssuedBook> getActiveIssuedBooks() throws SQLException {
        return getTransactionsWithFilter("ISSUED", null, null);
    }

    /**
     * Retrieves all transactions (or filtered) with book title and member name using SQL INNER JOIN.
     * 
     * @param statusFilter Optional status filter ("ISSUED", "RETURNED", or null for all)
     * @param memberSearch Optional member name search keyword (or null)
     * @param bookSearch Optional book title search keyword (or null)
     */
    public List<IssuedBook> getTransactionsWithFilter(String statusFilter, String memberSearch, String bookSearch) throws SQLException {
        List<IssuedBook> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT ib.issue_id, ib.book_id, ib.member_id, ib.issue_date, ib.return_date, ib.status, " +
            "       b.title AS book_title, m.name AS member_name, m.email AS member_email " +
            "FROM issued_books ib " +
            "INNER JOIN books b ON ib.book_id = b.book_id " +
            "INNER JOIN members m ON ib.member_id = m.member_id " +
            "WHERE 1=1 "
        );

        List<Object> params = new ArrayList<>();

        if (statusFilter != null && !statusFilter.trim().isEmpty() && !"ALL".equalsIgnoreCase(statusFilter)) {
            sql.append("AND ib.status = ? ");
            params.add(statusFilter.trim());
        }

        if (memberSearch != null && !memberSearch.trim().isEmpty()) {
            sql.append("AND m.name LIKE ? ");
            params.add("%" + memberSearch.trim() + "%");
        }

        if (bookSearch != null && !bookSearch.trim().isEmpty()) {
            sql.append("AND b.title LIKE ? ");
            params.add("%" + bookSearch.trim() + "%");
        }

        sql.append("ORDER BY ib.issue_id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractIssuedBook(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves recent transactions for the dashboard view.
     */
    public List<IssuedBook> getRecentTransactions(int limit) throws SQLException {
        List<IssuedBook> list = new ArrayList<>();
        String sql = "SELECT ib.issue_id, ib.book_id, ib.member_id, ib.issue_date, ib.return_date, ib.status, " +
                     "       b.title AS book_title, m.name AS member_name, m.email AS member_email " +
                     "FROM issued_books ib " +
                     "INNER JOIN books b ON ib.book_id = b.book_id " +
                     "INNER JOIN members m ON ib.member_id = m.member_id " +
                     "ORDER BY ib.issue_id DESC LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(extractIssuedBook(rs));
                }
            }
        }
        return list;
    }

    /**
     * Returns total count of currently issued books (status = 'ISSUED').
     */
    public int getCurrentlyIssuedCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM issued_books WHERE status = 'ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Helper method to map ResultSet row with JOIN fields into an IssuedBook object
    private IssuedBook extractIssuedBook(ResultSet rs) throws SQLException {
        return new IssuedBook(
            rs.getInt("issue_id"),
            rs.getInt("book_id"),
            rs.getInt("member_id"),
            rs.getDate("issue_date"),
            rs.getDate("return_date"),
            rs.getString("status"),
            rs.getString("book_title"),
            rs.getString("member_name"),
            rs.getString("member_email")
        );
    }
}
