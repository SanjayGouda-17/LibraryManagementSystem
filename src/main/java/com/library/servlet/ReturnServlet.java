package com.library.servlet;

import com.library.dao.IssueDAO;
import com.library.model.IssuedBook;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Servlet handling Book Return operations.
 */
@WebServlet(name = "ReturnServlet", urlPatterns = {"/return"})
public class ReturnServlet extends HttpServlet {

    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Load all currently issued transactions that are pending return
            List<IssuedBook> activeIssuedBooks = issueDAO.getActiveIssuedBooks();

            request.setAttribute("activeIssuedList", activeIssuedBooks);
            request.setAttribute("todayDate", LocalDate.now().toString());

            request.getRequestDispatcher("/issued-books.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading active issues: " + e.getMessage());
            request.getRequestDispatcher("/issued-books.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String issueIdParam = request.getParameter("issueId");
            String returnDateParam = request.getParameter("returnDate");

            if (issueIdParam == null || issueIdParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/return?error=Invalid+Transaction+ID");
                return;
            }

            int issueId = Integer.parseInt(issueIdParam);
            Date returnDate;
            if (returnDateParam != null && !returnDateParam.trim().isEmpty()) {
                returnDate = Date.valueOf(returnDateParam);
            } else {
                returnDate = Date.valueOf(LocalDate.now());
            }

            // Perform transactional return operation
            issueDAO.returnBook(issueId, returnDate);

            String successMsg = URLEncoder.encode("Book (Tx #" + issueId + ") returned successfully! Stock updated.", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/return?msg=" + successMsg);

        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Failed to return book.", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/return?error=" + errorMsg);
        }
    }
}
