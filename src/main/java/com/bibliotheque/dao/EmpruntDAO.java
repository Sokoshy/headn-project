package com.bibliotheque.dao;

import com.bibliotheque.config.DatabaseConfig;
import com.bibliotheque.model.Emprunt;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmpruntDAO {

    public List<Emprunt> getEmpruntsActifs() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM emprunts_actifs";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Emprunt e = new Emprunt();
                e.setId(rs.getInt("id"));
                e.setNomUtilisateur(rs.getString("nom_utilisateur"));
                e.setEmailUtilisateur(rs.getString("email"));
                e.setTitreLivre(rs.getString("titre"));
                e.setAuteurLivre(rs.getString("auteur"));
                e.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
                emprunts.add(e);
            }
        }
        return emprunts;
    }

    public List<Emprunt> getHistoriqueEmprunts() throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = "SELECT * FROM historique_emprunts";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Emprunt e = new Emprunt();
                e.setId(rs.getInt("id"));
                e.setNomUtilisateur(rs.getString("nom_utilisateur"));
                e.setEmailUtilisateur(rs.getString("email"));
                e.setTitreLivre(rs.getString("titre"));
                e.setAuteurLivre(rs.getString("auteur"));
                e.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
                Date retour = rs.getDate("date_retour");
                if (retour != null) e.setDateRetour(retour.toLocalDate());
                emprunts.add(e);
            }
        }
        return emprunts;
    }

    public List<Emprunt> getEmpruntsByUtilisateur(int utilisateurId) throws SQLException {
        List<Emprunt> emprunts = new ArrayList<>();
        String sql = 
            "SELECT e.id, e.utilisateur_id, e.livre_id, e.date_emprunt, e.date_retour, " +
            "u.nom as nom_utilisateur, u.email as email_utilisateur, " +
            "l.titre as titre_livre, l.auteur as auteur_livre " +
            "FROM emprunts e " +
            "JOIN utilisateurs u ON e.utilisateur_id = u.id " +
            "JOIN livres l ON e.livre_id = l.id " +
            "WHERE e.utilisateur_id = ? " +
            "ORDER BY e.date_emprunt DESC";
        
        try (Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, utilisateurId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Emprunt emprunt = new Emprunt();
                emprunt.setId(rs.getInt("id"));
                emprunt.setUtilisateurId(rs.getInt("utilisateur_id"));
                emprunt.setLivreId(rs.getInt("livre_id"));
                emprunt.setDateEmprunt(rs.getDate("date_emprunt").toLocalDate());
                
                Date dateRetour = rs.getDate("date_retour");
                if (dateRetour != null) {
                    emprunt.setDateRetour(dateRetour.toLocalDate());
                }
                
                emprunt.setNomUtilisateur(rs.getString("nom_utilisateur"));
                emprunt.setEmailUtilisateur(rs.getString("email_utilisateur"));
                emprunt.setTitreLivre(rs.getString("titre_livre"));
                emprunt.setAuteurLivre(rs.getString("auteur_livre"));
                
                emprunts.add(emprunt);
            }
        }
        return emprunts;
    }

    public boolean ajouterEmprunt(Emprunt emprunt) throws SQLException {
        String sql = "INSERT INTO emprunts (utilisateur_id, livre_id, date_emprunt) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, emprunt.getUtilisateurId());
            stmt.setInt(2, emprunt.getLivreId());
            stmt.setDate(3, Date.valueOf(emprunt.getDateEmprunt()));
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        emprunt.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean enregistrerRetour(int empruntId, LocalDate dateRetour) throws SQLException {
        String sql = "UPDATE emprunts SET date_retour = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(dateRetour));
            stmt.setInt(2, empruntId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Retourne l'identifiant du livre associé à un emprunt donné.
     */
    public Integer getLivreIdByEmpruntId(int empruntId) throws SQLException {
        String sql = "SELECT livre_id FROM emprunts WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, empruntId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("livre_id");
                }
            }
        }
        return null;
    }
}