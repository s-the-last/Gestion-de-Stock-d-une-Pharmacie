# Guide d'installation et lancement sur IntelliJ IDEA

## ✅ L'application est prête !

Tous les fichiers nécessaires sont présents. Suivez ces étapes pour lancer l'application dans IntelliJ IDEA.

## Prérequis

1. **IntelliJ IDEA** (version 2020.3 ou supérieure recommandée)
2. **JDK 17+** installé sur votre système
3. **MySQL** installé et démarré
4. **Maven** (inclus dans IntelliJ ou installé séparément)

## Étapes d'installation

### 1. Ouvrir le projet dans IntelliJ

1. Lancez IntelliJ IDEA
2. **File → Open** (ou **File → New → Project from Existing Sources**)
3. Sélectionnez le dossier `Gestion-de-Stock-d-une-Pharmacie`
4. IntelliJ détectera automatiquement le fichier `pom.xml` et vous proposera d'ouvrir comme projet Maven
5. Cliquez sur **"Open as Project"** ou **"Import Maven Project"**

### 2. Configurer le JDK

1. **File → Project Structure** (ou `Ctrl+Alt+Shift+S`)
2. Dans l'onglet **Project** :
   - **Project SDK** : Sélectionnez JDK 17 ou supérieur
   - Si JDK 17 n'est pas listé, cliquez sur **"New..."** et ajoutez le chemin vers votre JDK 17
   - **Project language level** : 17
3. Dans l'onglet **Modules** :
   - Vérifiez que le module utilise le même SDK (17)
4. Cliquez sur **OK**

### 3. Synchroniser Maven

1. IntelliJ devrait automatiquement détecter le `pom.xml` et proposer d'importer les dépendances
2. Si ce n'est pas le cas :
   - Ouvrez le panneau **Maven** (généralement à droite, ou **View → Tool Windows → Maven**)
   - Cliquez sur l'icône **"Reload All Maven Projects"** (flèche circulaire)
3. Attendez que Maven télécharge toutes les dépendances (JavaFX, MySQL Connector)

### 4. Configurer la base de données (optionnel mais recommandé)

1. Créez un fichier `database.properties` à la racine du projet (copiez `database.properties.example`)
2. Modifiez les valeurs selon votre configuration MySQL :
   ```properties
   db.host=localhost
   db.port=3306
   db.database=pharmacy_db
   db.username=root
   db.password=
   ```

### 5. Créer une configuration de run

#### Option A : Configuration Maven (recommandée)

1. **Run → Edit Configurations...**
2. Cliquez sur **"+"** → **Maven**
3. Configurez :
   - **Name** : `Pharmacy App`
   - **Working directory** : Le dossier racine du projet
   - **Command line** : `clean javafx:run`
4. Cliquez sur **OK**

#### Option B : Configuration Application Java

1. **Run → Edit Configurations...**
2. Cliquez sur **"+"** → **Application**
3. Configurez :
   - **Name** : `Pharmacy App`
   - **Main class** : `com.s4m.pharmacy.ui.PharmacyApp`
   - **VM options** : 
     ```
     --module-path "${PATH_TO_FX}" --add-modules javafx.controls,javafx.fxml
     ```
     *(Remplacez `${PATH_TO_FX}` par le chemin vers les modules JavaFX si nécessaire)*
   - **Working directory** : Le dossier racine du projet
4. Cliquez sur **OK**

**Note** : L'option A (Maven) est plus simple car elle gère automatiquement JavaFX.

### 6. Lancer l'application

1. Sélectionnez la configuration **"Pharmacy App"** dans la barre d'outils
2. Cliquez sur le bouton **Run** (▶️) ou appuyez sur `Shift+F10`
3. L'application devrait démarrer et afficher l'écran de connexion

### 7. Se connecter

Utilisez un des comptes par défaut :
- **Admin** : `admin@pharmacy.com` / `admin123`
- **User** : `user@pharmacy.com` / `admin123`

## Résolution des problèmes courants

### Erreur : "JavaFX runtime components are missing"

**Solution** : Utilisez la configuration Maven (Option A ci-dessus) au lieu de la configuration Application.

### Erreur : "Cannot connect to MySQL"

**Vérifications** :
1. MySQL est démarré (vérifiez dans les services Windows ou avec `mysql -u root -p`)
2. Le fichier `database.properties` existe et contient les bons identifiants
3. Le port MySQL est bien 3306 (ou modifiez dans `database.properties`)

### Erreur : "Module not found" ou erreurs d'import

**Solution** :
1. **File → Invalidate Caches / Restart...** → **Invalidate and Restart**
2. Après le redémarrage, **Maven → Reload All Maven Projects**

### Les fichiers FXML ne se chargent pas

**Vérification** :
1. Les fichiers `.fxml` doivent être dans `src/com/s4m/pharmacy/ui/`
2. Dans **File → Project Structure → Modules**, vérifiez que `src` est marqué comme **Sources**

### IntelliJ ne détecte pas Maven

**Solution** :
1. **File → Settings** (ou `Ctrl+Alt+S`)
2. **Build, Execution, Deployment → Build Tools → Maven**
3. Vérifiez que **Maven home directory** pointe vers votre installation Maven
4. Cochez **"Use Maven wrapper"** si disponible

## Astuces IntelliJ

- **Raccourci pour Run** : `Shift+F10`
- **Raccourci pour Debug** : `Shift+F9`
- **Recharger Maven** : Panneau Maven → icône "Reload"
- **Voir les dépendances** : Panneau Maven → Dependencies

## Structure attendue dans IntelliJ

```
Gestion-de-Stock-d-une-Pharmacie
├── .idea/                    (créé automatiquement par IntelliJ)
├── src/
│   └── com/s4m/pharmacy/
│       ├── ui/
│       │   ├── PharmacyApp.java
│       │   ├── LoginView.fxml
│       │   └── DashboardView.fxml
│       └── ...
├── target/                   (créé par Maven)
├── pom.xml
├── database.properties        (à créer)
└── README.md
```

## Vérification finale

Avant de lancer, vérifiez que :
- ✅ Le projet s'ouvre sans erreur dans IntelliJ
- ✅ Maven a téléchargé toutes les dépendances (panneau Maven → Dependencies)
- ✅ Le JDK 17 est configuré
- ✅ MySQL est démarré
- ✅ Le fichier `database.properties` existe (optionnel)

Une fois ces vérifications faites, vous pouvez lancer l'application ! 🚀

