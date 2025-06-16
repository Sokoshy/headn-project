package com.bibliotheque.model;

import java.time.LocalDateTime;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private LocalDateTime dateInscription;
    
    // Constructeurs
    public Utilisateur() {}
    
    public Utilisateur(String nom, String email) {
        this.nom = nom;
        this.email = email;
    }
    
    public Utilisateur(int id, String nom, String email) {
        this.id = id;
        this.nom = nom;
        this.email = email;
    }
    
    public Utilisateur(int id, String nom, String email, LocalDateTime dateInscription) {
        this.id = id;
        this.nom = nom;
        this.email = email;
        this.dateInscription = dateInscription;
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public LocalDateTime getDateInscription() {
        return dateInscription;
    }
    
    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }
    
    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", email='" + email + '\'' +
                ", dateInscription=" + dateInscription +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utilisateur that = (Utilisateur) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}