package com.library.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Security Filter intercepting HTTP requests to protect admin endpoints.
 * Demonstrates:
 * - Jakarta Servlet Filter lifecycle (init, doFilter, destroy)
 * - Session-based access control
 * - Preventing unauthenticated access to dashboard and CRUD operations
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        // Allow public resources without login: /login, /logout, static assets (/assets/*)
        boolean isPublicResource = path.equals("/login") || 
                                   path.equals("/logout") || 
                                   path.equals("/") || 
                                   path.equals("/index.jsp") || 
                                   path.startsWith("/assets/");

        if (isPublicResource) {
            chain.doFilter(req, res);
            return;
        }

        // Check if an authenticated admin session exists
        HttpSession session = request.getSession(false);
        boolean isLoggedIn = (session != null && session.getAttribute("adminUser") != null);

        if (isLoggedIn) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
