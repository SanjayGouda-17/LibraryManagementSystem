<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.library.model.Book" %>
<%@ page import="com.library.model.Member" %>
<%
    request.setAttribute("activePage", "issue");
    request.setAttribute("headerTitle", "Issue Book");
    request.setAttribute("headerSubtitle", "Checkout a book to a registered library member.");

    List<Book> bookList = (List<Book>) request.getAttribute("bookList");
    List<Member> memberList = (List<Member>) request.getAttribute("memberList");
    String todayDate = (String) request.getAttribute("todayDate");
    if (todayDate == null) {
        todayDate = java.time.LocalDate.now().toString();
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Issue Book - LibraryHub</title>
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
                        <span class="panel-title">Circulation Checkout Form</span>
                        <a href="${pageContext.request.contextPath}/return" class="btn btn-secondary btn-sm">View Issued Books &rarr;</a>
                    </div>
                    <div class="panel-body">
                        <form action="${pageContext.request.contextPath}/issue" method="post" class="form-grid" 
                              onsubmit="return validateIssueForm(this);">
                            
                            <div class="form-group">
                                <label for="bookSelect" class="form-label">Select Book to Issue *</label>
                                <select id="bookSelect" name="bookId" class="form-control" required>
                                    <option value="">-- Choose a Book --</option>
                                    <% if (bookList != null) { %>
                                        <% for (Book b : bookList) { %>
                                            <option value="<%= b.getBookId() %>" <%= b.getAvailableQuantity() <= 0 ? "disabled" : "" %>>
                                                <%= b.getTitle() %> by <%= b.getAuthor() %> 
                                                (<%= b.getAvailableQuantity() > 0 ? b.getAvailableQuantity() + " copies available" : "OUT OF STOCK" %>)
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                                <span class="form-hint">Only books with at least 1 available copy can be checked out.</span>
                            </div>

                            <div class="form-group">
                                <label for="memberSelect" class="form-label">Select Member *</label>
                                <select id="memberSelect" name="memberId" class="form-control" required>
                                    <option value="">-- Choose a Member --</option>
                                    <% if (memberList != null) { %>
                                        <% for (Member m : memberList) { %>
                                            <option value="<%= m.getMemberId() %>">
                                                <%= m.getName() %> &lt;<%= m.getEmail() %>&gt; (ID: #<%= m.getMemberId() %>)
                                            </option>
                                        <% } %>
                                    <% } %>
                                </select>
                            </div>

                            <div class="form-group">
                                <label for="issueDate" class="form-label">Issue Date *</label>
                                <input type="date" id="issueDate" name="issueDate" class="form-control" 
                                       value="<%= todayDate %>" required>
                            </div>

                            <div class="form-actions">
                                <button type="submit" class="btn btn-primary" style="background-color: #059669;">Confirm & Issue Book</button>
                                <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-secondary">Cancel</a>
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
