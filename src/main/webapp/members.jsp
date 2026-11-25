<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.Member" %>
<%
    request.setAttribute("activePage", "members");
    request.setAttribute("headerTitle", "Member Management");
    request.setAttribute("headerSubtitle", "View and manage registered library patrons and contact info.");

    List<Member> memberList = (List<Member>) request.getAttribute("memberList");
    String searchKeyword = (String) request.getAttribute("searchKeyword");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Members - LibraryHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/includes/sidebar.jsp" />
        <div class="main-wrapper">
            <jsp:include page="/includes/header.jsp" />
            <div class="content-body">
                <jsp:include page="/includes/messages.jsp" />

                <div class="toolbar">
                    <form action="${pageContext.request.contextPath}/members" method="get" class="filter-form">
                        <input type="hidden" name="action" value="search">
                        <input type="text" name="keyword" class="form-control" placeholder="Search members by name..." 
                               value="<%= searchKeyword != null ? searchKeyword : "" %>" style="width: 250px;">
                        <button type="submit" class="btn btn-primary btn-sm">🔍 Search</button>
                        <% if (searchKeyword != null && !searchKeyword.trim().isEmpty()) { %>
                            <a href="${pageContext.request.contextPath}/members" class="btn btn-secondary btn-sm">Clear</a>
                        <% } %>
                    </form>

                    <a href="${pageContext.request.contextPath}/members?action=new" class="btn btn-primary">+ Add New Member</a>
                </div>

                <div class="panel">
                    <div class="panel-header">
                        <span class="panel-title">Members List (<%= memberList != null ? memberList.size() : 0 %>)</span>
                    </div>
                    <div class="panel-body" style="padding: 0;">
                        <% if (memberList != null && !memberList.isEmpty()) { %>
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Member ID</th>
                                            <th>Full Name</th>
                                            <th>Email Address</th>
                                            <th>Phone Number</th>
                                            <th style="text-align: right;">Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (Member m : memberList) { %>
                                            <tr>
                                                <td>#<%= m.getMemberId() %></td>
                                                <td class="title-cell"><%= m.getName() %></td>
                                                <td><%= m.getEmail() %></td>
                                                <td><%= m.getPhone() %></td>
                                                <td style="text-align: right;">
                                                    <div style="display: inline-flex; gap: 8px;">
                                                        <a href="${pageContext.request.contextPath}/members?action=edit&id=<%= m.getMemberId() %>" 
                                                           class="btn btn-secondary btn-sm">Edit</a>
                                                        <form action="${pageContext.request.contextPath}/members" method="post" style="display:inline;"
                                                              onsubmit="return confirmDelete('member', '<%= m.getName().replace("'", "\\'") %>');">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="memberId" value="<%= m.getMemberId() %>">
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
                                <div class="empty-icon">👥</div>
                                <h3>No Members Found</h3>
                                <p>Register a new member to begin issuing books.</p>
                                <a href="${pageContext.request.contextPath}/members?action=new" class="btn btn-primary btn-sm">Add New Member</a>
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
