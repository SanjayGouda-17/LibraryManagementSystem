-- ==========================================================
-- LIBRARY MANAGEMENT SYSTEM - DATABASE SCRIPT
-- Database Name: library_management
-- Compatible with MySQL 8.0+
-- ==========================================================

-- 1. Create Database if not exists
CREATE DATABASE IF NOT EXISTS library_management;
USE library_management;

-- 2. Drop existing tables if re-initializing (child first, then parents)
DROP TABLE IF EXISTS issued_books;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS books;

-- ----------------------------------------------------------
-- Table 1: books
-- Stores information about library books and their stock levels.
-- ----------------------------------------------------------
CREATE TABLE books (
    book_id INT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    category VARCHAR(100) NOT NULL,
    total_quantity INT NOT NULL,
    available_quantity INT NOT NULL,
    PRIMARY KEY (book_id),
    CONSTRAINT chk_total_qty CHECK (total_quantity >= 0),
    CONSTRAINT chk_avail_qty CHECK (available_quantity >= 0 AND available_quantity <= total_quantity)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- Table 2: members
-- Stores information about registered library members (patrons).
-- ----------------------------------------------------------
CREATE TABLE members (
    member_id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(20) NOT NULL,
    PRIMARY KEY (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- Table 3: issued_books
-- Records book issue and return transactions.
-- Uses Foreign Keys with ON DELETE RESTRICT to preserve audit trail.
-- ----------------------------------------------------------
CREATE TABLE issued_books (
    issue_id INT NOT NULL AUTO_INCREMENT,
    book_id INT NOT NULL,
    member_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ISSUED',
    PRIMARY KEY (issue_id),
    CONSTRAINT fk_issued_book FOREIGN KEY (book_id) 
        REFERENCES books(book_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT fk_issued_member FOREIGN KEY (member_id) 
        REFERENCES members(member_id) 
        ON DELETE RESTRICT 
        ON UPDATE CASCADE,
    CONSTRAINT chk_status CHECK (status IN ('ISSUED', 'RETURNED'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- Sample Data: Books
-- ----------------------------------------------------------
INSERT INTO books (title, author, category, total_quantity, available_quantity) VALUES
('Effective Java (3rd Edition)', 'Joshua Bloch', 'Computer Science', 5, 4),
('Clean Code: A Handbook of Agile Software Craftsmanship', 'Robert C. Martin', 'Computer Science', 6, 5),
('Introduction to Algorithms (CLRS)', 'Thomas H. Cormen', 'Computer Science', 4, 3),
('Head First Java', 'Kathy Sierra & Bert Bates', 'Computer Science', 5, 5),
('Design Patterns: Elements of Reusable Object-Oriented Software', 'Erich Gamma et al.', 'Computer Science', 3, 3),
('To Kill a Mockingbird', 'Harper Lee', 'Fiction', 4, 4),
('1984', 'George Orwell', 'Fiction', 5, 5),
('The Great Gatsby', 'F. Scott Fitzgerald', 'Fiction', 3, 3),
('Calculus: Early Transcendentals', 'James Stewart', 'Mathematics', 4, 4),
('Discrete Mathematics and Its Applications', 'Kenneth H. Rosen', 'Mathematics', 5, 5),
('A Brief History of Time', 'Stephen Hawking', 'Science', 4, 3);

-- ----------------------------------------------------------
-- Sample Data: Members
-- ----------------------------------------------------------
INSERT INTO members (name, email, phone) VALUES
('Aarav Sharma', 'aarav.sharma@example.com', '9876543210'),
('Priya Patel', 'priya.patel@example.com', '9812345678'),
('Rahul Verma', 'rahul.verma@example.com', '9823456789'),
('Ananya Iyer', 'ananya.iyer@example.com', '9834567890'),
('Karan Singh', 'karan.singh@example.com', '9845678901');

-- ----------------------------------------------------------
-- Sample Data: Transactions (Issued Books)
-- Note: available_quantity in books table reflects these active issues!
-- ----------------------------------------------------------
-- Active issue 1: Book 1 (Effective Java) issued to Member 1 (Aarav)
INSERT INTO issued_books (book_id, member_id, issue_date, return_date, status) VALUES
(1, 1, '2026-11-15', NULL, 'ISSUED');

-- Active issue 2: Book 2 (Clean Code) issued to Member 2 (Priya)
INSERT INTO issued_books (book_id, member_id, issue_date, return_date, status) VALUES
(2, 2, '2026-11-18', NULL, 'ISSUED');

-- Active issue 3: Book 3 (Introduction to Algorithms) issued to Member 3 (Rahul)
INSERT INTO issued_books (book_id, member_id, issue_date, return_date, status) VALUES
(3, 3, '2026-11-20', NULL, 'ISSUED');

-- Active issue 4: Book 11 (A Brief History of Time) issued to Member 4 (Ananya)
INSERT INTO issued_books (book_id, member_id, issue_date, return_date, status) VALUES
(11, 4, '2026-11-21', NULL, 'ISSUED');

-- Historical returned transaction: Book 1 was issued to Member 5 and returned
INSERT INTO issued_books (book_id, member_id, issue_date, return_date, status) VALUES
(1, 5, '2026-10-01', '2026-10-15', 'RETURNED'),
(4, 2, '2026-10-10', '2026-10-25', 'RETURNED');
