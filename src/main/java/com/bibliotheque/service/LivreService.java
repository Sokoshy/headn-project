package com.bibliotheque.service;

import com.bibliotheque.config.ValidationUtil;
import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.model.Livre;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LivreService {
    private static final Logger logger = Logger.getLogger(LivreService.class.getName());
    private LivreDAO livreDAO;
    
    public LivreService() {
        this.livreDAO = new LivreDAO();
    }
    
    /**
     * Ajoute un nouveau livre après validation
     */
    public ServiceResult<Livre> ajouterLivre(String titre, String auteur) {
        try {
            // Validation des entrées
            if (!ValidationUtil.isNotEmpty(titre) || !ValidationUtil.isNotEmpty(auteur)) {
                return ServiceResult.error("Titre et auteur sont obligatoires.");
            }
            
            if (!ValidationUtil.isValidTitre(titre)) {
                return ServiceResult.error("Titre invalide. Le titre doit contenir entre 1 et 200 caractères.");
            }
            
            if (!ValidationUtil.isValidAuteur(auteur)) {
                return ServiceResult.error("Auteur invalide. Le nom doit contenir entre 2 et 100 caractères.");
            }
            
            // Création et ajout du livre
            Livre livre = new Livre(ValidationUtil.sanitizeInput(titre), 
                                   ValidationUtil.sanitizeInput(auteur));
            
            if (livreDAO.ajouterLivre(livre)) {
                logger.info("Livre ajouté avec succès: " + livre.getTitre());
                return ServiceResult.success(livre, "Livre ajouté avec succès!");
            } else {
                logger.warning("Échec d'ajout du livre: " + titre);
                return ServiceResult.error("Erreur lors de l'ajout du livre.");
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de l'ajout du livre", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
    
    /**
     * Modifie un livre existant
     */
    public ServiceResult<Livre> modifierLivre(String idStr, String titre, String auteur) {
        try {
            // Validation de l'ID
            if (!ValidationUtil.isValidId(idStr)) {
                return ServiceResult.error("ID de livre invalide.");
            }
            
            int id = Integer.parseInt(idStr);
            
            // Validation des autres champs
            ServiceResult<Livre> validationResult = ajouterLivre(titre, auteur);
            if (!validationResult.isSuccess()) {
                return validationResult;
            }
            
            // Modification du livre
            Livre livre = new Livre(id, ValidationUtil.sanitizeInput(titre), 
                                   ValidationUtil.sanitizeInput(auteur));
            
            if (livreDAO.modifierLivre(livre)) {
                logger.info("Livre modifié avec succès: ID " + id);
                return ServiceResult.success(livre, "Livre modifié avec succès!");
            } else {
                logger.warning("Échec de modification du livre: ID " + id);
                return ServiceResult.error("Erreur lors de la modification du livre.");
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la modification du livre", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
    
    /**
     * Supprime un livre
     */
    public ServiceResult<Boolean> supprimerLivre(String idStr) {
        try {
            if (!ValidationUtil.isValidId(idStr)) {
                return ServiceResult.error("ID de livre invalide.");
            }
            
            int id = Integer.parseInt(idStr);
            
            if (livreDAO.supprimerLivre(id)) {
                logger.info("Livre supprimé avec succès: ID " + id);
                return ServiceResult.success(true, "Livre supprimé avec succès!");
            } else {
                logger.warning("Échec de suppression du livre: ID " + id);
                return ServiceResult.error("Erreur lors de la suppression du livre.");
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la suppression du livre", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
    
    /**
     * Récupère tous les livres
     */
    public ServiceResult<List<Livre>> getAllLivres() {
        try {
            List<Livre> livres = livreDAO.getAllLivres();
            return ServiceResult.success(livres, "Livres récupérés avec succès.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la récupération des livres", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
    
    /**
     * Récupère les livres disponibles
     */
    public ServiceResult<List<Livre>> getLivresDisponibles() {
        try {
            List<Livre> livres = livreDAO.getLivresDisponibles();
            return ServiceResult.success(livres, "Livres disponibles récupérés avec succès.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la récupération des livres disponibles", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
    
    /**
     * Recherche des livres par terme
     */
    public ServiceResult<List<Livre>> rechercherLivres(String terme) {
        try {
            if (!ValidationUtil.isNotEmpty(terme)) {
                return getAllLivres();
            }
            
            String termeSanitized = ValidationUtil.sanitizeInput(terme);
            List<Livre> livres = livreDAO.rechercherLivres(termeSanitized);
            return ServiceResult.success(livres, "Recherche effectuée avec succès.");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la recherche des livres", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
    
    /**
     * Récupère un livre par son ID
     */
    public ServiceResult<Livre> getLivreById(String idStr) {
        try {
            if (!ValidationUtil.isValidId(idStr)) {
                return ServiceResult.error("ID de livre invalide.");
            }
            
            int id = Integer.parseInt(idStr);
            Livre livre = livreDAO.getLivreById(id);
            
            if (livre != null) {
                return ServiceResult.success(livre, "Livre trouvé.");
            } else {
                return ServiceResult.error("Livre non trouvé.");
            }
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erreur base de données lors de la récupération du livre", e);
            return ServiceResult.error("Erreur base de données.");
        }
    }
}