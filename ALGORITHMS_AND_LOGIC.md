# 🧠 Algorithms, Data Structures & Business Logic Guide
## Library Management System

This document explains the algorithms, data structures, SQL operations, and business logic implemented in the application.

---

## 1. Java Data Structures

### A. `java.util.ArrayList`
- **Where Used**: In all DAO methods returning lists:
  - [`BookDAO.getAllBooks()`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/BookDAO.java)
  - [`MemberDAO.getAllMembers()`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/MemberDAO.java)
  - [`IssueDAO.getTransactionsWithFilter()`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/IssueDAO.java)
- **Why Used**: We need an ordered, dynamically-sized collection to collect rows from JDBC `ResultSet` and pass them to the JSP views.
- **How It Works**: Backed by an internal array that dynamically resizes (by 50% in Java 21) when capacity is reached.
- **Time Complexity**:
  - Insertion at end: $O(1)$ amortized
  - Iteration: $O(n)$
  - Positional access (`get(i)`): $O(1)$
- **Interview Question**: *Why choose `ArrayList` over an array or `LinkedList`?*
  - Arrays have fixed size, whereas database row count is dynamic.
  - `ArrayList` offers faster traversal and lower memory overhead than `LinkedList` (no node pointers).

---

### B. `java.util.HashMap`
- **Where Used**: In [`BookDAO.getCategoryWiseStatistics()`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/BookDAO.java) to populate the category breakdown on the dashboard.
- **Why Used**: To store category name and book count as distinct key-value pairs (`Map<String, Integer>`).
- **How It Works**: Computes the hash of the category name (`hashCode()`), maps it to a bucket index, and stores entries in a bucket (linked list / red-black tree).
- **Time Complexity**:
  - Insertion (`put`): $O(1)$ average
  - Lookup (`get`): $O(1)$ average
- **Interview Question**: *How does `HashMap` handle hash collisions in Java 8+?*
  - Initially via separate chaining (linked list). When a bucket exceeds 8 entries and total table capacity $\ge 64$, it converts the bucket into a balanced Red-Black Tree ($O(\log n)$ search time).

---

## 2. Iteration Mechanisms

### A. `while (rs.next())` (JDBC Cursor Iteration)
- **Where Used**: Reading records from `java.sql.ResultSet`.
- **How It Works**: The database cursor begins before the first row. `rs.next()` advances the cursor to the next row and returns `true` if a row exists, or `false` when exhausted.

### B. Enhanced For-Loop (`for (Book book : bookList)`)
- **Where Used**: Inside JSP files (e.g., [`books.jsp`](file:///c:/Users/iamsa/Desktop/LMS/src/main/webapp/books.jsp), [`dashboard.jsp`](file:///c:/Users/iamsa/Desktop/LMS/src/main/webapp/dashboard.jsp)).
- **How It Works**: Syntactic sugar over `java.util.Iterator`. Calls `hasNext()` and `next()` under the hood.

---

## 3. SQL Searching vs. Linear Search

- **Where Used**: [`BookDAO.searchBooks(String keyword)`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/BookDAO.java) and [`MemberDAO.searchMembers(String keyword)`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/MemberDAO.java).
- **Query**:
  ```sql
  SELECT book_id, title, author, category, total_quantity, available_quantity 
  FROM books 
  WHERE title LIKE ? OR author LIKE ? 
  ORDER BY title ASC;
  ```
- **Why SQL Search Instead of Manual In-Memory Search**:
  - We do **not** fetch all thousands of books into a Java `ArrayList` and run a linear loop.
  - Offloading filtering to MySQL allows the database engine to use indexing and optimize memory usage, transferring only matched records over the network.
- **Time Complexity**:
  - Full table scan without index: $O(n)$
  - With B-Tree index on title/author: $O(\log n)$ for prefix matches (`term%`).

---

## 4. SQL Sorting vs. In-Memory Sorting

- **Where Used**: [`BookDAO.getBooksSorted(String sortBy, String order)`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/BookDAO.java).
- **Why SQL ORDER BY Instead of Java Collections.sort()**:
  - Databases are optimized for sorting using disk merges, temporary tables, and index scans.
  - Security precaution: The column name is checked against an allowlist in Java before constructing the SQL query to prevent SQL Injection in the `ORDER BY` clause.

---

## 5. SQL Aggregation & GROUP BY

- **Where Used**: Dashboard statistics in [`BookDAO`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/dao/BookDAO.java) and [`DashboardServlet`](file:///c:/Users/iamsa/Desktop/LMS/src/main/java/com/library/servlet/DashboardServlet.java).
- **Queries**:
  - Total books: `SELECT COUNT(*) FROM books;`
  - Total copies: `SELECT COALESCE(SUM(total_quantity), 0) FROM books;`
  - Available copies: `SELECT COALESCE(SUM(available_quantity), 0) FROM books;`
  - Category breakdown: `SELECT category, COUNT(*) AS count FROM books GROUP BY category ORDER BY count DESC;`
- **Why**: Evaluates aggregates directly on the database engine rather than transferring all records to Java.

---

## 6. Circulation Business Logic & Algorithms

### A. Book Issue Algorithm
```
Input: bookId, memberId, issueDate

Step 1: Check if memberId exists in `members` table.
        If NO  -> Throw IllegalArgumentException("Member not found").
Step 2: Check if bookId exists in `books` table.
        If NO  -> Throw IllegalArgumentException("Book not found").
Step 3: Begin JDBC Database Transaction:
        conn.setAutoCommit(false);
Step 4: Execute atomic conditional decrement:
        UPDATE books SET available_quantity = available_quantity - 1 
        WHERE book_id = ? AND available_quantity > 0;
Step 5: Inspect affected row count:
        If rowsUpdated == 0 ->
           Rollback and Throw IllegalStateException("Book is out of stock!").
Step 6: Insert transaction record:
        INSERT INTO issued_books (book_id, member_id, issue_date, status) 
        VALUES (?, ?, ?, 'ISSUED');
Step 7: Commit transaction:
        conn.commit();
Step 8: Exception handling:
        If any SQLException or error occurs:
           conn.rollback();
           throw exception to Controller.
Step 9: Finally:
        Reset conn.setAutoCommit(true) and close connection.
```

---

### B. Book Return Algorithm
```
Input: issueId, returnDate

Step 1: Query `issued_books` WHERE issue_id = ?
        If record not found -> Throw IllegalArgumentException("Transaction not found").
        If status == 'RETURNED' -> Throw IllegalStateException("Duplicate return rejected!").
Step 2: Begin JDBC Database Transaction:
        conn.setAutoCommit(false);
Step 3: Update transaction record:
        UPDATE issued_books SET status = 'RETURNED', return_date = ? 
        WHERE issue_id = ? AND status = 'ISSUED';
        If rowsUpdated == 0 ->
           Rollback and Throw IllegalStateException("Failed to update status.");
Step 4: Increment available copies in `books`:
        UPDATE books SET available_quantity = available_quantity + 1 
        WHERE book_id = ? AND available_quantity < total_quantity;
Step 5: Commit transaction:
        conn.commit();
Step 6: Exception handling:
        If any error occurs:
           conn.rollback();
Step 7: Finally:
        Reset auto-commit and close connection.
```

---

### C. Quantity Consistency Algorithm in Book Update
```
When updating total_quantity of a book:
1. Fetch current book record.
2. Calculate currently borrowed copies:
   issuedCopies = current.total_quantity - current.available_quantity;
3. Check validation constraint:
   if (newTotalQuantity < issuedCopies) {
       Throw IllegalArgumentException("Total quantity cannot be less than currently issued copies!");
   }
4. Calculate new available copies:
   newAvailableQuantity = newTotalQuantity - issuedCopies;
5. Save updated values in database:
   UPDATE books SET title = ?, author = ?, category = ?, total_quantity = ?, available_quantity = ? 
   WHERE book_id = ?;
```

---

### D. Concurrency Race Condition Prevention
- **Problem**: Two users try to issue the last available copy at the same millisecond.
- **Naive approach**: Reading `available_quantity` in Java, then running `UPDATE books SET available_quantity = available_quantity - 1`.
  - Flaw: Both threads read `1`, both execute the decrement, resulting in `available_quantity = -1` (oversold).
- **Our Solution**:
  ```sql
  UPDATE books SET available_quantity = available_quantity - 1 
  WHERE book_id = ? AND available_quantity > 0;
  ```
  MySQL InnoDB acquires an exclusive row lock. The first query executes, reducing available quantity to 0. The second query's `WHERE available_quantity > 0` condition fails, returning 0 rows affected. Java detects this and aborts safely.
