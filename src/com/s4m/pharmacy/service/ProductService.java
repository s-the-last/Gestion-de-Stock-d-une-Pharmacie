package com.s4m.pharmacy.service;

import com.s4m.pharmacy.db.DatabaseConnection;
import com.s4m.pharmacy.model.Product;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la gestion des produits (CRUD)
 */
public class ProductService {
    
    private DatabaseConnection dbConnection;
    
    public ProductService() {
        this.dbConnection = new DatabaseConnection();
    }
    
    /**
     * Ajoute un nouveau produit
     */
    public int ajouterProduit(Product product) {
        validerProduit(product);
        
        String sql = "INSERT INTO Produit (nom, description, prix, quantite, date_expiration, id_categorie) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, product.getNom());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrix());
            pstmt.setInt(4, product.getQuantite());
            pstmt.setDate(5, Date.valueOf(product.getDateExpiration()));
            pstmt.setInt(6, product.getIdCategorie());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getInt(1));
                    return product.getId();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du produit : " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Récupère un produit par son ID
     */
    public Product getProduitParId(int id) {
        return executerSelect("SELECT * FROM Produit WHERE id = ?", pstmt -> pstmt.setInt(1, id));
    }
    
    /**
     * Liste tous les produits
     */
    public List<Product> listerTousLesProduits() {
        return executerSelectListe("SELECT * FROM Produit ORDER BY nom");
    }
    
    /**
     * Recherche des produits par nom
     */
    public List<Product> rechercherProduitsParNom(String termeRecherche) {
        return executerSelectListe("SELECT * FROM Produit WHERE nom LIKE ? ORDER BY nom",
            pstmt -> pstmt.setString(1, "%" + termeRecherche + "%"));
    }
    
    /**
     * Recherche des produits par catégorie
     */
    public List<Product> rechercherProduitsParCategorie(int idCategorie) {
        return executerSelectListe("SELECT * FROM Produit WHERE id_categorie = ? ORDER BY nom",
            pstmt -> pstmt.setInt(1, idCategorie));
    }
    
    /**
     * Recherche des produits par date d'expiration
     */
    public List<Product> rechercherProduitsParDateExpiration(LocalDate date) {
        return executerSelectListe("SELECT * FROM Produit WHERE date_expiration = ? ORDER BY nom",
            pstmt -> pstmt.setDate(1, Date.valueOf(date)));
    }
    
    /**
     * Récupère les produits avec stock bas (< 10)
     */
    public List<Product> getProduitsStockBas() {
        return executerSelectListe("SELECT * FROM Produit WHERE quantite < 10 ORDER BY quantite ASC");
    }
    
    /**
     * Modifie un produit
     */
    public boolean modifierProduit(Product product) {
        validerProduit(product);
        
        String sql = "UPDATE Produit SET nom = ?, description = ?, prix = ?, quantite = ?, date_expiration = ?, id_categorie = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getNom());
            pstmt.setString(2, product.getDescription());
            pstmt.setDouble(3, product.getPrix());
            pstmt.setInt(4, product.getQuantite());
            pstmt.setDate(5, Date.valueOf(product.getDateExpiration()));
            pstmt.setInt(6, product.getIdCategorie());
            pstmt.setInt(7, product.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification du produit : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Supprime un produit
     */
    public boolean supprimerProduit(int id) {
        return executerUpdate("DELETE FROM Produit WHERE id = ?", pstmt -> pstmt.setInt(1, id));
    }
    
    private void validerProduit(Product product) {
        if (product.getNom() == null || product.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du produit est requis");
        }
        if (product.getPrix() < 0) throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        if (product.getQuantite() < 0) throw new IllegalArgumentException("La quantité ne peut pas être négative");
        if (product.getDateExpiration() == null) throw new IllegalArgumentException("La date d'expiration est requise");
        if (product.getIdCategorie() <= 0) throw new IllegalArgumentException("La catégorie est requise");
    }
    
    private Product creerProduitDepuisResultSet(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setNom(rs.getString("nom"));
        p.setDescription(rs.getString("description"));
        p.setPrix(rs.getDouble("prix"));
        p.setQuantite(rs.getInt("quantite"));
        Date dateExp = rs.getDate("date_expiration");
        if (dateExp != null) p.setDateExpiration(dateExp.toLocalDate());
        p.setIdCategorie(rs.getInt("id_categorie"));
        return p;
    }
    
    private Product executerSelect(String sql, Setter setter) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setter.set(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? creerProduitDepuisResultSet(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return null;
        }
    }
    
    private List<Product> executerSelectListe(String sql) {
        return executerSelectListe(sql, null);
    }
    
    private List<Product> executerSelectListe(String sql, Setter setter) {
        List<Product> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (setter != null) setter.set(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(creerProduitDepuisResultSet(rs));
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
