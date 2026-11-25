package com.library.dao;

import com.library.model.Book;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) for Book operations.
 * Handles all CRUD operations, searching, sorting, and aggregate statistics for books.
 * Uses PreparedStatement for all queries containing parameters to protect against SQL Injection.
 */
public class BookDAO {

    /**
     * Adds a new book to the library.
     * When adding a new book, available_quantity is initially equal to total_quantity.
     */
    public boolean addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, category, total_quantity, available_quantity) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getTotalQuantity());
            ps.setInt(5, book.getTotalQuantity()); // Initial available quantity equals total copies
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all books from the database.
     */
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, title, author, category, total_quantity, available_quantity FROM books ORDER BY book_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        }
        return books;
    }

    /**
     * Retrieves a single book by its ID.
     */
    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT book_id, title, author, category, total_quantity, available_quantity FROM books WHERE book_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractBookFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing book's details.
     * Maintains quantity consistency:
     * Issued copies = current_total - current_available.
     * New available quantity = new_total - issued copies.
     */
    public boolean updateBook(Book book) throws SQLException {
        // First retrieve current book details to calculate active issued copies
        Book current = getBookById(book.getBookId());
        if (current == null) {
            return false;
        }

        int currentlyIssued = current.getTotalQuantity() - current.getAvailableQuantity();
        if (book.getTotalQuantity() < currentlyIssued) {
            throw new IllegalArgumentException("Total quantity (" + book.getTotalQuantity() 
                + ") cannot be less than currently issued copies (" + currentlyIssued + ").");
        }

        int newAvailable = book.getTotalQuantity() - currentlyIssued;

        String sql = "UPDATE books SET title = ?, author = ?, category = ?, total_quantity = ?, available_quantity = ? WHERE book_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setInt(4, book.getTotalQuantity());
            ps.setInt(5, newAvailable);
            ps.setInt(6, book.getBookId());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a book by ID.
     * Checks whether the book has active issue transactions before deleting.
     */
    public boolean deleteBook(int bookId) throws SQLException {
        // Business Rule: Check if any copy is currently issued
        String checkSql = "SELECT COUNT(*) FROM issued_books WHERE book_id = ? AND status = 'ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            
            checkPs.setInt(1, bookId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalStateException("Cannot delete book: " + rs.getInt(1) + " copy/copies are currently issued to members.");
                }
            }

            // Also check if any past transactions exist (Foreign Key RESTRICT will reject)
            String historySql = "SELECT COUNT(*) FROM issued_books WHERE book_id = ?";
            try (PreparedStatement histPs = conn.prepareStatement(historySql)) {
                histPs.setInt(1, bookId);
                try (ResultSet rs = histPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalStateException("Cannot delete book: Book has " + rs.getInt(1) + " historical transaction record(s). Removing it would violate audit trail.");
                    }
                }
            }

            String deleteSql = "DELETE FROM books WHERE book_id = ?";
            try (PreparedStatement delPs = conn.prepareStatement(deleteSql)) {
                delPs.setInt(1, bookId);
                return delPs.executeUpdate() > 0;
            }
        }
    }

    /**
     * Searches books by title or author using SQL LIKE and PreparedStatement.
     */
    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT book_id, title, author, category, total_quantity, available_quantity FROM books "
                   + "WHERE title LIKE ? OR author LIKE ? ORDER BY title ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword.trim() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(extractBookFromResultSet(rs));
                }
            }
        }
        return books;
    }

    /**
     * Retrieves books sorted by title, author, or available_quantity using SQL ORDER BY.
     */
    public List<Book> getBooksSorted(String sortBy, String order) throws SQLException {
        // Whitelist sorting columns to prevent SQL injection in ORDER BY clause
        String sortColumn;
        if ("author".equalsIgnoreCase(sortBy)) {
            sortColumn = "author";
        } else if ("available_quantity".equalsIgnoreCase(sortBy)) {
            sortColumn = "available_quantity";
        } else {
            sortColumn = "title";
        }

        String sortOrder = "DESC".equalsIgnoreCase(order) ? "DESC" : "ASC";
        String sql = "SELECT book_id, title, author, category, total_quantity, available_quantity FROM books ORDER BY " 
                   + sortColumn + " " + sortOrder;

        List<Book> books = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                books.add(extractBookFromResultSet(rs));
            }
        }
        return books;
    }

    /**
     * Returns total distinct books count.
     */
    public int getTotalBooksCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Returns total copies across all books (SUM(total_quantity)).
     */
    public int getTotalCopiesCount() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_quantity), 0) FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Returns total available copies across all books (SUM(available_quantity)).
     */
    public int getAvailableCopiesCount() throws SQLException {
        String sql = "SELECT COALESCE(SUM(available_quantity), 0) FROM books";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Returns category-wise book statistics using SQL GROUP BY and Java HashMap.
     */
    public Map<String, Integer> getCategoryWiseStatistics() throws SQLException {
        Map<String, Integer> categoryStats = new HashMap<>();
        String sql = "SELECT category, COUNT(*) AS count FROM books GROUP BY category ORDER BY count DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                categoryStats.put(rs.getString("category"), rs.getInt("count"));
            }
        }
        return categoryStats;
    }

    // Helper method to map ResultSet row to a Book object
    private Book extractBookFromResultSet(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("category"),
            rs.getInt("total_quantity"),
            rs.getInt("available_quantity")
        );
    }
}
