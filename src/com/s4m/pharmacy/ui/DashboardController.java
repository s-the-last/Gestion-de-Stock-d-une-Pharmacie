package com.s4m.pharmacy.ui;

import com.s4m.pharmacy.model.Category;
import com.s4m.pharmacy.model.Product;
import com.s4m.pharmacy.model.User;
import com.s4m.pharmacy.service.AuthService;
import com.s4m.pharmacy.service.CategoryService;
import com.s4m.pharmacy.service.ProductService;
import com.s4m.pharmacy.service.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Tableau de bord complet avec validation, confirmations, recherche et contrôle d'accès.
 */
public class DashboardController {

    private final User currentUser;
    private final AuthService authService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final Runnable onLogout;

    private final ObservableList<Product> produits = FXCollections.observableArrayList();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final Map<Integer, Category> categoryById = new HashMap<>();

    // Listes filtrées pour la recherche
    private FilteredList<Product> filteredProduits;
    private FilteredList<Category> filteredCategories;
    private FilteredList<User> filteredUsers;

    @FXML
    private Label welcomeLabel;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab userTab;

    // Produits
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, String> colNom;
    @FXML private TableColumn<Product, String> colPrix;
    @FXML private TableColumn<Product, Integer> colQuantite;
    @FXML private TableColumn<Product, String> colExpiration;
    @FXML private TableColumn<Product, String> colCategorie;
    @FXML private TextField productNameField;
    @FXML private TextArea productDescField;
    @FXML private TextField productPriceField;
    @FXML private TextField productQtyField;
    @FXML private DatePicker productDateField;
    @FXML private ComboBox<Category> productCategoryCombo;
    @FXML private Label productStatus;
    @FXML private TextField productSearchField;

    // Catégories
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, String> catNomCol;
    @FXML private TableColumn<Category, String> catDescCol;
    @FXML private TextField categoryNameField;
    @FXML private TextField categoryDescField;
    @FXML private Label categoryStatus;
    @FXML private TextField categorySearchField;

    // Utilisateurs
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> userNomCol;
    @FXML private TableColumn<User, String> userEmailCol;
    @FXML private TableColumn<User, String> userRoleCol;
    @FXML private TextField userNameField;
    @FXML private TextField userEmailField;
    @FXML private PasswordField userPasswordField;
    @FXML private ComboBox<User.Role> userRoleCombo;
    @FXML private Label userStatus;
    @FXML private TextField userSearchField;

    public DashboardController(User currentUser,
                               AuthService authService,
                               ProductService productService,
                               CategoryService categoryService,
                               UserService userService,
                               Runnable onLogout) {
        this.currentUser = currentUser;
        this.authService = authService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.onLogout = onLogout;
    }

    @FXML
    private void initialize() {
        welcomeLabel.setText("Bienvenue " + currentUser.getNom() +
                (currentUser.isAdmin() ? " (Admin)" : ""));

        // Masquer l'onglet Utilisateurs si pas admin
        if (!currentUser.isAdmin() && userTab != null) {
            tabPane.getTabs().remove(userTab);
        }

        // Initialiser les listes filtrées
        filteredProduits = new FilteredList<>(produits, p -> true);
        filteredCategories = new FilteredList<>(categories, c -> true);
        filteredUsers = new FilteredList<>(users, u -> true);

        // Table produits avec formatage
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        colPrix.setCellValueFactory(c -> {
            double prix = c.getValue().getPrix();
            return new SimpleStringProperty(String.format("%.2f €", prix));
        });
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colExpiration.setCellValueFactory(c -> {
            LocalDate date = c.getValue().getDateExpiration();
            return new SimpleStringProperty(date != null ? 
                date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "");
        });
        colCategorie.setCellValueFactory(c -> {
            Category cat = categoryById.get(c.getValue().getIdCategorie());
            return new SimpleStringProperty(cat != null ? cat.getNom() : "");
        });
        productTable.setItems(filteredProduits);
        productTable.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> fillProductForm(newV));

        // Recherche produits
        if (productSearchField != null) {
            productSearchField.textProperty().addListener((obs, oldV, newV) -> {
                filteredProduits.setPredicate(p -> 
                    p.getNom().toLowerCase().contains(newV.toLowerCase()) ||
                    (p.getDescription() != null && p.getDescription().toLowerCase().contains(newV.toLowerCase()))
                );
            });
        }

        // Table catégories
        catNomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        catDescCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDescription()));
        categoryTable.setItems(filteredCategories);
        categoryTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> fillCategoryForm(n));

        // Recherche catégories
        if (categorySearchField != null) {
            categorySearchField.textProperty().addListener((obs, oldV, newV) -> {
                filteredCategories.setPredicate(c -> 
                    c.getNom().toLowerCase().contains(newV.toLowerCase()) ||
                    (c.getDescription() != null && c.getDescription().toLowerCase().contains(newV.toLowerCase()))
                );
            });
        }

        // Table utilisateurs
        userNomCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        userEmailCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail()));
        userRoleCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRole().name()));
        userTable.setItems(filteredUsers);
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> fillUserForm(n));
        userRoleCombo.setItems(FXCollections.observableArrayList(User.Role.values()));

        // Recherche utilisateurs
        if (userSearchField != null) {
            userSearchField.textProperty().addListener((obs, oldV, newV) -> {
                filteredUsers.setPredicate(u -> 
                    u.getNom().toLowerCase().contains(newV.toLowerCase()) ||
                    u.getEmail().toLowerCase().contains(newV.toLowerCase())
                );
            });
        }

        refreshCategories();
        refreshProduits();
        refreshUsers();
    }

    // ==================== PRODUITS ====================
    @FXML
    private void refreshProduits() {
        try {
            produits.setAll(productService.listerTousLesProduits());
            showStatus(productStatus, "Liste rafraîchie", true);
        } catch (Exception e) {
            showError("Erreur lors du rafraîchissement : " + e.getMessage());
            showStatus(productStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void addProduit() {
        try {
            Product p = buildProductFromForm(0);
            int id = productService.ajouterProduit(p);
            if (id > 0) {
                showStatus(productStatus, "Produit ajouté avec succès", true);
                refreshProduits();
                clearProductForm();
            } else {
                showStatus(productStatus, "Échec de l'ajout", false);
            }
        } catch (IllegalArgumentException e) {
            showStatus(productStatus, e.getMessage(), false);
        } catch (Exception e) {
            showError("Erreur lors de l'ajout : " + e.getMessage());
            showStatus(productStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void updateProduit() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus(productStatus, "Sélectionnez un produit à modifier", false);
            return;
        }
        try {
            Product p = buildProductFromForm(selected.getId());
            boolean ok = productService.modifierProduit(p);
            showStatus(productStatus, ok ? "Produit mis à jour avec succès" : "Échec de la mise à jour", ok);
            if (ok) refreshProduits();
        } catch (IllegalArgumentException e) {
            showStatus(productStatus, e.getMessage(), false);
        } catch (Exception e) {
            showError("Erreur lors de la modification : " + e.getMessage());
            showStatus(productStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void deleteProduit() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus(productStatus, "Sélectionnez un produit à supprimer", false);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer le produit ?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer : " + selected.getNom() + " ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean ok = productService.supprimerProduit(selected.getId());
                showStatus(productStatus, ok ? "Produit supprimé avec succès" : "Échec de la suppression", ok);
                if (ok) {
                    refreshProduits();
                    clearProductForm();
                }
            } catch (Exception e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
                showStatus(productStatus, "Erreur de connexion à la base de données", false);
            }
        }
    }

    private Product buildProductFromForm(int id) {
        // Validation
        String nom = productNameField.getText().trim();
        if (nom.isEmpty()) throw new IllegalArgumentException("Le nom est requis");
        
        String desc = productDescField.getText();
        
        double prix;
        try {
            prix = Double.parseDouble(productPriceField.getText().trim());
            if (prix < 0) throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Le prix doit être un nombre valide");
        }
        
        int qty;
        try {
            qty = Integer.parseInt(productQtyField.getText().trim());
            if (qty < 0) throw new IllegalArgumentException("La quantité ne peut pas être négative");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("La quantité doit être un nombre entier valide");
        }
        
        LocalDate date = productDateField.getValue();
        if (date == null) throw new IllegalArgumentException("La date d'expiration est requise");
        
        Category cat = productCategoryCombo.getValue();
        if (cat == null) throw new IllegalArgumentException("Choisissez une catégorie");
        
        Product p = new Product(nom, desc, prix, qty, date, cat.getId());
        if (id > 0) p.setId(id);
        return p;
    }

    private void fillProductForm(Product p) {
        if (p == null) return;
        productNameField.setText(p.getNom());
        productDescField.setText(p.getDescription());
        productPriceField.setText(String.valueOf(p.getPrix()));
        productQtyField.setText(String.valueOf(p.getQuantite()));
        productDateField.setValue(p.getDateExpiration());
        Category cat = categoryById.get(p.getIdCategorie());
        productCategoryCombo.setValue(cat);
    }

    private void clearProductForm() {
        productNameField.clear();
        productDescField.clear();
        productPriceField.clear();
        productQtyField.clear();
        productDateField.setValue(null);
        productCategoryCombo.setValue(null);
    }

    // ==================== CATEGORIES ====================
    @FXML
    private void refreshCategories() {
        try {
            categories.setAll(categoryService.listerToutesLesCategories());
            categoryById.clear();
            for (Category c : categories) {
                categoryById.put(c.getId(), c);
            }
            productCategoryCombo.setItems(categories);
            showStatus(categoryStatus, "Liste rafraîchie", true);
        } catch (Exception e) {
            showError("Erreur lors du rafraîchissement : " + e.getMessage());
            showStatus(categoryStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void addCategorie() {
        String nom = categoryNameField.getText().trim();
        if (nom.isEmpty()) {
            showStatus(categoryStatus, "Le nom est requis", false);
            return;
        }
        String desc = categoryDescField.getText();
        try {
            Category c = new Category(nom, desc);
            int id = categoryService.ajouterCategorie(c);
            showStatus(categoryStatus, id > 0 ? "Catégorie ajoutée avec succès" : "Échec de l'ajout", id > 0);
            if (id > 0) {
                refreshCategories();
                refreshProduits();
                clearCategoryForm();
            }
        } catch (IllegalArgumentException e) {
            showStatus(categoryStatus, e.getMessage(), false);
        } catch (Exception e) {
            showError("Erreur lors de l'ajout : " + e.getMessage());
            showStatus(categoryStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void updateCategorie() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus(categoryStatus, "Sélectionnez une catégorie à modifier", false);
            return;
        }
        String nom = categoryNameField.getText().trim();
        if (nom.isEmpty()) {
            showStatus(categoryStatus, "Le nom est requis", false);
            return;
        }
        try {
            selected.setNom(nom);
            selected.setDescription(categoryDescField.getText());
            boolean ok = categoryService.modifierCategorie(selected);
            showStatus(categoryStatus, ok ? "Catégorie mise à jour avec succès" : "Échec de la mise à jour", ok);
            if (ok) {
                refreshCategories();
                refreshProduits();
            }
        } catch (IllegalArgumentException e) {
            showStatus(categoryStatus, e.getMessage(), false);
        } catch (Exception e) {
            showError("Erreur lors de la modification : " + e.getMessage());
            showStatus(categoryStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void deleteCategorie() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus(categoryStatus, "Sélectionnez une catégorie à supprimer", false);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la catégorie ?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer : " + selected.getNom() + " ?\n" +
                "Attention : cette catégorie ne peut pas être supprimée si elle contient des produits.");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean ok = categoryService.supprimerCategorie(selected.getId());
                showStatus(categoryStatus, ok ? "Catégorie supprimée avec succès" : 
                    "Échec : la catégorie est liée à des produits", ok);
                if (ok) {
                    refreshCategories();
                    refreshProduits();
                    clearCategoryForm();
                }
            } catch (Exception e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
                showStatus(categoryStatus, "Erreur de connexion à la base de données", false);
            }
        }
    }

    private void fillCategoryForm(Category c) {
        if (c == null) return;
        categoryNameField.setText(c.getNom());
        categoryDescField.setText(c.getDescription());
    }

    private void clearCategoryForm() {
        categoryNameField.clear();
        categoryDescField.clear();
    }

    // ==================== UTILISATEURS ====================
    @FXML
    private void refreshUsers() {
        if (!currentUser.isAdmin()) return;
        try {
            users.setAll(userService.listerTousLesUtilisateurs());
            showStatus(userStatus, "Liste rafraîchie", true);
        } catch (Exception e) {
            showError("Erreur lors du rafraîchissement : " + e.getMessage());
            showStatus(userStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void addUser() {
        if (!currentUser.isAdmin()) return;
        
        String nom = userNameField.getText().trim();
        String email = userEmailField.getText().trim();
        String pwd = userPasswordField.getText();
        User.Role role = userRoleCombo.getValue();
        
        // Validation
        if (nom.isEmpty()) {
            showStatus(userStatus, "Le nom est requis", false);
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            showStatus(userStatus, "Email invalide", false);
            return;
        }
        if (pwd.isEmpty()) {
            showStatus(userStatus, "Le mot de passe est requis", false);
            return;
        }
        if (role == null) {
            showStatus(userStatus, "Le rôle est requis", false);
            return;
        }
        
        try {
            User u = new User(nom, email, pwd, role);
            int id = userService.ajouterUtilisateur(u, pwd);
            showStatus(userStatus, id > 0 ? "Utilisateur ajouté avec succès" : "Échec de l'ajout", id > 0);
            if (id > 0) {
                refreshUsers();
                clearUserForm();
            }
        } catch (IllegalArgumentException e) {
            showStatus(userStatus, e.getMessage(), false);
        } catch (Exception e) {
            showError("Erreur lors de l'ajout : " + e.getMessage());
            showStatus(userStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void updateUser() {
        if (!currentUser.isAdmin()) return;
        
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus(userStatus, "Sélectionnez un utilisateur à modifier", false);
            return;
        }
        
        String nom = userNameField.getText().trim();
        String email = userEmailField.getText().trim();
        User.Role role = userRoleCombo.getValue();
        
        if (nom.isEmpty()) {
            showStatus(userStatus, "Le nom est requis", false);
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            showStatus(userStatus, "Email invalide", false);
            return;
        }
        if (role == null) {
            showStatus(userStatus, "Le rôle est requis", false);
            return;
        }
        
        try {
            selected.setNom(nom);
            selected.setEmail(email);
            selected.setRole(role);
            boolean ok = userService.modifierUtilisateur(selected);
            showStatus(userStatus, ok ? "Utilisateur mis à jour avec succès" : "Échec de la mise à jour", ok);
            if (ok) refreshUsers();
        } catch (IllegalArgumentException e) {
            showStatus(userStatus, e.getMessage(), false);
        } catch (Exception e) {
            showError("Erreur lors de la modification : " + e.getMessage());
            showStatus(userStatus, "Erreur de connexion à la base de données", false);
        }
    }

    @FXML
    private void deleteUser() {
        if (!currentUser.isAdmin()) return;
        
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showStatus(userStatus, "Sélectionnez un utilisateur à supprimer", false);
            return;
        }
        
        if (selected.getId() == currentUser.getId()) {
            showStatus(userStatus, "Vous ne pouvez pas supprimer votre propre compte", false);
            return;
        }
        
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer l'utilisateur ?");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer : " + selected.getNom() + " (" + selected.getEmail() + ") ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                boolean ok = userService.supprimerUtilisateur(selected.getId());
                showStatus(userStatus, ok ? "Utilisateur supprimé avec succès" : "Échec de la suppression", ok);
                if (ok) {
                    refreshUsers();
                    clearUserForm();
                }
            } catch (Exception e) {
                showError("Erreur lors de la suppression : " + e.getMessage());
                showStatus(userStatus, "Erreur de connexion à la base de données", false);
            }
        }
    }

    private void fillUserForm(User u) {
        if (u == null) return;
        userNameField.setText(u.getNom());
        userEmailField.setText(u.getEmail());
        userRoleCombo.setValue(u.getRole());
        userPasswordField.clear(); // on ne remplit pas le mot de passe existant
    }

    private void clearUserForm() {
        userNameField.clear();
        userEmailField.clear();
        userPasswordField.clear();
        userRoleCombo.setValue(null);
    }

    // ==================== UTIL ====================
    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Déconnexion");
        confirm.setHeaderText("Voulez-vous vous déconnecter ?");
        
        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            authService.seDeconnecter();
            onLogout.run();
        }
    }

    private void showStatus(Label label, String text, boolean success) {
        label.setText(text);
        label.setStyle(success ? "-fx-text-fill: green;" : "-fx-text-fill: crimson;");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
