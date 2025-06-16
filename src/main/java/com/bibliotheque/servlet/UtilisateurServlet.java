package com.bibliotheque.servlet;

import com.bibliotheque.dao.UtilisateurDAO;
import com.bibliotheque.model.Utilisateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/utilisateurs")
public class UtilisateurServlet extends HttpServlet {
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";
        HttpSession session = request.getSession(false);
        if (session != null) {
            String message = (String) session.getAttribute("message");
            String error = (String) session.getAttribute("error");
            if (message != null) {
                request.setAttribute("message", message);
                session.removeAttribute("message");
            }
            if (error != null) {
                request.setAttribute("error", error);
                session.removeAttribute("error");
            }
        }
        try {
            switch (action) {
                case "list":
                    listerUtilisateurs(request, response);
                    break;
                case "edit":
                    afficherFormulaireEdit(request, response);
                    break;
                case "delete":
                    supprimerUtilisateur(request, response);
                    break;
                default:
                    listerUtilisateurs(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erreur base de données", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "add";
        try {
            switch (action) {
                case "add":
                    ajouterUtilisateur(request, response);
                    break;
                case "update":
                    modifierUtilisateur(request, response);
                    break;
                default:
                    ajouterUtilisateur(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erreur base de données", e);
        }
    }

    private void listerUtilisateurs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Utilisateur> utilisateurs = utilisateurDAO.getAllUtilisateurs();
        request.setAttribute("utilisateurs", utilisateurs);
        request.getRequestDispatcher("/WEB-INF/views/utilisateurs.jsp").forward(request, response);
    }

    private void ajouterUtilisateur(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        HttpSession session = request.getSession();
        if (nom != null && !nom.trim().isEmpty() && email != null && !email.trim().isEmpty()) {
            Utilisateur utilisateur = new Utilisateur(nom.trim(), email.trim());
            if (utilisateurDAO.ajouterUtilisateur(utilisateur)) {
                session.setAttribute("message", "Utilisateur ajouté avec succès !");
            } else {
                session.setAttribute("error", "Erreur lors de l'ajout de l'utilisateur.");
            }
        } else {
            session.setAttribute("error", "Nom et email sont obligatoires.");
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }

    private void afficherFormulaireEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                Utilisateur utilisateur = utilisateurDAO.getUtilisateurById(id);
                if (utilisateur != null) {
                    request.setAttribute("utilisateur", utilisateur);
                    // Affiche le formulaire d'édition dans utilisateurs.jsp
                    request.getRequestDispatcher("/WEB-INF/views/utilisateurs.jsp").forward(request, response);
                    return;
                }
            } catch (NumberFormatException e) {
                // ID invalide
            }
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }

    private void modifierUtilisateur(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String idStr = request.getParameter("id");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        HttpSession session = request.getSession();
        if (idStr != null && nom != null && !nom.trim().isEmpty() && email != null && !email.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr);
                Utilisateur utilisateur = new Utilisateur(id, nom.trim(), email.trim());
                if (utilisateurDAO.modifierUtilisateur(utilisateur)) {
                    session.setAttribute("message", "Utilisateur modifié avec succès !");
                } else {
                    session.setAttribute("error", "Erreur lors de la modification de l'utilisateur.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "ID utilisateur invalide.");
            }
        } else {
            session.setAttribute("error", "Tous les champs sont obligatoires.");
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }

    private void supprimerUtilisateur(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession();
        if (idStr != null) {
            try {
                int id = Integer.parseInt(idStr);
                if (utilisateurDAO.supprimerUtilisateur(id)) {
                    session.setAttribute("message", "Utilisateur supprimé avec succès !");
                } else {
                    session.setAttribute("error", "Erreur lors de la suppression de l'utilisateur.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "ID utilisateur invalide.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }
}
