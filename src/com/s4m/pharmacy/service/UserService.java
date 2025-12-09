package com.s4m.pharmacy.service;

import com.s4m.pharmacy.db.DatabaseConnection;
import com.s4m.pharmacy.model.User;
import com.s4m.pharmacy.util.PasswordHasher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service pour la gestion des utilisateurs (CRUD)
 */
public class UserService {
    
    private DatabaseConnection dbConnection;
    
    public UserService() {
        this.dbConnection = new DatabaseConnection();
    }
    
    /**
     * Ajoute un nouvel utilisateur
     */
    public int ajouterUtilisateur(User user, String motDePasseClair) {
        validerUtilisateur(user);
        if (motDePasseClair == null || motDePasseClair.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est requis");
        }
        if (emailExiste(user.getEmail(), 0)) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        
        user.setMotDePasse(PasswordHasher.hashPassword(motDePasseClair));
        String sql = "INSERT INTO Utilisateur (nom, email, mot_de_passe, role) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getMotDePasse());
            pstmt.setString(4, user.getRole().name());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                    return user.getId();
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
        return -1;
    }
    
    /**
     * Récupère un utilisateur par son ID
     */
    public User getUtilisateurParId(int id) {
        return executerSelect("SELECT * FROM Utilisateur WHERE id = ?", pstmt -> pstmt.setInt(1, id));
    }
    
    /**
     * Récupère un utilisateur par son email
     */
    public User getUtilisateurParEmail(String email) {
        return executerSelect("SELECT * FROM Utilisateur WHERE email = ?", pstmt -> pstmt.setString(1, email));
    }
    
    /**
     * Liste tous les utilisateurs
     */
    public List<User> listerTousLesUtilisateurs() {
        return executerSelectListe("SELECT * FROM Utilisateur ORDER BY nom");
    }
    
    /**
     * Recherche des utilisateurs par nom ou email
     */
    public List<User> rechercherUtilisateurs(String termeRecherche) {
        return executerSelectListe("SELECT * FROM Utilisateur WHERE nom LIKE ? OR email LIKE ? ORDER BY nom",
            pstmt -> {
                String pattern = "%" + termeRecherche + "%";
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
            });
    }
    
    /**
     * Modifie un utilisateur
     */
    public boolean modifierUtilisateur(User user) {
        validerUtilisateur(user);
        if (user.getId() <= 0) {
            throw new IllegalArgumentException("L'ID de l'utilisateur est requis pour la modification");
        }
        if (emailExiste(user.getEmail(), user.getId())) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }
        
        String sql = "UPDATE Utilisateur SET nom = ?, email = ?, role = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, user.getNom());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getRole().name());
            pstmt.setInt(4, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'utilisateur : " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Modifie le mot de passe d'un utilisateur
     */
    public boolean modifierMotDePasse(int userId, String nouveauMotDePasseClair) {
        if (nouveauMotDePasseClair == null || nouveauMotDePasseClair.isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
        
        return executerUpdate("UPDATE Utilisateur SET mot_de_passe = ? WHERE id = ?",
            pstmt -> {
                pstmt.setString(1, PasswordHasher.hashPassword(nouveauMotDePasseClair));
                pstmt.setInt(2, userId);
            });
    }
    
    /**
     * Supprime un utilisateur
     */
    public boolean supprimerUtilisateur(int id) {
        return executerUpdate("DELETE FROM Utilisateur WHERE id = ?", pstmt -> pstmt.setInt(1, id));
    }
    
    private boolean emailExiste(String email, int idAExclure) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM Utilisateur WHERE email = ? AND id != ?")) {
            
            pstmt.setString(1, email);
            pstmt.setInt(2, idAExclure);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de l'email : " + e.getMessage());
        }
        return false;
    }
    
    private void validerUtilisateur(User user) {
        if (user.getNom() == null || user.getNom().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'utilisateur est requis");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty() || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("L'email de l'utilisateur est requis et valide");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Le rôle de l'utilisateur est requis");
        }
    }
    
    private User creerUtilisateurDepuisResultSet(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setNom(rs.getString("nom"));
        u.setEmail(rs.getString("email"));
        u.setMotDePasse(rs.getString("mot_de_passe"));
        String roleStr = rs.getString("role");
        if (roleStr != null) {
            try {
                u.setRole(User.Role.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                u.setRole(User.Role.USER);
            }
        }
        return u;
    }
    
    private User executerSelect(String sql, Setter setter) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            setter.set(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? creerUtilisateurDepuisResultSet(rs) : null;
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL : " + e.getMessage());
            return null;
        }
    }
    
    private List<User> executerSelectListe(String sql) {
        return executerSelectListe(sql, null);
    }
    
    private List<User> executerSelectListe(String sql, Setter setter) {
        List<User> list = new ArrayList<>();
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (setter != null) setter.set(pstmt);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(creerUtilisateurDepuisResultSet(rs));
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
