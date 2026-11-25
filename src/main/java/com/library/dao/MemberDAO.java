package com.library.dao;

import com.library.model.Member;
import com.library.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Member operations.
 * Handles adding, retrieving, updating, deleting, and searching library members.
 */
public class MemberDAO {

    /**
     * Adds a new member to the library.
     */
    public boolean addMember(Member member) throws SQLException {
        String sql = "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all registered members.
     */
    public List<Member> getAllMembers() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT member_id, name, email, phone FROM members ORDER BY member_id DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                members.add(extractMemberFromResultSet(rs));
            }
        }
        return members;
    }

    /**
     * Retrieves a member by ID.
     */
    public Member getMemberById(int memberId) throws SQLException {
        String sql = "SELECT member_id, name, email, phone FROM members WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMemberFromResultSet(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing member's profile.
     */
    public boolean updateMember(Member member) throws SQLException {
        String sql = "UPDATE members SET name = ?, email = ?, phone = ? WHERE member_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getPhone());
            ps.setInt(4, member.getMemberId());
            
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a member by ID.
     * Enforces business rule: Member cannot be deleted if they currently hold issued books.
     */
    public boolean deleteMember(int memberId) throws SQLException {
        // Business Rule: Check for active issued books
        String checkSql = "SELECT COUNT(*) FROM issued_books WHERE member_id = ? AND status = 'ISSUED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
            
            checkPs.setInt(1, memberId);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalStateException("Cannot delete member: This member currently has " + rs.getInt(1) + " book(s) issued.");
                }
            }

            // Check if member has past transaction history (foreign key audit protection)
            String historySql = "SELECT COUNT(*) FROM issued_books WHERE member_id = ?";
            try (PreparedStatement histPs = conn.prepareStatement(historySql)) {
                histPs.setInt(1, memberId);
                try (ResultSet rs = histPs.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        throw new IllegalStateException("Cannot delete member: Member has historical transaction records. Deleting them violates audit trail.");
                    }
                }
            }

            String deleteSql = "DELETE FROM members WHERE member_id = ?";
            try (PreparedStatement delPs = conn.prepareStatement(deleteSql)) {
                delPs.setInt(1, memberId);
                return delPs.executeUpdate() > 0;
            }
        }
    }

    /**
     * Searches members by name using SQL LIKE.
     */
    public List<Member> searchMembers(String keyword) throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT member_id, name, email, phone FROM members WHERE name LIKE ? OR email LIKE ? ORDER BY name ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword.trim() + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    members.add(extractMemberFromResultSet(rs));
                }
            }
        }
        return members;
    }

    /**
     * Returns total count of registered members.
     */
    public int getTotalMembersCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM members";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    // Helper method to map ResultSet row to Member object
    private Member extractMemberFromResultSet(ResultSet rs) throws SQLException {
        return new Member(
            rs.getInt("member_id"),
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("phone")
        );
    }
}
