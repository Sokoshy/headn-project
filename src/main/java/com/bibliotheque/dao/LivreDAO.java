package com.bibliotheque.dao;

import com.bibliotheque.config.DatabaseConfig;
import com.bibliotheque.model.Livre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivreDAO {
    
    public List<Livre> getAllLivres() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT id, titre, auteur, disponible FROM livres ORDER BY titre";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Livre livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setDisponible(rs.getBoolean("disponible"));
                livres.add(livre);
            }
        }
        return livres;
    }
    
    public List<Livre> getLivresDisponibles() throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT id, titre, auteur, disponible FROM livres WHERE disponible = TRUE ORDER BY titre";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Livre livre = new Livre();
                livre.setId(rs.getInt("id"));
                livre.setTitre(rs.getString("titre"));
                livre.setAuteur(rs.getString("auteur"));
                livre.setDisponible(rs.getBoolean("disponible"));
                livres.add(livre);
            }
        }
        return livres;
    }
    
    public Livre getLivreById(int id) throws SQLException {
        String sql = "SELECT id, titre, auteur, disponible FROM livres WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Livre livre = new Livre();
                    livre.setId(rs.getInt("id"));
                    livre.setTitre(rs.getString("titre"));
                    livre.setAuteur(rs.getString("auteur"));
                    livre.setDisponible(rs.getBoolean("disponible"));
                    return livre;
                }
            }
        }
        return null;
    }
    
    public List<Livre> rechercherLivres(String terme) throws SQLException {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT id, titre, auteur, disponible FROM livres " +
                    "WHERE LOWER(titre) LIKE LOWER(?) OR LOWER(auteur) LIKE LOWER(?) " +
                    "ORDER BY titre";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String termeLike = "%" + terme + "%";
            stmt.setString(1, termeLike);
            stmt.setString(2, termeLike);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Livre livre = new Livre();
                    livre.setId(rs.getInt("id"));
                    livre.setTitre(rs.getString("titre"));
                    livre.setAuteur(rs.getString("auteur"));
                    livre.setDisponible(rs.getBoolean("disponible"));
                    livres.add(livre);
                }
            }
        }
        return livres;
    }
    
    public boolean ajouterLivre(Livre livre) throws SQLException {
        String sql = "INSERT INTO livres (titre, auteur, disponible) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setBoolean(3, livre.isDisponible());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        livre.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean modifierLivre(Livre livre) throws SQLException {
        String sql = "UPDATE livres SET titre = ?, auteur = ?, disponible = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, livre.getTitre());
            stmt.setString(2, livre.getAuteur());
            stmt.setBoolean(3, livre.isDisponible());
            stmt.setInt(4, livre.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean supprimerLivre(int id) throws SQLException {
        String sql = "DELETE FROM livres WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }
    
    public boolean updateDisponibilite(int id, boolean disponible) throws SQLException {
        String sql = "UPDATE livres SET disponible = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, disponible);
            stmt.setInt(2, id);
            
            return stmt.executeUpdate() > 0;
        }
    }
}