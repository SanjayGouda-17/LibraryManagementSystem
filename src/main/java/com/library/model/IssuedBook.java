package com.library.model;

import java.sql.Date;

/**
 * Model class representing an Issued Book Transaction.
 * Contains core transaction fields from the issued_books table,
 * as well as joined fields (bookTitle, memberName, memberEmail) populated via SQL JOINs.
 */
public class IssuedBook {
    private int issueId;
    private int bookId;
    private int memberId;
    private Date issueDate;
    private Date returnDate;
    private String status; // "ISSUED" or "RETURNED"

    // Joined fields from 'books' and 'members' tables
    private String bookTitle;
    private String memberName;
    private String memberEmail;

    // Default constructor
    public IssuedBook() {
    }

    // Core constructor
    public IssuedBook(int bookId, int memberId, Date issueDate, Date returnDate, String status) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Full constructor including ID
    public IssuedBook(int issueId, int bookId, int memberId, Date issueDate, Date returnDate, String status) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
    }

    // Complete constructor including JOIN fields
    public IssuedBook(int issueId, int bookId, int memberId, Date issueDate, Date returnDate, String status,
                      String bookTitle, String memberName, String memberEmail) {
        this.issueId = issueId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.bookTitle = bookTitle;
        this.memberName = memberName;
        this.memberEmail = memberEmail;
    }

    // Getters and Setters
    public int getIssueId() {
        return issueId;
    }

    public void setIssueId(int issueId) {
        this.issueId = issueId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    // Helper method
    public boolean isReturned() {
        return "RETURNED".equalsIgnoreCase(this.status);
    }

    @Override
    public String toString() {
        return "IssuedBook [issueId=" + issueId + ", bookId=" + bookId + ", memberId=" + memberId 
                + ", status=" + status + ", bookTitle=" + bookTitle + ", memberName=" + memberName + "]";
    }
}
