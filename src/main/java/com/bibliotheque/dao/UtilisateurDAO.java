package com.bibliotheque.dao;

import com.bibliotheque.config.DatabaseConfig;
import com.bibliotheque.model.Utilisateur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public List<Utilisateur> getAllUtilisateurs() throws SQLException {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT id, nom, email, date_inscription FROM utilisateurs ORDER BY nom";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Utilisateur u = new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email"),
                        rs.getTimestamp("date_inscription").toLocalDateTime()
                );
                utilisateurs.add(u);
            }
        }
        return utilisateurs;
    }

    public Utilisateur getUtilisateurById(int id) throws SQLException {
        String sql = "SELECT id, nom, email, date_inscription FROM utilisateurs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("email"),
                            rs.getTimestamp("date_inscription").toLocalDateTime()
                    );
                }
            }
        }
        return null;
    }

    public boolean ajouterUtilisateur(Utilisateur utilisateur) throws SQLException {
        String sql = "INSERT INTO utilisateurs (nom, email) VALUES (?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getEmail());
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        utilisateur.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean modifierUtilisateur(Utilisateur utilisateur) throws SQLException {
        String sql = "UPDATE utilisateurs SET nom = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, utilisateur.getNom());
            stmt.setString(2, utilisateur.getEmail());
            stmt.setInt(3, utilisateur.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean supprimerUtilisateur(int id) throws SQLException {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
}
