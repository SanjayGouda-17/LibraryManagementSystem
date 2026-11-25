<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Check both request attributes and URL parameters for feedback messages
    String errorMsg = (String) request.getAttribute("errorMessage");
    if (errorMsg == null || errorMsg.trim().isEmpty()) {
        errorMsg = request.getParameter("error");
    }

    String successMsg = (String) request.getAttribute("successMessage");
    if (successMsg == null || successMsg.trim().isEmpty()) {
        successMsg = request.getParameter("msg");
    }
%>

<% if (errorMsg != null && !errorMsg.trim().isEmpty()) { %>
    <div class="alert alert-error">
        <span><strong>Error:</strong> <%= errorMsg %></span>
        <button type="button" class="alert-close" onclick="dismissAlert(this)">&times;</button>
    </div>
<% } %>

<% if (successMsg != null && !successMsg.trim().isEmpty()) { %>
    <div class="alert alert-success">
        <span><strong>Success:</strong> <%= successMsg %></span>
        <button type="button" class="alert-close" onclick="dismissAlert(this)">&times;</button>
    </div>
<% } %>
