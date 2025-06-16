package com.bibliotheque.model;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private int utilisateurId;
    private int livreId;
    private LocalDate dateEmprunt;
    private LocalDate dateRetour;
    
    // Champs additionnels pour les jointures
    private String nomUtilisateur;
    private String emailUtilisateur;
    private String titreLivre;
    private String auteurLivre;
    
    // Constructeurs
    public Emprunt() {}
    
    public Emprunt(int utilisateurId, int livreId) {
        this.utilisateurId = utilisateurId;
        this.livreId = livreId;
        this.dateEmprunt = LocalDate.now();
    }
    
    public Emprunt(int id, int utilisateurId, int livreId, LocalDate dateEmprunt, LocalDate dateRetour) {
        this.id = id;
        this.utilisateurId = utilisateurId;
        this.livreId = livreId;
        this.dateEmprunt = dateEmprunt;
        this.dateRetour = dateRetour;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUtilisateurId() {
        return utilisateurId;
    }
    
    public void setUtilisateurId(int utilisateurId) {
        this.utilisateurId = utilisateurId;
    }
    
    public int getLivreId() {
        return livreId;
    }
    
    public void setLivreId(int livreId) {
        this.livreId = livreId;
    }
    
    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }
    
    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }
    
    public LocalDate getDateRetour() {
        return dateRetour;
    }
    
    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }
    
    public String getNomUtilisateur() {
        return nomUtilisateur;
    }
    
    public void setNomUtilisateur(String nomUtilisateur) {
        this.nomUtilisateur = nomUtilisateur;
    }
    
    public String getEmailUtilisateur() {
        return emailUtilisateur;
    }
    
    public void setEmailUtilisateur(String emailUtilisateur) {
        this.emailUtilisateur = emailUtilisateur;
    }
    
    public String getTitreLivre() {
        return titreLivre;
    }
    
    public void setTitreLivre(String titreLivre) {
        this.titreLivre = titreLivre;
    }
    
    public String getAuteurLivre() {
        return auteurLivre;
    }
    
    public void setAuteurLivre(String auteurLivre) {
        this.auteurLivre = auteurLivre;
    }
    
    // Méthodes utilitaires
    public boolean estEnCours() {
        return dateRetour == null;
    }
    
    public boolean estEnRetard() {
        if (dateRetour != null) return false;
        return dateEmprunt.plusDays(30).isBefore(LocalDate.now());
    }
    
    public long getNombreJoursEmprunt() {
        LocalDate dateFin = dateRetour != null ? dateRetour : LocalDate.now();
        return dateEmprunt.until(dateFin).getDays();
    }
    
    @Override
    public String toString() {
        return "Emprunt{" +
                "id=" + id +
                ", utilisateurId=" + utilisateurId +
                ", livreId=" + livreId +
                ", dateEmprunt=" + dateEmprunt +
                ", dateRetour=" + dateRetour +
                ", nomUtilisateur='" + nomUtilisateur + '\'' +
                ", titreLivre='" + titreLivre + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Emprunt emprunt = (Emprunt) obj;
        return id == emprunt.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}