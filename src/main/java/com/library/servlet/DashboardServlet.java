package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.dao.IssueDAO;
import com.library.dao.MemberDAO;
import com.library.model.IssuedBook;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Servlet handling the Dashboard view.
 * Fetches aggregate metrics, category statistics, and recent transactions from the database.
 */
@WebServlet(name = "DashboardServlet", urlPatterns = {"/dashboard"})
public class DashboardServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Aggregate metrics calculated via SQL COUNT and SUM
            int totalBooks = bookDAO.getTotalBooksCount();
            int totalCopies = bookDAO.getTotalCopiesCount();
            int availableCopies = bookDAO.getAvailableCopiesCount();
            int issuedCopies = issueDAO.getCurrentlyIssuedCount();
            int totalMembers = memberDAO.getTotalMembersCount();

            // Category breakdown using SQL GROUP BY and Java Map
            Map<String, Integer> categoryStats = bookDAO.getCategoryWiseStatistics();

            // Recent 5 transactions using SQL JOIN
            List<IssuedBook> recentTransactions = issueDAO.getRecentTransactions(5);

            // Pass data to JSP view via request attributes
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("totalCopies", totalCopies);
            request.setAttribute("availableCopies", availableCopies);
            request.setAttribute("issuedCopies", issuedCopies);
            request.setAttribute("totalMembers", totalMembers);
            request.setAttribute("categoryStats", categoryStats);
            request.setAttribute("recentTransactions", recentTransactions);

            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Database error loading dashboard: " + e.getMessage());
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        }
    }
}
