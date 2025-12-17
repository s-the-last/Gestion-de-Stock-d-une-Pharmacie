# Pharmacy Management System

Application desktop JavaFX pour la gestion du stock de médicaments avec authentification et contrôle d’accès par rôles.

---

## 🎯 Présentation du projet

Le **Pharmacy Management System** est une application desktop développée en Java permettant de gérer efficacement le stock d’une pharmacie. Elle propose une interface graphique intuitive, une authentification sécurisée et une gestion des droits selon le rôle de l’utilisateur (Administrateur ou Utilisateur).

Les principales fonctionnalités incluent la gestion des produits, des catégories et des utilisateurs, ainsi qu’un contrôle strict de l’accès aux fonctionnalités sensibles.

---

## 🛠️ Technologies utilisées

* **Java 17**
* **JavaFX 21** (interface graphique)
* **MySQL 8+** (base de données)
* **Maven** (gestion des dépendances)
* **JDBC** (connexion à la base de données)
* **IntelliJ IDEA** (environnement de développement)

---

## 📁 Structure du projet

```
Gestion-de-Stock-d-une-Pharmacie
├── src/
│   └── com/s4m/pharmacy/
│       ├── db/            # Connexion et configuration MySQL
│       ├── model/         # Modèles (Product, Category, User)
│       ├── service/       # Logique métier (CRUD, authentification)
│       ├── util/          # Outils (hashage mot de passe)
│       └── ui/            # JavaFX (contrôleurs + vues FXML)
├── target/                # Généré par Maven
├── pom.xml                # Configuration Maven
├── database.properties    # Configuration base de données (optionnel)
└── README.md
```

---

## 🧩 Fonctionnalités principales

### 🔐 Authentification

* Connexion sécurisée par e-mail et mot de passe
* Mots de passe hashés avec **SHA-256**
* Gestion de session utilisateur

### 📦 Gestion du stock

* Ajouter, modifier et supprimer des produits
* Gestion des quantités et dates d’expiration
* Détection des produits à stock faible

### 🗂️ Gestion des catégories

* CRUD des catégories
* Interdiction de supprimer une catégorie liée à un produit

### 👥 Gestion des utilisateurs

* Réservée aux administrateurs
* Création, modification et suppression de comptes
* Gestion des rôles (ADMIN / USER)

### 🔎 Autres fonctionnalités

* Recherche en temps réel
* Validation des formulaires
* Messages d’erreur clairs
* Confirmation avant suppression

---

## 🗄️ Base de données

### Tables principales

* **Utilisateur** : id, nom, email, mot_de_passe (hashé), rôle, dates
* **Categorie** : id, nom, description, dates
* **Produit** : id, nom, description, prix, quantité, date d’expiration, catégorie

### Relations

* Un produit appartient à une catégorie
* Contrainte de clé étrangère avec restriction à la suppression

### Initialisation automatique

Au lancement, l’application :

* Crée la base de données si elle n’existe pas
* Crée les tables nécessaires
* Insère des données de test (utilisateurs, catégories, produits)

---

## ⚙️ Configuration de la base de données

### Méthode recommandée

1. Copier le fichier `database.properties.example`
2. Le renommer en `database.properties`
3. Adapter les valeurs :

```properties
db.host=localhost
db.port=3306
db.database=pharmacy_db
db.username=root
db.password=
```

Si le fichier n’existe pas, des valeurs par défaut sont utilisées.

---

## ▶️ Installation et lancement (IntelliJ IDEA)

### Prérequis

* IntelliJ IDEA (2020.3 ou plus récent)
* JDK 17 ou supérieur
* MySQL démarré
* Maven (intégré à IntelliJ)

### Étapes

1. Ouvrir IntelliJ IDEA
2. **File → Open** et sélectionner le dossier du projet
3. Importer le projet comme **Maven Project**
4. Configurer le **JDK 17** :

    * File → Project Structure → Project SDK
5. Synchroniser Maven (Reload Maven Project)

### Lancer l’application (recommandé)

Via Maven :

```bash
mvn clean javafx:run
```

Ou en utilisant la configuration Run Maven dans IntelliJ :

* Command line : `clean javafx:run`

---

## 👤 Comptes de test

* **Administrateur** : `admin@pharmacy.com` / `admin123`
* **Utilisateur** : `user@pharmacy.com` / `admin123`

---

## 🧪 Tests

Des scénarios de tests fonctionnels ont été réalisés afin de vérifier :

* L’authentification
* La gestion du stock
* Le contrôle d’accès par rôles
* La validation des données

Les résultats montrent un fonctionnement conforme aux attentes.

---

## 📦 Compilation

Compiler le projet :

```bash
mvn clean compile
```

Créer un JAR exécutable :

```bash
mvn clean package
```

Le fichier JAR est généré dans le dossier `target/`.

---

## ✅ Conclusion

Ce projet propose une solution complète et sécurisée pour la gestion du stock d’une pharmacie. L’architecture claire, l’utilisation de JavaFX et de Maven, ainsi que la gestion des rôles permettent une application fiable, évolutive et adaptée à un contexte professionnel.
