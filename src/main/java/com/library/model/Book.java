package com.library.model;

/**
 * Model class representing a Book entity in the library.
 * Demonstrates Encapsulation with private fields and public getters/setters.
 */
public class Book {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private int totalQuantity;
    private int availableQuantity;

    // Default constructor
    public Book() {
    }

    // Constructor without ID (useful when inserting a new book where ID is auto-generated)
    public Book(String title, String author, String category, int totalQuantity, int availableQuantity) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    // Full constructor
    public Book(int bookId, String title, String author, String category, int totalQuantity, int availableQuantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    // Getters and Setters
    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    // Business helper: check if at least one copy is available for issue
    public boolean isAvailable() {
        return this.availableQuantity > 0;
    }

    // Business helper: calculate how many copies are currently issued to members
    public int getIssuedQuantity() {
        return this.totalQuantity - this.availableQuantity;
    }

    @Override
    public String toString() {
        return "Book [bookId=" + bookId + ", title=" + title + ", author=" + author 
                + ", category=" + category + ", total=" + totalQuantity 
                + ", available=" + availableQuantity + "]";
    }
}
