<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.Book" %>
<%
    request.setAttribute("activePage", "books");
    request.setAttribute("headerTitle", "Book Inventory");
    request.setAttribute("headerSubtitle", "Manage all books, copies, categories, and real-time stock levels.");

    List<Book> bookList = (List<Book>) request.getAttribute("bookList");
    String searchKeyword = (String) request.getAttribute("searchKeyword");
    String currentSort = (String) request.getAttribute("currentSort");
    String currentOrder = (String) request.getAttribute("currentOrder");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Book Inventory - LibraryHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/includes/sidebar.jsp" />

        <div class="main-wrapper">
            <jsp:include page="/includes/header.jsp" />

            <div class="content-body">
                <jsp:include page="/includes/messages.jsp" />

                <!-- Toolbar: Search, Sort, and Add New Book -->
                <div class="toolbar">
                    <!-- Search Form -->
                    <form action="${pageContext.request.contextPath}/books" method="get" class="filter-form">
                        <input type="hidden" name="action" value="search">
                        <input type="text" name="keyword" class="form-control" placeholder="Search by title or author..." 
                               value="<%= searchKeyword != null ? searchKeyword : "" %>" style="width: 260px;">
                        <button type="submit" class="btn btn-primary btn-sm">🔍 Search</button>
                        <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                            <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary btn-sm">Clear</a>
                        <% } %>
                    </form>

                    <!-- Sorting Options -->
                    <form action="${pageContext.request.contextPath}/books" method="get" class="filter-form">
                        <input type="hidden" name="action" value="list">
                        <label for="sortBy" style="font-size: 13px; font-weight: 500;">Sort By:</label>
                        <select name="sortBy" id="sortBy" class="form-control" onchange="this.form.submit()">
                            <option value="title" <%= "title".equals(currentSort) ? "selected" : "" %>>Title</option>
                            <option value="author" <%= "author".equals(currentSort) ? "selected" : "" %>>Author</option>
                            <option value="available_quantity" <%= "available_quantity".equals(currentSort) ? "selected" : "" %>>Available Copies</option>
                        </select>
                        <select name="order" class="form-control" onchange="this.form.submit()">
                            <option value="ASC" <%= "ASC".equalsIgnoreCase(currentOrder) ? "selected" : "" %>>Ascending (A-Z / Low-High)</option>
                            <option value="DESC" <%= "DESC".equalsIgnoreCase(currentOrder) ? "selected" : "" %>>Descending (Z-A / High-Low)</option>
                        </select>
                    </form>

                    <a href="${pageContext.request.contextPath}/books?action=new" class="btn btn-primary">+ Add New Book</a>
                </div>

                <!-- Book Inventory Table -->
                <div class="panel">
                    <div class="panel-header">
                        <span class="panel-title">Books (<%= bookList != null ? bookList.size() : 0 %>)</span>
                    </div>
                    <div class="panel-body" style="padding: 0;">
                        <% if (bookList != null && !bookList.isEmpty()) { %>
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>ID</th>
                                            <th>Title</th>
                                            <th>Author</th>
                                            <th>Category</th>
                                            <th>Total Copies</th>
                                            <th>Available</th>
                                            <th>Issued</th>
                                            <th>Status</th>
                                            <th style="text-align: right;">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Book book : bookList) { %>
                                            <tr>
                                                <td>#<%= book.getBookId() %></td>
                                                <td class="title-cell"><%= book.getTitle() %></td>
                                                <td><%= book.getAuthor() %></td>
                                                <td><span class="badge" style="background-color: #f1f5f9; color: var(--text-secondary);"><%= book.getCategory() %></span></td>
                                                <td><strong><%= book.getTotalQuantity() %></strong></td>
                                                <td>
                                                    <span style="font-weight: 600; color: <%= book.isAvailable() ? "#059669" : "#dc2626" %>;">
                                                        <%= book.getAvailableQuantity() %>
                                                    </span>
                                                </td>
                                                <td><%= book.getIssuedQuantity() %></td>
                                                <td>
                                                    <% if (book.isAvailable()) { %>
                                                        <span class="badge badge-in-stock">IN STOCK</span>
                                                    <% } else { %>
                                                        <span class="badge badge-out-of-stock">OUT OF STOCK</span>
                                                    <% } %>
                                                </td>
                                                <td style="text-align: right;">
                                                    <div style="display: inline-flex; gap: 8px;">
                                                        <a href="${pageContext.request.contextPath}/books?action=edit&id=<%= book.getBookId() %>" 
                                                           class="btn btn-secondary btn-sm">Edit</a>
                                                        
                                                        <form action="${pageContext.request.contextPath}/books" method="post" style="display:inline;"
                                                              onsubmit="return confirmDelete('book', '<%= book.getTitle().replace("'", "\\'") %>');">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="bookId" value="<%= book.getBookId() %>">
                                                            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                                        </form>
                                                    </div>
                                                </td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        <% } else { %>
                            <div class="empty-state">
                                <div class="empty-icon">📖</div>
                                <h3>No Books Found</h3>
                                <p>No books match your current filter or database is empty.</p>
                                <a href="${pageContext.request.contextPath}/books?action=new" class="btn btn-primary btn-sm">Add Your First Book</a>
                            </div>
                        <% } %>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
