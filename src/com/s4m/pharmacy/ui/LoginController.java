package com.s4m.pharmacy.ui;

import com.s4m.pharmacy.model.User;
import com.s4m.pharmacy.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.function.Consumer;

/**
 * Contrôleur pour l'écran de connexion.
 */
public class LoginController {

    private final AuthService authService;
    private final Consumer<User> onLoginSuccess;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    public LoginController(AuthService authService, Consumer<User> onLoginSuccess) {
        this.authService = authService;
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs");
            statusLabel.setStyle("-fx-text-fill: crimson;");
            return;
        }

        try {
            boolean ok = authService.seConnecter(email, password);
            if (ok) {
                statusLabel.setText("Connexion réussie");
                statusLabel.setStyle("-fx-text-fill: green;");
                onLoginSuccess.accept(authService.getUtilisateurConnecte());
            } else {
                statusLabel.setText("Identifiants incorrects");
                statusLabel.setStyle("-fx-text-fill: crimson;");
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur de connexion à la base de données");
            statusLabel.setStyle("-fx-text-fill: crimson;");
        }
    }
}

