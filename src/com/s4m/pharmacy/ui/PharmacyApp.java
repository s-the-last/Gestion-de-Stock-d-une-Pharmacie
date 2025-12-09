package com.s4m.pharmacy.ui;

import com.s4m.pharmacy.db.DatabaseConnection;
import com.s4m.pharmacy.model.User;
import com.s4m.pharmacy.service.AuthService;
import com.s4m.pharmacy.service.CategoryService;
import com.s4m.pharmacy.service.ProductService;
import com.s4m.pharmacy.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Point d'entrée JavaFX : affiche d'abord l'écran de connexion puis un tableau produits.
 */
public class PharmacyApp extends Application {

    private final AuthService authService = new AuthService();
    private final ProductService productService = new ProductService();
    private final CategoryService categoryService = new CategoryService();
    private final UserService userService = new UserService();
    private Stage primaryStage;

    @Override
    public void init() {
        // Initialise la base de données (création BD + données par défaut)
        DatabaseConnection.initialiser();
    }

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Pharmacy - JavaFX");
        showLoginView();
        this.primaryStage.show();
    }

    private void showLoginView() {
        try {
            FXMLLoader loader = new FXMLLoader(PharmacyApp.class.getResource("LoginView.fxml"));
            loader.setControllerFactory(type -> {
                if (type == LoginController.class) {
                    return new LoginController(authService, this::onLoginSuccess);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger la vue de connexion", e);
        }
    }

    private void showDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(PharmacyApp.class.getResource("DashboardView.fxml"));
            loader.setControllerFactory(type -> {
                if (type == DashboardController.class) {
                    return new DashboardController(
                            user,
                            authService,
                            productService,
                            categoryService,
                            userService,
                            this::onLogout);
                }
                try {
                    return type.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger le tableau de bord", e);
        }
    }

    private void onLoginSuccess(User user) {
        showDashboard(user);
    }

    private void onLogout() {
        authService.seDeconnecter();
        showLoginView();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

