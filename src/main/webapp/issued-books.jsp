<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.IssuedBook" %>
<%
    request.setAttribute("activePage", "return");
    request.setAttribute("headerTitle", "Return Books");
    request.setAttribute("headerSubtitle", "Process returns for currently issued books and restock inventory.");

    List<IssuedBook> activeIssuedList = (List<IssuedBook>) request.getAttribute("activeIssuedList");
    String todayDate = (String) request.getAttribute("todayDate");
    if (todayDate == null) {
        todayDate = java.time.LocalDate.now().toString();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Return Books - LibraryHub</title>
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
                    <span style="font-size: 14px; color: var(--text-muted);">
                        Active Borrowed Books: <strong><%= activeIssuedList != null ? activeIssuedList.size() : 0 %></strong>
                    </span>
                    <a href="${pageContext.request.contextPath}/issue" class="btn btn-primary btn-sm">+ Issue New Book</a>
                </div>

                <div class="panel">
                    <div class="panel-header">
                        <span class="panel-title">Currently Issued Transactions (Pending Return)</span>
                    </div>
                    <div class="panel-body" style="padding: 0;">
                        <% if (activeIssuedList != null && !activeIssuedList.isEmpty()) { %>
                            <div class="table-responsive">
                                <table class="table">
                                    <thead>
                                        <tr>
                                            <th>Tx ID</th>
                                            <th>Book Title</th>
                                            <th>Member Name</th>
                                            <th>Member Email</th>
                                            <th>Issue Date</th>
                                            <th>Status</th>
                                            <th style="text-align: right;">Action</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% for (IssuedBook tx : activeIssuedList) { %>
                                            <tr>
                                                <td>#<%= tx.getIssueId() %></td>
                                                <td class="title-cell"><%= tx.getBookTitle() %></td>
                                                <td><%= tx.getMemberName() %></td>
                                                <td><%= tx.getMemberEmail() %></td>
                                                <td><%= tx.getIssueDate() %></td>
                                                <td><span class="badge badge-issued">ISSUED</span></td>
                                                <td style="text-align: right;">
                                                    <form action="${pageContext.request.contextPath}/return" method="post" style="display:inline-flex; gap: 6px; align-items: center;"
                                                          onsubmit="return confirm('Confirm return of \'' + '<%= tx.getBookTitle().replace("'", "\\'") %>' + '\' by <%= tx.getMemberName().replace("'", "\\'") %>? Available stock will increase by 1.');">
                                                        <input type="hidden" name="issueId" value="<%= tx.getIssueId() %>">
                                                        <input type="hidden" name="returnDate" value="<%= todayDate %>">
                                                        <button type="submit" class="btn btn-primary btn-sm" style="background-color: #059669;">
                                                            📥 Mark Returned
                                                        </button>
                                                    </form>
                                                </td>
                                            </tr>
                                        <% } %>
                                    </tbody>
                                </table>
                            </div>
                        <% } else { %>
                            <div class="empty-state">
                                <div class="empty-icon">✅</div>
                                <h3>No Books Currently Issued</h3>
                                <p>All borrowed books have been returned to the library inventory.</p>
                                <a href="${pageContext.request.contextPath}/issue" class="btn btn-primary btn-sm">Issue a Book</a>
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
