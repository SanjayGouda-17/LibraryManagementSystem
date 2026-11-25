package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.dao.IssueDAO;
import com.library.dao.MemberDAO;
import com.library.model.Book;
import com.library.model.Member;
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
 * Servlet handling Book Issuance operations.
 */
@WebServlet(name = "IssueServlet", urlPatterns = {"/issue"})
public class IssueServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final IssueDAO issueDAO = new IssueDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // Load books and members for dropdown selection in the issue form
            List<Book> books = bookDAO.getAllBooks();
            List<Member> members = memberDAO.getAllMembers();

            request.setAttribute("bookList", books);
            request.setAttribute("memberList", members);
            request.setAttribute("todayDate", LocalDate.now().toString());

            request.getRequestDispatcher("/issue-book.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error loading issue form: " + e.getMessage());
            request.getRequestDispatcher("/issue-book.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String bookIdParam = request.getParameter("bookId");
            String memberIdParam = request.getParameter("memberId");
            String issueDateParam = request.getParameter("issueDate");

            if (bookIdParam == null || bookIdParam.trim().isEmpty() ||
                memberIdParam == null || memberIdParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/issue?error=Please+select+both+a+book+and+a+member.");
                return;
            }

            int bookId = Integer.parseInt(bookIdParam);
            int memberId = Integer.parseInt(memberIdParam);

            Date issueDate;
            if (issueDateParam != null && !issueDateParam.trim().isEmpty()) {
                issueDate = Date.valueOf(issueDateParam);
            } else {
                issueDate = Date.valueOf(LocalDate.now());
            }

            // Perform transactional issue operation
            issueDAO.issueBook(bookId, memberId, issueDate);

            String successMsg = URLEncoder.encode("Book issued successfully!", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/transactions?msg=" + successMsg);

        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Failed to issue book.", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/issue?error=" + errorMsg);
        }
    }
}
