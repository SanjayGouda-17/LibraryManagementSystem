<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.library.model.Book" %>
<%
    request.setAttribute("activePage", "books");
    request.setAttribute("headerTitle", "Edit Book");
    request.setAttribute("headerSubtitle", "Update book details and manage inventory levels.");
    Book book = (Book) request.getAttribute("book");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Book - LibraryHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/includes/sidebar.jsp" />
        <div class="main-wrapper">
            <jsp:include page="/includes/header.jsp" />
            <div class="content-body">
                <jsp:include page="/includes/messages.jsp" />

                <div class="panel">
                    <div class="panel-header">
                        <span class="panel-title">Edit Book #<%= book != null ? book.getBookId() : "" %></span>
                        <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary btn-sm">&larr; Back to Inventory</a>
                    </div>
                    <div class="panel-body">
                        <% if (book != null) { %>
                            <form action="${pageContext.request.contextPath}/books" method="post" class="form-grid" 
                                  onsubmit="return validateBookForm(this);">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="bookId" value="<%= book.getBookId() %>">

                                <div class="form-group">
                                    <label class="form-label">Book Title *</label>
                                    <input type="text" name="title" class="form-control" value="<%= book.getTitle() %>" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Author Name *</label>
                                    <input type="text" name="author" class="form-control" value="<%= book.getAuthor() %>" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Category *</label>
                                    <input type="text" name="category" class="form-control" value="<%= book.getCategory() %>" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Total Copies *</label>
                                    <input type="number" name="totalQuantity" class="form-control" 
                                           min="<%= book.getIssuedQuantity() %>" value="<%= book.getTotalQuantity() %>" required>
                                    <span class="form-hint">
                                        Currently issued copies: <strong><%= book.getIssuedQuantity() %></strong>. 
                                        Available copies: <strong><%= book.getAvailableQuantity() %></strong>.
                                    </span>
                                </div>

                                <div class="form-actions">
                                    <button type="submit" class="btn btn-primary">Update Book</button>
                                    <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary">Cancel</a>
                                </div>
                            </form>
                        <% } else { %>
                            <p>Book record not found.</p>
                        <% } %>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
