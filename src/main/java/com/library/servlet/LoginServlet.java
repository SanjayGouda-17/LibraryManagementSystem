package com.library.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Servlet handling Admin Authentication and Session Management.
 * Reads credentials dynamically from admin.properties, allowing
 * instant password changes without rebuilding or restarting the server.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login", "/logout"})
public class LoginServlet extends HttpServlet {

    private static final String PROP_FILE_PATH = "c:/Users/iamsa/Desktop/LMS/admin.properties";
    private static final String DEFAULT_USER = "admin";
    private static final String DEFAULT_PASS = "admin1234";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login?msg=Logged+out+successfully.");
            return;
        }

        // If already logged in, redirect straight to dashboard
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("adminUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String inputUser = request.getParameter("username");
        String inputPass = request.getParameter("password");

        // Load credentials from admin.properties dynamically
        String validUser = DEFAULT_USER;
        String validPass = DEFAULT_PASS;

        File file = new File(PROP_FILE_PATH);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Properties props = new Properties();
                props.load(fis);
                validUser = props.getProperty("username", DEFAULT_USER).trim();
                validPass = props.getProperty("password", DEFAULT_PASS).trim();
            } catch (Exception e) {
                System.err.println("Warning: Could not read admin.properties: " + e.getMessage());
            }
        }

        // Authenticate credentials
        if (inputUser != null && validUser.equalsIgnoreCase(inputUser.trim()) && 
            inputPass != null && validPass.equals(inputPass.trim())) {
            
            HttpSession session = request.getSession(true);
            session.setAttribute("adminUser", validUser);
            session.setMaxInactiveInterval(30 * 60); // 30 minutes session timeout

            response.sendRedirect(request.getContextPath() + "/dashboard");
        } else {
            // Display only clean error message with no hints
            response.sendRedirect(request.getContextPath() + "/login?error=Invalid+username+or+password.");
        }
    }
}
