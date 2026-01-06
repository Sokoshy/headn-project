package com.bibliotheque.servlet;

import com.bibliotheque.dao.EmpruntDAO;
import com.bibliotheque.dao.LivreDAO;
import com.bibliotheque.dao.UtilisateurDAO;
import com.bibliotheque.model.Emprunt;
import com.bibliotheque.model.Livre;
import com.bibliotheque.model.Utilisateur;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/emprunts")
public class EmpruntServlet extends HttpServlet {
    private EmpruntDAO empruntDAO;
    private LivreDAO livreDAO;
    private UtilisateurDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        empruntDAO = new EmpruntDAO();
        livreDAO = new LivreDAO();
        utilisateurDAO = new UtilisateurDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "list";
        // Récupérer les messages de la session (pour affichage après redirection)
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
                    listerEmpruntsActifs(request, response);
                    break;
                case "historique":
                    listerHistorique(request, response);
                    break;
                default:
                    listerEmpruntsActifs(request, response);
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
                    ajouterEmprunt(request, response);
                    break;
                case "retour":
                    enregistrerRetour(request, response);
                    break;
                default:
                    ajouterEmprunt(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Erreur base de données", e);
        }
    }

    private void listerEmpruntsActifs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        List<Emprunt> emprunts;
        List<Utilisateur> utilisateurs = utilisateurDAO.getAllUtilisateurs();
        List<Livre> livresDisponibles = livreDAO.getLivresDisponibles();

        String filtreUtilisateur = request.getParameter("filtreUtilisateur");
        
        if (filtreUtilisateur != null && !filtreUtilisateur.isEmpty()) {
            try {
                int userId = Integer.parseInt(filtreUtilisateur);
                emprunts = empruntDAO.getEmpruntsByUtilisateur(userId);
            } catch (NumberFormatException e) {
                emprunts = empruntDAO.getEmpruntsActifs();
            }
        } else {
            emprunts = empruntDAO.getEmpruntsActifs();
        }

    request.setAttribute("emprunts", emprunts);
    request.setAttribute("utilisateurs", utilisateurs);
    request.setAttribute("livresDisponibles", livresDisponibles);
    request.getRequestDispatcher("/WEB-INF/views/emprunts.jsp").forward(request, response);
}

    private void listerHistorique(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        List<Emprunt> emprunts = empruntDAO.getHistoriqueEmprunts();
        List<Utilisateur> utilisateurs = utilisateurDAO.getAllUtilisateurs();
        List<Livre> livresDisponibles = livreDAO.getLivresDisponibles();
        request.setAttribute("emprunts", emprunts);
        request.setAttribute("utilisateurs", utilisateurs);
        request.setAttribute("livresDisponibles", livresDisponibles);
        request.getRequestDispatcher("/WEB-INF/views/emprunts.jsp").forward(request, response);
    }

    private void ajouterEmprunt(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String utilisateurIdStr = request.getParameter("utilisateurId");
        String livreIdStr = request.getParameter("livreId");
        HttpSession session = request.getSession();
        if (utilisateurIdStr != null && livreIdStr != null) {
            try {
                int utilisateurId = Integer.parseInt(utilisateurIdStr);
                int livreId = Integer.parseInt(livreIdStr);
                Emprunt emprunt = new Emprunt(utilisateurId, livreId);
                if (empruntDAO.ajouterEmprunt(emprunt)) {
                    livreDAO.updateDisponibilite(livreId, false);
                    session.setAttribute("message", "Emprunt enregistré !");
                } else {
                    session.setAttribute("error", "Erreur lors de l'emprunt.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Identifiants invalides.");
            }
        } else {
            session.setAttribute("error", "Paramètres manquants.");
        }
        response.sendRedirect(request.getContextPath() + "/emprunts");
    }

    private void enregistrerRetour(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        String empruntIdStr = request.getParameter("empruntId");
        HttpSession session = request.getSession();
        if (empruntIdStr != null) {
            try {
                int empruntId = Integer.parseInt(empruntIdStr);
                LocalDate dateRetour = LocalDate.now();
                if (empruntDAO.enregistrerRetour(empruntId, dateRetour)) {
                    Integer livreId = empruntDAO.getLivreIdByEmpruntId(empruntId);
                    if (livreId != null) {
                        livreDAO.updateDisponibilite(livreId, true);
                    }
                    session.setAttribute("message", "Retour enregistré !");
                } else {
                    session.setAttribute("error", "Erreur lors de l'enregistrement du retour.");
                }
            } catch (NumberFormatException e) {
                session.setAttribute("error", "Identifiant d'emprunt invalide.");
            }
        } else {
            session.setAttribute("error", "Paramètre manquant.");
        }
        response.sendRedirect(request.getContextPath() + "/emprunts");
    }
}
