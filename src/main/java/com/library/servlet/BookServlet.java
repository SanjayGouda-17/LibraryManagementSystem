package com.library.servlet;

import com.library.dao.BookDAO;
import com.library.model.Book;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet handling Book CRUD, searching, and sorting operations.
 */
@WebServlet(name = "BookServlet", urlPatterns = {"/books"})
public class BookServlet extends HttpServlet {

    private final BookDAO bookDAO = new BookDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "search":
                    searchBooks(request, response);
                    break;
                case "list":
                default:
                    listBooks(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            listBooksFallback(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "add":
                    addBook(request, response);
                    break;
                case "update":
                    updateBook(request, response);
                    break;
                case "delete":
                    deleteBook(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/books");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String encodedErr = URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Operation failed.", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/books?error=" + encodedErr);
        }
    }

    private void listBooks(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");

        List<Book> books;
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            books = bookDAO.getBooksSorted(sortBy, order);
            request.setAttribute("currentSort", sortBy);
            request.setAttribute("currentOrder", order);
        } else {
            books = bookDAO.getAllBooks();
        }

        request.setAttribute("bookList", books);
        request.getRequestDispatcher("/books.jsp").forward(request, response);
    }

    private void searchBooks(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        String keyword = request.getParameter("keyword");
        List<Book> books;
        if (keyword != null && !keyword.trim().isEmpty()) {
            books = bookDAO.searchBooks(keyword);
            request.setAttribute("searchKeyword", keyword);
        } else {
            books = bookDAO.getAllBooks();
        }

        request.setAttribute("bookList", books);
        request.getRequestDispatcher("/books.jsp").forward(request, response);
    }

    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/add-book.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/books?error=Invalid+Book+ID");
            return;
        }

        int bookId = Integer.parseInt(idParam);
        Book book = bookDAO.getBookById(bookId);
        if (book == null) {
            response.sendRedirect(request.getContextPath() + "/books?error=Book+not+found");
            return;
        }

        request.setAttribute("book", book);
        request.getRequestDispatcher("/edit-book.jsp").forward(request, response);
    }

    private void addBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        String qtyParam = request.getParameter("totalQuantity");

        if (title == null || title.trim().isEmpty() ||
            author == null || author.trim().isEmpty() ||
            category == null || category.trim().isEmpty() ||
            qtyParam == null || qtyParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/books?error=All+fields+are+required");
            return;
        }

        int totalQuantity = Integer.parseInt(qtyParam);
        if (totalQuantity <= 0) {
            response.sendRedirect(request.getContextPath() + "/books?error=Quantity+must+be+greater+than+0");
            return;
        }

        Book book = new Book(title.trim(), author.trim(), category.trim(), totalQuantity, totalQuantity);
        bookDAO.addBook(book);

        String msg = URLEncoder.encode("Book '" + title.trim() + "' added successfully!", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/books?msg=" + msg);
    }

    private void updateBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        String title = request.getParameter("title");
        String author = request.getParameter("author");
        String category = request.getParameter("category");
        int totalQuantity = Integer.parseInt(request.getParameter("totalQuantity"));

        if (totalQuantity <= 0) {
            response.sendRedirect(request.getContextPath() + "/books?error=Quantity+must+be+greater+than+0");
            return;
        }

        Book book = new Book();
        book.setBookId(bookId);
        book.setTitle(title.trim());
        book.setAuthor(author.trim());
        book.setCategory(category.trim());
        book.setTotalQuantity(totalQuantity);

        bookDAO.updateBook(book);

        String msg = URLEncoder.encode("Book updated successfully!", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/books?msg=" + msg);
    }

    private void deleteBook(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        
        int bookId = Integer.parseInt(request.getParameter("bookId"));
        bookDAO.deleteBook(bookId);

        String msg = URLEncoder.encode("Book deleted successfully!", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/books?msg=" + msg);
    }

    private void listBooksFallback(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Book> books = bookDAO.getAllBooks();
            request.setAttribute("bookList", books);
        } catch (Exception ignored) {
        }
        request.getRequestDispatcher("/books.jsp").forward(request, response);
    }
}
