package com.library.servlet;

import com.library.dao.MemberDAO;
import com.library.model.Member;
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
 * Servlet handling Member CRUD and searching operations.
 */
@WebServlet(name = "MemberServlet", urlPatterns = {"/members"})
public class MemberServlet extends HttpServlet {

    private final MemberDAO memberDAO = new MemberDAO();

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
                    request.getRequestDispatcher("/add-member.jsp").forward(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "search":
                    searchMembers(request, response);
                    break;
                case "list":
                default:
                    listMembers(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Error: " + e.getMessage());
            listMembersFallback(request, response);
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
                    addMember(request, response);
                    break;
                case "update":
                    updateMember(request, response);
                    break;
                case "delete":
                    deleteMember(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/members");
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            String encodedErr = URLEncoder.encode(e.getMessage() != null ? e.getMessage() : "Operation failed.", StandardCharsets.UTF_8);
            response.sendRedirect(request.getContextPath() + "/members?error=" + encodedErr);
        }
    }

    private void listMembers(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        List<Member> members = memberDAO.getAllMembers();
        request.setAttribute("memberList", members);
        request.getRequestDispatcher("/members.jsp").forward(request, response);
    }

    private void searchMembers(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String keyword = request.getParameter("keyword");
        List<Member> members;
        if (keyword != null && !keyword.trim().isEmpty()) {
            members = memberDAO.searchMembers(keyword);
            request.setAttribute("searchKeyword", keyword);
        } else {
            members = memberDAO.getAllMembers();
        }
        request.setAttribute("memberList", members);
        request.getRequestDispatcher("/members.jsp").forward(request, response);
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/members?error=Invalid+Member+ID");
            return;
        }

        int memberId = Integer.parseInt(idParam);
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) {
            response.sendRedirect(request.getContextPath() + "/members?error=Member+not+found");
            return;
        }

        request.setAttribute("member", member);
        request.getRequestDispatcher("/edit-member.jsp").forward(request, response);
    }

    private void addMember(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        if (name == null || name.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            phone == null || phone.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/members?error=All+fields+are+required");
            return;
        }

        Member member = new Member(name.trim(), email.trim(), phone.trim());
        memberDAO.addMember(member);

        String msg = URLEncoder.encode("Member '" + name.trim() + "' registered successfully!", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/members?msg=" + msg);
    }

    private void updateMember(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        int memberId = Integer.parseInt(request.getParameter("memberId"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");

        Member member = new Member(memberId, name.trim(), email.trim(), phone.trim());
        memberDAO.updateMember(member);

        String msg = URLEncoder.encode("Member details updated successfully!", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/members?msg=" + msg);
    }

    private void deleteMember(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        int memberId = Integer.parseInt(request.getParameter("memberId"));
        memberDAO.deleteMember(memberId);

        String msg = URLEncoder.encode("Member removed successfully!", StandardCharsets.UTF_8);
        response.sendRedirect(request.getContextPath() + "/members?msg=" + msg);
    }

    private void listMembersFallback(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            List<Member> members = memberDAO.getAllMembers();
            request.setAttribute("memberList", members);
        } catch (Exception ignored) {
        }
        request.getRequestDispatcher("/members.jsp").forward(request, response);
    }
}
