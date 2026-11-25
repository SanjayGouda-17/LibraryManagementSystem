<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.library.model.IssuedBook" %>
<%
    request.setAttribute("activePage", "dashboard");
    request.setAttribute("headerTitle", "Dashboard");
    request.setAttribute("headerSubtitle", "Real-time overview of library inventory, patrons, and circulation.");

    Integer totalBooks = (Integer) request.getAttribute("totalBooks");
    Integer totalCopies = (Integer) request.getAttribute("totalCopies");
    Integer availableCopies = (Integer) request.getAttribute("availableCopies");
    Integer issuedCopies = (Integer) request.getAttribute("issuedCopies");
    Integer totalMembers = (Integer) request.getAttribute("totalMembers");
    Map<String, Integer> categoryStats = (Map<String, Integer>) request.getAttribute("categoryStats");
    List<IssuedBook> recentTransactions = (List<IssuedBook>) request.getAttribute("recentTransactions");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - LibraryHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="app-container">
        <!-- Sidebar Navigation -->
        <jsp:include page="/includes/sidebar.jsp" />

        <div class="main-wrapper">
            <!-- Top Header -->
            <jsp:include page="/includes/header.jsp" />

            <div class="content-body">
                <!-- Notifications -->
                <jsp:include page="/includes/messages.jsp" />

                <!-- Quick Action Buttons -->
                <div class="toolbar">
                    <h2 style="font-size: 16px; font-weight: 700;">Library Overview</h2>
                    <div style="display: flex; gap: 10px;">
                        <a href="${pageContext.request.contextPath}/books?action=new" class="btn btn-primary btn-sm">+ Add Book</a>
                        <a href="${pageContext.request.contextPath}/members?action=new" class="btn btn-secondary btn-sm">+ Add Member</a>
                        <a href="${pageContext.request.contextPath}/issue" class="btn btn-primary btn-sm" style="background-color: #059669;">📤 Issue Book</a>
                    </div>
                </div>

                <!-- Metric Cards Grid -->
                <div class="metrics-grid">
                    <div class="metric-card">
                        <div class="metric-icon-box blue">📚</div>
                        <div class="metric-data">
                            <div class="metric-val"><%= totalBooks != null ? totalBooks : 0 %></div>
                            <div class="metric-lbl">Total Books</div>
                        </div>
                    </div>

                    <div class="metric-card">
                        <div class="metric-icon-box purple">📦</div>
                        <div class="metric-data">
                            <div class="metric-val"><%= totalCopies != null ? totalCopies : 0 %></div>
                            <div class="metric-lbl">Total Copies</div>
                        </div>
                    </div>

                    <div class="metric-card">
                        <div class="metric-icon-box green">✅</div>
                        <div class="metric-data">
                            <div class="metric-val"><%= availableCopies != null ? availableCopies : 0 %></div>
                            <div class="metric-lbl">Available Copies</div>
                        </div>
                    </div>

                    <div class="metric-card">
                        <div class="metric-icon-box amber">📤</div>
                        <div class="metric-data">
                            <div class="metric-val"><%= issuedCopies != null ? issuedCopies : 0 %></div>
                            <div class="metric-lbl">Issued Copies</div>
                        </div>
                    </div>

                    <div class="metric-card">
                        <div class="metric-icon-box rose">👥</div>
                        <div class="metric-data">
                            <div class="metric-val"><%= totalMembers != null ? totalMembers : 0 %></div>
                            <div class="metric-lbl">Registered Members</div>
                        </div>
                    </div>
                </div>

                <!-- Two-Column Section: Recent Transactions & Category Stats -->
                <div class="dashboard-columns">
                    <!-- Recent Transactions -->
                    <div class="panel">
                        <div class="panel-header">
                            <span class="panel-title">Recent Activity</span>
                            <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary btn-sm">View All &rarr;</a>
                        </div>
                        <div class="panel-body" style="padding: 0;">
                            <% if (recentTransactions != null && !recentTransactions.isEmpty()) { %>
                                <div class="table-responsive">
                                    <table class="table">
                                        <thead>
                                            <tr>
                                                <th>Tx ID</th>
                                                <th>Book Title</th>
                                                <th>Member Name</th>
                                                <th>Issue Date</th>
                                                <th>Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <% for (IssuedBook tx : recentTransactions) { %>
                                                <tr>
                                                    <td>#<%= tx.getIssueId() %></td>
                                                    <td class="title-cell"><%= tx.getBookTitle() %></td>
                                                    <td><%= tx.getMemberName() %></td>
                                                    <td><%= tx.getIssueDate() %></td>
                                                    <td>
                                                        <% if ("ISSUED".equalsIgnoreCase(tx.getStatus())) { %>
                                                            <span class="badge badge-issued">ISSUED</span>
                                                        <% } else { %>
                                                            <span class="badge badge-returned">RETURNED</span>
                                                        <% } %>
                                                    </td>
                                                </tr>
                                            <% } %>
                                        </tbody>
                                    </table>
                                </div>
                            <% } else { %>
                                <div class="empty-state">
                                    <div class="empty-icon">📑</div>
                                    <h3>No Recent Transactions</h3>
                                    <p>Issue a book to begin tracking circulation history.</p>
                                    <a href="${pageContext.request.contextPath}/issue" class="btn btn-primary btn-sm">Issue Book Now</a>
                                </div>
                            <% } %>
                        </div>
                    </div>

                    <!-- Category-wise Statistics -->
                    <div class="panel">
                        <div class="panel-header">
                            <span class="panel-title">Books by Category</span>
                        </div>
                        <div class="panel-body">
                            <% if (categoryStats != null && !categoryStats.isEmpty()) { %>
                                <ul class="cat-list">
                                    <% for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) { %>
                                        <li class="cat-item">
                                            <span class="cat-name"><%= entry.getKey() %></span>
                                            <span class="cat-badge"><%= entry.getValue() %> books</span>
                                        </li>
                                    <% } %>
                                </ul>
                            <% } else { %>
                                <p style="color: var(--text-muted); text-align: center; padding: 20px;">No categories available.</p>
                            <% } %>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
    <script src="${pageContext.request.contextPath}/assets/js/script.js"></script>
</body>
</html>
