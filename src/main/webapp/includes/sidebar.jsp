<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String activePage = (String) request.getAttribute("activePage");
    if (activePage == null) {
        activePage = "";
    }
%>
<aside class="sidebar">
    <div class="sidebar-header">
        <a href="${pageContext.request.contextPath}/dashboard" class="brand-logo">
            <div class="brand-icon">📚</div>
            <span>LibraryHub</span>
        </a>
    </div>

    <div class="sidebar-nav">
        <div class="nav-section-title">Main Menu</div>
        <ul class="nav-menu">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/dashboard" 
                   class="nav-link <%= "dashboard".equals(activePage) ? "active" : "" %>">
                    <span class="icon">📊</span>
                    <span>Dashboard</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/books" 
                   class="nav-link <%= "books".equals(activePage) ? "active" : "" %>">
                    <span class="icon">📖</span>
                    <span>Books</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/members" 
                   class="nav-link <%= "members".equals(activePage) ? "active" : "" %>">
                    <span class="icon">👥</span>
                    <span>Members</span>
                </a>
            </li>
        </ul>

        <div class="nav-section-title">Circulation</div>
        <ul class="nav-menu">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/issue" 
                   class="nav-link <%= "issue".equals(activePage) ? "active" : "" %>">
                    <span class="icon">📤</span>
                    <span>Issue Book</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/return" 
                   class="nav-link <%= "return".equals(activePage) ? "active" : "" %>">
                    <span class="icon">📥</span>
                    <span>Return Books</span>
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/transactions" 
                   class="nav-link <%= "transactions".equals(activePage) ? "active" : "" %>">
                    <span class="icon">📑</span>
                    <span>Transaction History</span>
                </a>
            </li>
            <li class="nav-item" style="margin-top: 12px; border-top: 1px solid rgba(255,255,255,0.08); padding-top: 8px;">
                <a href="${pageContext.request.contextPath}/logout" class="nav-link" style="color: #f87171;">
                    <span class="icon">🚪</span>
                    <span>Sign Out</span>
                </a>
            </li>
        </ul>
    </div>

    <div class="sidebar-footer">
        <div>Library Management v1.0</div>
        <div style="margin-top: 4px; font-size: 11px;">Java 21 &bull; Tomcat 10.1</div>
    </div>
</aside>
