<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    request.setAttribute("activePage", "members");
    request.setAttribute("headerTitle", "Add Member");
    request.setAttribute("headerSubtitle", "Register a new student, teacher, or community member.");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Member - LibraryHub</title>
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
                        <span class="panel-title">Member Profile Form</span>
                        <a href="${pageContext.request.contextPath}/members" class="btn btn-secondary btn-sm">&larr; Back to Members</a>
                    </div>
                    <div class="panel-body">
                        <form action="${pageContext.request.contextPath}/members" method="post" class="form-grid" 
                              onsubmit="return validateMemberForm(this);">
                            <input type="hidden" name="action" value="add">

                            <div class="form-group">
                                <label class="form-label">Full Name *</label>
                                <input type="text" name="name" class="form-control" placeholder="e.g., John Doe" required autofocus>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Email Address *</label>
                                <input type="email" name="email" class="form-control" placeholder="e.g., john.doe@example.com" required>
                                <span class="form-hint">Used for book notifications and return reminders.</span>
                            </div>

                            <div class="form-group">
                                <label class="form-label">Phone Number *</label>
                                <input type="tel" name="phone" class="form-control" placeholder="e.g., 9876543210" required>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary">Save Member</button>
                                <a href="${pageContext.request.contextPath}/members" class="btn btn-secondary">Cancel</a>
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
