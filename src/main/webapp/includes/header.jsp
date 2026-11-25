<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String headerTitle = (String) request.getAttribute("headerTitle");
    if (headerTitle == null) {
        headerTitle = "Library Management System";
    }
    String headerSubtitle = (String) request.getAttribute("headerSubtitle");
    if (headerSubtitle == null) {
        headerSubtitle = "Administrator Control Center";
    }
%>
<header class="top-header">
    <div class="page-title-box">
        <h1><%= headerTitle %></h1>
        <p><%= headerSubtitle %></p>
    </div>
    <div class="header-actions">
        <span class="badge-role">👤 Admin: <%= session.getAttribute("adminUser") != null ? session.getAttribute("adminUser") : "admin" %></span>
        <span style="font-size: 13px; color: var(--text-muted);"><%= java.time.LocalDate.now().toString() %></span>
        <a href="${pageContext.request.contextPath}/logout" class="btn btn-secondary btn-sm" style="color: #dc2626;">🚪 Logout</a>
    </div>
</header>
