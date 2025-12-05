<<<<<<< HEAD
# Pharmacy Management System

Application desktop JavaFX pour la gestion de stock de médicaments avec authentification et contrôle d'accès par rôles.

## Technologies

- **Java 8+**
- **JavaFX** (interface graphique)
- **MySQL** (base de données)
- **JDBC** (connexion base de données)

## Structure du Projet

```
src/com/s4m/pharmacy/
├── db/
│   └── DatabaseConnection.java      # Connexion MySQL + initialisation BD
├── model/
│   ├── Category.java                # Modèle Catégorie
│   ├── Product.java                 # Modèle Produit
│   └── User.java                    # Modèle Utilisateur (avec Role)
├── service/
│   ├── AuthService.java            # Authentification (login/logout)
│   ├── CategoryService.java         # CRUD Catégories
│   ├── ProductService.java          # CRUD Produits
│   └── UserService.java             # CRUD Utilisateurs
├── util/
│   └── PasswordHasher.java          # Hashage SHA-256
├── Main.java                        # Point d'entrée
└── TestPharmacy.java                # Tests des modèles
```



### 2. Configuration Base de Données

Modifier les paramètres dans `DatabaseConnection.java` si nécessaire :

```java
private static final String USERNAME = "root";
private static final String PASSWORD = "";
```

### 3. Initialisation

Lancer `Main.java` pour créer automatiquement :
- La base de données `pharmacy_db`
- Les tables (Categorie, Utilisateur, Produit)
- Les données par défaut

## Utilisation

### Comptes par défaut

- **Admin** : `admin@pharmacy.com` / `admin123`
- **User** : `user@pharmacy.com` / `admin123`

### Services disponibles

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

## Architecture

- **Modèles** : Classes POJO représentant les entités
- **Services** : Logique métier et accès base de données
- **DB** : Gestion connexion MySQL et initialisation

## Base de Données

### Tables

- **Categorie** : id, nom, description
- **Utilisateur** : id, nom, email, mot_de_passe (hashé), role (ADMIN/USER)
- **Produit** : id, nom, description, prix, quantite, date_expiration, id_categorie

### Relations

- Produit → Categorie (Foreign Key)

=======
# Gestion-de-Stock-d-une-Pharmacie
>>>>>>> 39c2c8ffd7d68cb7f032b0921735a5e5b9768066
