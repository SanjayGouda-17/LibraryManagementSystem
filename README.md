# LibraryHub - Library Management System (Java Web Application)

A complete, production-ready, beginner-friendly **Library Management System** built with **Java 21**, **Jakarta Servlets**, **JDBC**, **JSP**, and **MySQL 8**. 

Designed specifically for fresher Java Developer interviews to demonstrate solid software engineering fundamentals, Object-Oriented Programming (OOP), database transaction management (ACID), and clean MVC architecture without reliance on heavy frameworks.

---

## 📌 1. Project Overview & Features

### Core Modules:
1. **Interactive Dashboard**:
   - Total distinct books count.
   - Total physical copies count.
   - Available copies vs. currently issued copies.
   - Total registered members.
   - Category-wise book breakdown (`GROUP BY`).
   - Recent circulation transactions log (`INNER JOIN`).
2. **Book Inventory Management (CRUD)**:
   - Add new books (Title, Author, Category, Total Copies).
   - View inventory with real-time stock levels and availability status badges.
   - Edit books (maintains quantity consistency with currently issued copies).
   - Delete books (protected: prevents deleting books with active issues or historical records).
   - Search books by title or author (`SQL LIKE`).
   - Sort books by title, author, or available quantity (`SQL ORDER BY`).
3. **Member Directory (CRUD)**:
   - Register members (Name, Email, Phone).
   - View, update, and search members.
   - Protected deletion: rejects deleting members who hold active issued books.
4. **Circulation - Issue Book**:
   - Select member and book from dynamic dropdowns.
   - Concurrency-safe stock deduction (`UPDATE ... WHERE available_quantity > 0`).
   - Transactional execution (`setAutoCommit(false)`, `commit()`, `rollback()`).
5. **Circulation - Return Books**:
   - View all currently borrowed books pending return.
   - One-click return processing: sets return date and restocks available copies.
   - Duplicate return prevention: ensures returned records cannot be processed twice.
6. **Transaction History & Audit**:
   - Complete audit trail of all borrowings and returns.
   - Rich SQL `INNER JOIN` combining `issued_books`, `books`, and `members`.
   - Filters for status (`ALL`, `ISSUED`, `RETURNED`) and search by member/book name.

---

## 🛠️ 2. Technology Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 (LTS) |
| **Server-Side API** | Jakarta Servlet API 6.0 |
| **View Technology** | JavaServer Pages (JSP) 3.1 & JSTL 3.0 |
| **Database Connectivity** | Plain JDBC (DriverManager, PreparedStatement, Connection, ResultSet) |
| **Database** | MySQL 8.0+ (InnoDB Engine) |
| **Web Server** | Apache Tomcat 10.1.57 (Configured on Port 8050) |
| **Build Tool** | Apache Maven 3.9.9 |
| **Frontend** | HTML5, CSS3 (Custom Responsive Admin Theme), JavaScript |

---

## 🏛️ 3. Architecture & Request Flow

The application follows the classic **Model-View-Controller (MVC)** architectural design:

```
[Browser Client]
       │  ▲
(HTTP) │  │ (HTML / JSP Rendering)
       ▼  │
[Jakarta Servlet (Controller)] ───► Passes Data via request.setAttribute()
       │
       ▼
[Data Access Object (DAO)] ──── Uses Model Entities (Book, Member, IssuedBook)
       │
       ▼
[JDBC Connection (DBConnection)]
       │
       ▼ (SQL PreparedStatements & Transactions)
[MySQL 8.0 Database (library_management)]
```

### Complete Request Lifecycle:
1. **User Action**: The administrator submits a form in the browser (e.g., Issue Book).
2. **Controller (Servlet)**: `IssueServlet` receives the HTTP POST request, validates parameters.
3. **Model & DAO**: The Servlet calls `IssueDAO.issueBook()`.
4. **Transaction Handling**: `IssueDAO` turns off auto-commit (`conn.setAutoCommit(false)`), updates book stock, inserts into `issued_books`, and commits. If any step fails, `conn.rollback()` executes.
5. **PRG Pattern**: The Servlet redirects to prevent accidental duplicate submissions (`response.sendRedirect("transactions?msg=Success")`).
6. **View (JSP)**: The user is presented with the updated inventory and confirmation notification.

---

## 📁 4. Project Folder Structure

```
c:\Users\iamsa\Desktop\LMS\
├── pom.xml                               # Maven project descriptor & dependencies
├── database.sql                          # Database schema, constraints, sample data
├── README.md                             # Setup, execution & deployment guide
├── INTERVIEW_PREPARATION.md              # Complete interview questions & answers
├── ALGORITHMS_AND_LOGIC.md               # Deep dive into algorithms, DSA & logic
└── src/
    └── main/
        ├── java/
        │   └── com/library/
        │       ├── model/                # OOP Entity / JavaBean classes
        │       │   ├── Book.java
        │       │   ├── Member.java
        │       │   └── IssuedBook.java
        │       ├── dao/                  # Data Access Objects (JDBC logic)
        │       │   ├── BookDAO.java
        │       │   ├── MemberDAO.java
        │       │   └── IssueDAO.java
        │       ├── servlet/              # Jakarta HTTP Servlets (Controllers)
        │       │   ├── DashboardServlet.java
        │       │   ├── BookServlet.java
        │       │   ├── MemberServlet.java
        │       │   ├── IssueServlet.java
        │       │   ├── ReturnServlet.java
        │       │   └── TransactionServlet.java
        │       └── util/                 # Connection management
        │           └── DBConnection.java
        └── webapp/
            ├── WEB-INF/
            │   └── web.xml               # Deployment descriptor
            ├── assets/
            │   ├── css/
            │   │   └── style.css         # Modern, professional dashboard styling
            │   └── js/
            │       └── script.js         # Client validation & delete confirmations
            ├── includes/                 # Modular JSP components
            │   ├── header.jsp
            │   ├── sidebar.jsp
            │   └── messages.jsp
            ├── index.jsp                 # Landing page (redirects to /dashboard)
            ├── dashboard.jsp             # Main overview dashboard
            ├── books.jsp                 # Book list with search & sort
            ├── add-book.jsp              # Add book form
            ├── edit-book.jsp             # Edit book form
            ├── members.jsp               # Member directory
            ├── add-member.jsp            # Add member form
            ├── edit-member.jsp           # Edit member form
            ├── issue-book.jsp            # Circulation issue form
            ├── issued-books.jsp          # Return book section
            └── transactions.jsp          # Complete transaction audit history
```

---

## 🚀 5. Quick Setup & Run Instructions (Windows)

### Step 1: Initialize MySQL Database
1. Open MySQL Command Line Client or PowerShell:
   ```powershell
   mysql -u root -p
   ```
2. Run the `database.sql` script:
   ```sql
   source c:/Users/iamsa/Desktop/LMS/database.sql;
   ```
   *(Or copy-paste the contents of `database.sql` directly into MySQL Workbench or MySQL CLI).*

### Step 2: Configure Database Credentials in Java
Open `src/main/java/com/library/util/DBConnection.java` and verify your MySQL password:
```java
private static final String URL = "jdbc:mysql://localhost:3306/library_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "YOUR_MYSQL_PASSWORD"; // Set your password here
```

### Step 3: Build the Application with Maven
Open PowerShell in `c:\Users\iamsa\Desktop\LMS`:
```powershell
mvn clean package
```
This generates the packaged file: `target\LMS.war`.

### Step 4: Deploy WAR to Apache Tomcat 10.1.57
1. Ensure Tomcat is configured for port `8050` in `C:\apache-tomcat-10.1.57\apache-tomcat-10.1.57\conf\server.xml`:
   ```xml
   <Connector port="8050" protocol="HTTP/1.1" connectionTimeout="20000" redirectPort="8443" />
   ```
2. Copy `LMS.war` to the Tomcat `webapps` folder:
   ```powershell
   Copy-Item "c:\Users\iamsa\Desktop\LMS\target\LMS.war" -Destination "C:\apache-tomcat-10.1.57\apache-tomcat-10.1.57\webapps"
   ```

### Step 5: Start Tomcat Server
In PowerShell:
```powershell
& "C:\apache-tomcat-10.1.57\apache-tomcat-10.1.57\bin\startup.bat"
```
A new Tomcat console window will appear and show deployment progress.

### Step 6: Access the Application
Open your web browser and navigate to:
```
http://localhost:8050/LMS/
```
The application will prompt for Admin Login:
- **Username**: `admin`
- **Password**: `admin123`

Once logged in, you will be in the full Administrator Dashboard! Click **"Logout"** in the top header or sidebar anytime to end the session.

---

## 🔧 6. Troubleshooting Common Issues

1. **Port 8050 already in use**:
   Check what process is using the port:
   ```powershell
   Get-NetTCPConnection -LocalPort 8050
   ```
   Kill the existing process or stop Tomcat using `shutdown.bat`.

2. **Access denied for user 'root'@'localhost'**:
   Update `DBConnection.java` with your exact MySQL root password and rebuild (`mvn clean package`).

3. **HTTP 404 Not Found**:
   Ensure `LMS.war` is in `C:\apache-tomcat-10.1.57\apache-tomcat-10.1.57\webapps` and Tomcat has extracted it into a folder named `LMS`. Check Tomcat logs in `C:\apache-tomcat-10.1.57\apache-tomcat-10.1.57\logs\catalina.out` or `localhost.log`.

---

## 🎯 7. Why This Project is Ideal for Fresher Interviews
- **No Magic**: Every query, route, connection, and entity is written by hand.
- **ACID Demonstrated**: Clear usage of JDBC transaction rollback and commit in circulation.
- **Protected Against Vulnerabilities**: `PreparedStatement` used exclusively to prevent SQL Injection.
- **Separation of Concerns**: Clean layers (Model, View, Controller, DAO).
