package com.s4m.pharmacy.service;

import com.s4m.pharmacy.db.DatabaseConnection;
import com.s4m.pharmacy.model.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la gestion des catégories (CRUD)
 */
public class CategoryService {
    
    private DatabaseConnection dbConnection;
    
    public CategoryService() {
        this.dbConnection = new DatabaseConnection();
    }
    
    /**
     * Ajoute une nouvelle catégorie
     */
    public int ajouterCategorie(Category category) {
        if (category.getNom() == null || category.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie est requis");
        }
        
        String sql = "INSERT INTO Categorie (nom, description) VALUES (?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, category.getNom());
            pstmt.setString(2, category.getDescription());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    category.setId(rs.getInt(1));
                    return category.getId();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Récupère une catégorie par son ID
     */
    public Category getCategorieParId(int id) {
        return executerSelect("SELECT * FROM Categorie WHERE id = ?", 
            pstmt -> pstmt.setInt(1, id));
    }
    
    /**
     * Liste toutes les catégories
     */
    public List<Category> listerToutesLesCategories() {
        return executerSelectListe("SELECT * FROM Categorie ORDER BY nom");
    }
    
    /**
     * Recherche des catégories par nom
     */
    public List<Category> rechercherCategories(String termeRecherche) {
        return executerSelectListe("SELECT * FROM Categorie WHERE nom LIKE ? ORDER BY nom",
            pstmt -> pstmt.setString(1, "%" + termeRecherche + "%"));
    }
    
    /**
     * Modifie une catégorie
     */
    public boolean modifierCategorie(Category category) {
        if (category.getNom() == null || category.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de la catégorie est requis");
        }
        
        String sql = "UPDATE Categorie SET nom = ?, description = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, category.getNom());
            pstmt.setString(2, category.getDescription());
            pstmt.setInt(3, category.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la catégorie : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Supprime une catégorie
     */
    public boolean supprimerCategorie(int id) {
        return executerUpdate("DELETE FROM Categorie WHERE id = ?", pstmt -> pstmt.setInt(1, id));
    }
    
    private Category creerCategorieDepuisResultSet(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setNom(rs.getString("nom"));
        c.setDescription(rs.getString("description"));
        return c;
    }
    
    private Category executerSelect(String sql, Setter setter) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setter.set(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? creerCategorieDepuisResultSet(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return null;
        }
    }
    
    private List<Category> executerSelectListe(String sql) {
        return executerSelectListe(sql, null);
    }
    
    private List<Category> executerSelectListe(String sql, Setter setter) {
        List<Category> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (setter != null) setter.set(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(creerCategorieDepuisResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
        }
        return list;
    }
    
    private boolean executerUpdate(String sql, Setter setter) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setter.set(pstmt);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return false;
        }
    }
    
    @FunctionalInterface
    private interface Setter {
        void set(PreparedStatement pstmt) throws SQLException;
    }
}
