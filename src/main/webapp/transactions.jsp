<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.IssuedBook" %>
<%
    request.setAttribute("activePage", "transactions");
    request.setAttribute("headerTitle", "Transaction History");
    request.setAttribute("headerSubtitle", "Audit log of all past and current book borrowings with SQL JOIN details.");

    List<IssuedBook> transactionList = (List<IssuedBook>) request.getAttribute("transactionList");
    String selectedStatus = (String) request.getAttribute("selectedStatus");
    if (selectedStatus == null) selectedStatus = "ALL";
    String memberSearch = (String) request.getAttribute("memberSearch");
    String bookSearch = (String) request.getAttribute("bookSearch");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Transaction History - LibraryHub</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
</head>
<body>
    <div class="app-container">
        <jsp:include page="/includes/sidebar.jsp" />
        <div class="main-wrapper">
            <jsp:include page="/includes/header.jsp" />
            <div class="content-body">
                <jsp:include page="/includes/messages.jsp" />

                <!-- Filters & Search Toolbar -->
                <div class="toolbar">
                    <form action="${pageContext.request.contextPath}/transactions" method="get" class="filter-form" style="width: 100%;">
                        <div style="display: flex; gap: 10px; flex-wrap: wrap; align-items: center; width: 100%;">
                            <label style="font-weight: 600;">Status:</label>
                            <select name="status" class="form-control" onchange="this.form.submit()">
                                <option value="ALL" <%= "ALL".equalsIgnoreCase(selectedStatus) ? "selected" : "" %>>All Statuses</option>
                                <option value="ISSUED" <%= "ISSUED".equalsIgnoreCase(selectedStatus) ? "selected" : "" %>>Currently Issued</option>
                                <option value="RETURNED" <%= "RETURNED".equalsIgnoreCase(selectedStatus) ? "selected" : "" %>>Returned</option>
                            </select>

                            <input type="text" name="memberSearch" class="form-control" placeholder="Search by member name..." 
                                   value="<%= memberSearch != null ? memberSearch : "" %>" style="width: 200px;">

                            <input type="text" name="bookSearch" class="form-control" placeholder="Search by book title..." 
                                   value="<%= bookSearch != null ? bookSearch : "" %>" style="width: 200px;">

                            <button type="submit" class="btn btn-primary btn-sm">Filter</button>
                            <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary btn-sm">Reset</a>
                        </div>
                    </form>
                </div>

                <!-- Transaction Table -->
                <div class="panel">
                    <div class="panel-header">
                        <span class="panel-title">Transactions (<%= transactionList != null ? transactionList.size() : 0 %>)</span>
                    </div>
                    <div class="panel-body" style="padding: 0;">
                        <% if (transactionList != null && !transactionList.isEmpty()) { %>
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Tx ID</th>
                                            <th>Book Title</th>
                                            <th>Member Name</th>
                                            <th>Member Email</th>
                                            <th>Issue Date</th>
                                            <th>Return Date</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (IssuedBook tx : transactionList) { %>
                                            <tr>
                                                <td>#<%= tx.getIssueId() %></td>
                                                <td class="title-cell"><%= tx.getBookTitle() %></td>
                                                <td><%= tx.getMemberName() %></td>
                                                <td><%= tx.getMemberEmail() %></td>
                                                <td><%= tx.getIssueDate() %></td>
                                                <td>
                                                    <%= tx.getReturnDate() != null ? tx.getReturnDate() : "<span style='color: var(--text-muted);'>Not Returned Yet</span>" %>
                                                </td>
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
                                <h3>No Transactions Found</h3>
                                <p>No records match your selected filter criteria.</p>
                                <a href="${pageContext.request.contextPath}/transactions" class="btn btn-secondary btn-sm">Reset Filters</a>
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
