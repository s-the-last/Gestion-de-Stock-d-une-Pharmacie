package com.s4m.pharmacy.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Configuration de la base de données depuis un fichier properties.
 * Si le fichier n'existe pas, utilise les valeurs par défaut.
 */
public class DatabaseConfig {
    
    private static final String CONFIG_FILE = "database.properties";
    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_DB = "pharmacy_db";
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";
    
    private String host;
    private String port;
    private String database;
    private String username;
    private String password;
    
    public DatabaseConfig() {
        loadConfig();
    }
    
    private void loadConfig() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            host = props.getProperty("db.host", DEFAULT_HOST);
            port = props.getProperty("db.port", DEFAULT_PORT);
            database = props.getProperty("db.database", DEFAULT_DB);
            username = props.getProperty("db.username", DEFAULT_USERNAME);
            password = props.getProperty("db.password", DEFAULT_PASSWORD);
        } catch (FileNotFoundException e) {
            // Fichier non trouvé, utiliser les valeurs par défaut (silencieux)
            setDefaults();
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du fichier de configuration : " + e.getMessage());
            setDefaults();
        }
    }
    
    private void setDefaults() {
        host = DEFAULT_HOST;
        port = DEFAULT_PORT;
        database = DEFAULT_DB;
        username = DEFAULT_USERNAME;
        password = DEFAULT_PASSWORD;
    }
    
    public String getUrl() {
        return "jdbc:mysql://" + host + ":" + port + "/" + database + 
               "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    }
    
    public String getUrlWithoutDatabase() {
        return "jdbc:mysql://" + host + ":" + port + 
               "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8";
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getDatabase() {
        return database;
    }
}

