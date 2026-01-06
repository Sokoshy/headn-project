package com.bibliotheque.service;

import com.bibliotheque.config.ValidationUtil;
import com.bibliotheque.dao.UtilisateurDAO;
import com.bibliotheque.model.Utilisateur;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtilisateurService {
    private static final Logger logger = Logger.getLogger(UtilisateurService.class.getName());
    private UtilisateurDAO utilisateurDAO;

    public UtilisateurService() {
        this.utilisateurDAO = new UtilisateurDAO();
    }

    /**
     * Ajoute un nouvel utilisateur après validation
     */
    public ServiceResult<Utilisateur> ajouterUtilisateur(String nom, String email) {
        try {
            // Validation des entrées
            if (!ValidationUtil.isNotEmpty(nom) || !ValidationUtil.isNotEmpty(email)) {
                return ServiceResult.error("Nom et email sont obligatoires.");
            }

            if (!ValidationUtil.isValidNom(nom)) {
                return ServiceResult.error("Nom invalide. Le nom doit contenir entre 2 et 50 caractères.");
            }

            if (!ValidationUtil.isValidEmail(email)) {
                return ServiceResult.error("Adresse email invalide.");
            }

            // Création et ajout de l'utilisateur
            Utilisateur utilisateur = new Utilisateur(ValidationUtil.sanitizeInput(nom),
                                                   ValidationUtil.sanitizeInput(email));

            if (utilisateurDAO.ajouterUtilisateur(utilisateur)) {
                logger.info("Utilisateur ajouté avec succès: " + utilisateur.getEmail());
                return ServiceResult.success(utilisateur, "Utilisateur ajouté avec succès!");
            } else {
                logger.warning("Échec d'ajout de l'utilisateur: " + email);
                return ServiceResult.error("Erreur lors de l'ajout de l'utilisateur.");
            }

        } catch (SQLException e) {
            // Gestion spécifique des contraintes d'unicité
            if (e.getMessage() != null &&
                (e.getMessage().contains("duplicate key") ||
                 e.getMessage().contains("unique constraint") ||
                 e.getMessage().contains("email"))) {
                logger.warning("Tentative d'ajout d'un email dupliqué: " + email);
                return ServiceResult.error("Cette adresse email est déjà utilisée. Veuillez en choisir une autre.");
            } else {
                logger.log(Level.SEVERE, "Erreur base de données lors de l'ajout de l'utilisateur", e);
                return ServiceResult.error("Erreur base de données.");
            }
        }
    }

    /**
     * Modifie un utilisateur existant
     */
    public ServiceResult<Utilisateur> modifierUtilisateur(String idStr, String nom, String email) {
        try {
            // Validation de l'ID
            if (!ValidationUtil.isValidId(idStr)) {
                return ServiceResult.error("ID d'utilisateur invalide.");
            }

            int id = Integer.parseInt(idStr);

            // Validation des autres champs
            ServiceResult<Utilisateur> validationResult = ajouterUtilisateur(nom, email);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }

            // Modification de l'utilisateur
            Utilisateur utilisateur = new Utilisateur(id, ValidationUtil.sanitizeInput(nom),
                                                   ValidationUtil.sanitizeInput(email));

            if (utilisateurDAO.modifierUtilisateur(utilisateur)) {
                logger.info("Utilisateur modifié avec succès: ID " + id);
                return ServiceResult.success(utilisateur, "Utilisateur modifié avec succès!");
            } else {
                logger.warning("Échec de modification de l'utilisateur: ID " + id);
                return ServiceResult.error("Erreur lors de la modification de l'utilisateur.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la modification de l'utilisateur", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }

    /**
     * Supprime un utilisateur
     */
    public ServiceResult<Boolean> supprimerUtilisateur(String idStr) {
        try {
            if (!ValidationUtil.isValidId(idStr)) {
                return ServiceResult.error("ID d'utilisateur invalide.");
            }

            int id = Integer.parseInt(idStr);

            if (utilisateurDAO.supprimerUtilisateur(id)) {
                logger.info("Utilisateur supprimé avec succès: ID " + id);
                return ServiceResult.success(true, "Utilisateur supprimé avec succès!");
            } else {
                logger.warning("Échec de suppression de l'utilisateur: ID " + id);
                return ServiceResult.error("Erreur lors de la suppression de l'utilisateur.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la suppression de l'utilisateur", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }

    /**
     * Récupère tous les utilisateurs
     */
    public ServiceResult<List<Utilisateur>> getAllUtilisateurs() {
        try {
            List<Utilisateur> utilisateurs = utilisateurDAO.getAllUtilisateurs();
            return ServiceResult.success(utilisateurs, "Utilisateurs récupérés avec succès.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la récupération des utilisateurs", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }

    /**
     * Récupère un utilisateur par son ID
     */
    public ServiceResult<Utilisateur> getUtilisateurById(String idStr) {
        try {
            if (!ValidationUtil.isValidId(idStr)) {
                return ServiceResult.error("ID d'utilisateur invalide.");
            }

            int id = Integer.parseInt(idStr);
            Utilisateur utilisateur = utilisateurDAO.getUtilisateurById(id);

            if (utilisateur != null) {
                return ServiceResult.success(utilisateur, "Utilisateur trouvé.");
            } else {
                return ServiceResult.error("Utilisateur non trouvé.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la récupération de l'utilisateur", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
}