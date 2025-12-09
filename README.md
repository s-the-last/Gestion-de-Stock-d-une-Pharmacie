# Pharmacy Management System

Application desktop JavaFX pour la gestion de stock de médicaments avec authentification et contrôle d'accès par rôles.

## Technologies

- **Java 17** (compatible Java 11+)
- **JavaFX 21** (interface graphique)
- **MySQL 8+** (base de données)
- **Maven** (gestion des dépendances)
- **JDBC** (connexion base de données)

## Structure du Projet

```
src/com/s4m/pharmacy/
├── db/
│   ├── DatabaseConnection.java      # Connexion MySQL + initialisation BD
│   └── DatabaseConfig.java          # Configuration depuis fichier properties
├── model/
│   ├── Category.java                # Modèle Catégorie
│   ├── Product.java                 # Modèle Produit
│   └── User.java                    # Modèle Utilisateur (avec Role)
├── service/
│   ├── AuthService.java             # Authentification (login/logout)
│   ├── CategoryService.java         # CRUD Catégories
│   ├── ProductService.java          # CRUD Produits
│   └── UserService.java             # CRUD Utilisateurs
├── util/
│   └── PasswordHasher.java          # Hashage SHA-256
├── ui/
│   ├── PharmacyApp.java             # Application JavaFX principale
│   ├── LoginController.java         # Contrôleur écran de connexion
│   ├── LoginView.fxml               # Vue de connexion
│   ├── DashboardController.java     # Contrôleur tableau de bord
│   └── DashboardView.fxml           # Vue tableau de bord (onglets)
├── Main.java                        # Point d'entrée console (init BD)
└── TestPharmacy.java                # Tests des modèles
```

## Configuration Base de Données

### Option 1 : Fichier de configuration (recommandé)

1. Copiez `database.properties.example` en `database.properties`
2. Modifiez les valeurs selon votre environnement :

```properties
db.host=localhost
db.port=3306
db.database=pharmacy_db
db.username=root
db.password=votre_mot_de_passe
```

Si le fichier `database.properties` n'existe pas, l'application utilise les valeurs par défaut (localhost, root, mot de passe vide).

### Option 2 : Modification directe du code

Modifiez `DatabaseConnection.java` si vous préférez (non recommandé pour la production).

## Installation et Lancement

### Prérequis

- **JDK 17+** installé
- **MySQL** installé et démarré
- **Maven** installé (ou utilisez le wrapper Maven)

### Étapes

1. **Cloner ou télécharger le projet**

2. **Configurer la base de données** (voir section Configuration ci-dessus)

3. **Lancer l'application JavaFX** :
   ```bash
   mvn clean javafx:run
   ```

   Ou avec le wrapper Maven :
   ```bash
   ./mvnw clean javafx:run
   ```

4. **Se connecter** avec un compte par défaut :
   - **Admin** : `admin@pharmacy.com` / `admin123`
   - **User** : `user@pharmacy.com` / `admin123`

## Fonctionnalités

### Interface Graphique

- ✅ **Écran de connexion** avec validation
- ✅ **Tableau de bord** avec onglets :
  - **Produits** : Liste, ajout, modification, suppression
  - **Catégories** : Liste, ajout, modification, suppression
  - **Utilisateurs** : Visible uniquement pour les admins
- ✅ **Recherche en temps réel** dans toutes les tables
- ✅ **Validation des formulaires** (champs obligatoires, formats)
- ✅ **Confirmations avant suppression**
- ✅ **Gestion d'erreurs** avec messages clairs
- ✅ **Formatage** : prix en euros, dates au format français
- ✅ **Contrôle d'accès** : onglet Utilisateurs masqué pour les non-admins

### Services Backend

**AuthService**
- `seConnecter(email, motDePasse)` : Authentification
- `seDeconnecter()` : Déconnexion
- `getUtilisateurConnecte()` : Utilisateur actuel
- `estAdmin()` : Vérification rôle admin

**CategoryService**
- `ajouterCategorie(category)`
- `modifierCategorie(category)`
- `supprimerCategorie(id)`
- `listerToutesLesCategories()`
- `rechercherCategories(terme)`

**ProductService**
- `ajouterProduit(product)`
- `modifierProduit(product)`
- `supprimerProduit(id)`
- `listerTousLesProduits()`
- `rechercherProduitsParNom(terme)`
- `getProduitsStockBas()` : Produits avec stock < 10

**UserService**
- `ajouterUtilisateur(user, motDePasseClair)`
- `modifierUtilisateur(user)`
- `supprimerUtilisateur(id)`
- `listerTousLesUtilisateurs()`
- `modifierMotDePasse(userId, nouveauMotDePasse)`

## Architecture

- **Modèles** : Classes POJO représentant les entités (Product, Category, User)
- **Services** : Logique métier et accès base de données (CRUD, validation)
- **UI** : Contrôleurs JavaFX et vues FXML
- **DB** : Gestion connexion MySQL et initialisation automatique

## Base de Données

### Tables

- **Categorie** : id, nom, description, created_at, updated_at
- **Utilisateur** : id, nom, email, mot_de_passe (hashé SHA-256), role (ADMIN/USER), created_at, updated_at
- **Produit** : id, nom, description, prix, quantite, date_expiration, id_categorie, created_at, updated_at

### Relations

- Produit → Categorie (Foreign Key avec ON DELETE RESTRICT)

### Initialisation

L'application crée automatiquement :
- La base de données si elle n'existe pas
- Les tables si elles n'existent pas
- Les données par défaut (catégories, utilisateurs, produits de test) si les tables sont vides

## Développement

### Compiler le projet

```bash
mvn clean compile
```

### Créer un JAR exécutable

```bash
mvn clean package
```

Le JAR sera créé dans `target/pharmacy-1.0-SNAPSHOT.jar`

### Structure Maven

Le projet utilise Maven avec :
- JavaFX Maven Plugin pour lancer l'application
- Dépendances : JavaFX Controls, JavaFX FXML, MySQL Connector

## Notes

- Les mots de passe sont hashés avec SHA-256
- Les utilisateurs non-admin ne peuvent pas accéder à la gestion des utilisateurs
- Les catégories liées à des produits ne peuvent pas être supprimées (contrainte de clé étrangère)
- La recherche fonctionne en temps réel sur les noms et descriptions

## Support

Pour toute question ou problème, vérifiez :
1. Que MySQL est démarré
2. Que les identifiants dans `database.properties` sont corrects
3. Que JDK 17+ est installé et configuré
