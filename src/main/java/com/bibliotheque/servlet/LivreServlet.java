package com.bibliotheque.servlet;

import com.bibliotheque.config.CSRFUtil;
import com.bibliotheque.config.ValidationUtil;
import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.model.Livre;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/livres")
public class LivreServlet extends HttpServlet {
    private LivreDAO livreDAO;
    
    @Override
    public void init() throws ServletException {
        livreDAO = new LivreDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "list";
        
        try {
            switch (action) {
                case "list":
                    listerLivres(request, response);
                    break;
                case "disponibles":
                    listerLivresDisponibles(request, response);
                    break;
                case "recherche":
                    rechercherLivres(request, response);
                    break;
                case "edit":
                    afficherFormulaireEdit(request, response);
                    break;
                case "delete":
                    supprimerLivre(request, response);
                    break;
                default:
                    listerLivres(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erreur base de données", e);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Validation CSRF
        if (!CSRFUtil.validateToken(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF invalide");
            return;
        }
        
        String action = request.getParameter("action");
        if (action == null) action = "add";
        
        try {
            switch (action) {
                case "add":
                    ajouterLivre(request, response);
                    break;
                case "update":
                    modifierLivre(request, response);
                    break;
                default:
                    ajouterLivre(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erreur base de données", e);
        }
    }
    
    private void listerLivres(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        CSRFUtil.generateToken(request);
        
        List<Livre> livres = livreDAO.getAllLivres();
        request.setAttribute("livres", livres);
        request.setAttribute("titre", "Tous les livres");
        request.getRequestDispatcher("/WEB-INF/views/livres.jsp").forward(request, response);
    }
    
    private void listerLivresDisponibles(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        List<Livre> livres = livreDAO.getLivresDisponibles();
        request.setAttribute("livres", livres);
        request.setAttribute("titre", "Livres disponibles");
        request.getRequestDispatcher("/WEB-INF/views/livres.jsp").forward(request, response);
    }
    
    private void rechercherLivres(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String terme = request.getParameter("terme");
        if (terme != null && !terme.trim().isEmpty()) {
            List<Livre> livres = livreDAO.rechercherLivres(terme);
            request.setAttribute("livres", livres);
            request.setAttribute("titre", "Résultats de recherche pour: " + terme);
            request.setAttribute("terme", terme);
        } else {
            List<Livre> livres = livreDAO.getAllLivres();
            request.setAttribute("livres", livres);
            request.setAttribute("titre", "Tous les livres");
        }
        request.getRequestDispatcher("/WEB-INF/views/livres.jsp").forward(request, response);
    }
    
    private void ajouterLivre(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String titre = ValidationUtil.sanitizeInput(request.getParameter("titre"));
        String auteur = ValidationUtil.sanitizeInput(request.getParameter("auteur"));
        if (!ValidationUtil.isNotEmpty(titre) || !ValidationUtil.isNotEmpty(auteur)) {
            request.setAttribute("error", "Titre et auteur sont obligatoires.");
        } else if (!ValidationUtil.isValidTitre(titre)) {
            request.setAttribute("error", "Titre invalide. Le titre doit contenir entre 1 et 200 caractères.");
        } else if (!ValidationUtil.isValidAuteur(auteur)) {
            request.setAttribute("error", "Auteur invalide. Le nom doit contenir entre 2 et 100 caractères.");
        } else {
            Livre livre = new Livre(titre, auteur);
            if (livreDAO.ajouterLivre(livre)) {
                request.setAttribute("message", "Livre ajouté avec succès!");
            } else {
                request.setAttribute("error", "Erreur lors de l'ajout du livre.");
            }
        }
        CSRFUtil.generateToken(request);
        listerLivres(request, response);
    }
    
    private void modifierLivre(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String idStr = request.getParameter("id");
        String titre = request.getParameter("titre");
        String auteur = request.getParameter("auteur");
        String disponibleStr = request.getParameter("disponible");
        
        if (idStr != null && titre != null && !titre.trim().isEmpty() && 
            auteur != null && !auteur.trim().isEmpty()) {
            
            try {
                int id = Integer.parseInt(idStr);
                boolean disponible = "on".equals(disponibleStr) || "true".equals(disponibleStr);
                
                Livre livre = new Livre(id, titre.trim(), auteur.trim(), disponible);
                if (livreDAO.modifierLivre(livre)) {
                    request.setAttribute("message", "Livre modifié avec succès!");
                } else {
                    request.setAttribute("error", "Erreur lors de la modification du livre.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID du livre invalide.");
            }
        } else {
            request.setAttribute("error", "Tous les champs sont obligatoires.");
        }
        response.sendRedirect(request.getContextPath() + "/livres");
    }
    
    private void afficherFormulaireEdit(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {

        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Livre livre = livreDAO.getLivreById(id);
                if (livre != null) {
                    request.setAttribute("livre", livre);
                    request.getRequestDispatcher("/WEB-INF/views/livres.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // ID invalide
            }
        }
        response.sendRedirect(request.getContextPath() + "/livres");
    }
    
    private void supprimerLivre(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException, SQLException {
        
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                if (livreDAO.supprimerLivre(id)) {
                    request.setAttribute("message", "Livre supprimé avec succès!");
                } else {
                    request.setAttribute("error", "Erreur lors de la suppression du livre.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "ID du livre invalide.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/livres");
    }
}