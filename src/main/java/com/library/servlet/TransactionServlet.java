package com.library.servlet;

import com.library.dao.IssueDAO;
import com.library.model.IssuedBook;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet handling Transaction History and filtering.
 */
@WebServlet(name = "TransactionServlet", urlPatterns = {"/transactions"})
public class TransactionServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String status = request.getParameter("status");
        String memberSearch = request.getParameter("memberSearch");
        String bookSearch = request.getParameter("bookSearch");

        try {
            List<IssuedBook> list = issueDAO.getTransactionsWithFilter(status, memberSearch, bookSearch);

            request.setAttribute("transactionList", list);
            request.setAttribute("selectedStatus", status != null ? status : "ALL");
            request.setAttribute("memberSearch", memberSearch);
            request.setAttribute("bookSearch", bookSearch);

            request.getRequestDispatcher("/transactions.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading transaction history: " + e.getMessage());
            request.getRequestDispatcher("/transactions.jsp").forward(request, response);
        }
    }
}
