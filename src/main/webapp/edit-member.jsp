<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.library.model.Member" %>
<%
    request.setAttribute("activePage", "members");
    request.setAttribute("headerTitle", "Edit Member");
    request.setAttribute("headerSubtitle", "Update member contact info and registration details.");
    Member member = (Member) request.getAttribute("member");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Member - LibraryHub</title>
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
                        <span class="panel-title">Edit Member #<%= member != null ? member.getMemberId() : "" %></span>
                        <a href="${pageContext.request.contextPath}/members" class="btn btn-secondary btn-sm">&larr; Back to Members</a>
                    </div>
                    <div class="panel-body">
                        <% if (member != null) { %>
                            <form action="${pageContext.request.contextPath}/members" method="post" class="form-grid" 
                                  onsubmit="return validateMemberForm(this);">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="memberId" value="<%= member.getMemberId() %>">

                                <div class="form-group">
                                    <label class="form-label">Full Name *</label>
                                    <input type="text" name="name" class="form-control" value="<%= member.getName() %>" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Email Address *</label>
                                    <input type="email" name="email" class="form-control" value="<%= member.getEmail() %>" required>
                                </div>

                                <div class="form-group">
                                    <label class="form-label">Phone Number *</label>
                                    <input type="tel" name="phone" class="form-control" value="<%= member.getPhone() %>" required>
                                </div>

                                <div class="form-actions">
                                    <button type="submit" class="btn btn-primary">Update Member</button>
                                    <a href="${pageContext.request.contextPath}/members" class="btn btn-secondary">Cancel</a>
                                </div>
                            </form>
                        <% } else { %>
                            <p>Member not found.</p>
                        <% } %>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
