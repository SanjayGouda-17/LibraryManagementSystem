<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("activePage", "books");
    request.setAttribute("headerTitle", "Add New Book");
    request.setAttribute("headerSubtitle", "Register a new book title into the library repository.");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Book - LibraryHub</title>
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
                        <span class="panel-title">Book Information</span>
                        <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary btn-sm">&larr; Back to Inventory</a>
                    </div>
                    <div class="panel-body">
                        <form action="${pageContext.request.contextPath}/books" method="post" class="form-grid" 
                              onsubmit="return validateBookForm(this);">
                            <input type="hidden" name="action" value="add">

                            <div class="form-group">
                                <label for="title" class="form-label">Book Title *</label>
                                <input type="text" id="title" name="title" class="form-control" 
                                       placeholder="e.g., Clean Code" required autofocus>
                                <span class="form-hint">Enter the full official title of the book.</span>
                            </div>

                            <div class="form-group">
                                <label for="author" class="form-label">Author Name *</label>
                                <input type="text" id="author" name="author" class="form-control" 
                                       placeholder="e.g., Robert C. Martin" required>
                            </div>

                            <div class="form-group">
                                <label for="category" class="form-label">Category *</label>
                                <input type="text" id="category" name="category" class="form-control" list="categoryOptions" 
                                       placeholder="e.g., Computer Science" required>
                                <datalist id="categoryOptions">
                                    <option value="Computer Science">
                                    <option value="Fiction">
                                    <option value="Mathematics">
                                    <option value="Science">
                                    <option value="History">
                                    <option value="Philosophy">
                                    <option value="Business & Economics">
                                </datalist>
                                <span class="form-hint">Select an existing category or type a new one.</span>
                            </div>

                            <div class="form-group">
                                <label for="totalQuantity" class="form-label">Total Copies *</label>
                                <input type="number" id="totalQuantity" name="totalQuantity" class="form-control" 
                                       min="1" value="1" required>
                                <span class="form-hint">Number of physical book copies acquired for the library.</span>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Save Book</button>
                                <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary">Cancel</a>
                            </div>
                        </form>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
