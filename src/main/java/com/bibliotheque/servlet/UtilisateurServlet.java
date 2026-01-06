package com.bibliotheque.servlet;

import com.bibliotheque.service.UtilisateurService;
import com.bibliotheque.service.ServiceResult;
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
    private UtilisateurService utilisateurService;

    @Override
    public void init() throws ServletException {
        utilisateurService = new UtilisateurService();
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
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) action = "add";

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
    }

    private void listerUtilisateurs(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServiceResult<List<Utilisateur>> result = utilisateurService.getAllUtilisateurs();
        if (result.isSuccess()) {
            request.setAttribute("utilisateurs", result.getData());
        } else {
            request.setAttribute("error", result.getMessage());
        }
        request.getRequestDispatcher("/WEB-INF/views/utilisateurs.jsp").forward(request, response);
    }

    private void ajouterUtilisateur(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        ServiceResult<Utilisateur> result = utilisateurService.ajouterUtilisateur(nom, email);
        if (result.isSuccess()) {
            session.setAttribute("message", result.getMessage());
        } else {
            session.setAttribute("error", result.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }

    private void afficherFormulaireEdit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        if (idStr != null) {
            ServiceResult<Utilisateur> result = utilisateurService.getUtilisateurById(idStr);
            if (result.isSuccess()) {
                request.setAttribute("utilisateur", result.getData());
                // Affiche le formulaire d'édition dans utilisateurs.jsp
                request.getRequestDispatcher("/WEB-INF/views/utilisateurs.jsp").forward(request, response);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }

    private void modifierUtilisateur(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        HttpSession session = request.getSession();

        ServiceResult<Utilisateur> result = utilisateurService.modifierUtilisateur(idStr, nom, email);
        if (result.isSuccess()) {
            session.setAttribute("message", result.getMessage());
        } else {
            session.setAttribute("error", result.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }

    private void supprimerUtilisateur(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idStr = request.getParameter("id");
        HttpSession session = request.getSession();

        ServiceResult<Boolean> result = utilisateurService.supprimerUtilisateur(idStr);
        if (result.isSuccess()) {
            session.setAttribute("message", result.getMessage());
        } else {
            session.setAttribute("error", result.getMessage());
        }
        response.sendRedirect(request.getContextPath() + "/utilisateurs");
    }
}
