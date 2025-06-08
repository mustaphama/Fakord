package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnectionManager{
    private static final String URL = "jdbc:postgresql://localhost:5432/Fakord";
    private static final String USER = "groupe7";
    private static final String PASSWORD = "projet";
    private static DbConnectionManager instance;
    private DbConnectionManager(){
        try{// Charger le driver PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e){ throw new RuntimeException("Pb de driver", e); }
    }
    public Connection getConnection() throws SQLException {// Obtenir une connexion à la bdd
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    public static DbConnectionManager getInstance(){
        if (instance == null) {
            synchronized (DbConnectionManager.class){
                if (instance == null) {
                    instance = new DbConnectionManager();
                }
            }
        }
        return instance;
    }
}
