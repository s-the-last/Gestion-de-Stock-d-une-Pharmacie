package com.s4m.pharmacy.util;

import java.security.MessageDigest;

/**
 * Utilitaire pour le hashage des mots de passe avec SHA-256
 */
public class PasswordHasher {
    
    /**
     * Hash un mot de passe avec SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) sb.append('0');
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            System.err.println("Erreur lors du hashage du mot de passe : " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Vérifie si un mot de passe correspond à un hash
     */
    public static boolean verifyPassword(String password, String hash) {
        String hashed = hashPassword(password);
        return hashed != null && hashed.equals(hash);
    }
}
