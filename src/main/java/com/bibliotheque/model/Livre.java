package com.bibliotheque.model;

import java.time.LocalDateTime;

public class Livre {
    private int id;
    private String titre;
    private String auteur;
    private boolean disponible;
    private LocalDateTime dateCreation;
    
    // Constructeurs
    public Livre() {}
    
    public Livre(String titre, String auteur) {
        this.titre = titre;
        this.auteur = auteur;
        this.disponible = true;
    }
    
    public Livre(int id, String titre, String auteur, boolean disponible) {
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.disponible = disponible;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getAuteur() {
        return auteur;
    }
    
    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    @Override
    public String toString() {
        return "Livre{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", auteur='" + auteur + '\'' +
                ", disponible=" + disponible +
                ", dateCreation=" + dateCreation +
                '}';
    }
}