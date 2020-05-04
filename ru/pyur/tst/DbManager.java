package ru.pyur.tst;

import java.sql.Connection;
import java.sql.DriverManager;


public class DbManager {

    // -------- Main DB -------- //

    //private static final String DB_URL = "jdbc:mariadb://127.0.0.1/";
    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/skdev";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1";

    private Connection db_connection;



    // -------- Config DB -------- //

    private static final String CONFIG_URL = "jdbc:sqlite:config.db";

    private Connection db_config;



    public DbManager() {}




    // ---- setters, getters ----------------------------------------------------------------

    public Connection getDb() { return db_connection;}

    public Connection getConfigDb() { return db_config;}




    // ---- maintain ----------------------------------------------------------------

    public void connectDb() {
        if (db_connection == null) {
            try {
                db_connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                //todo: use modern 'DataSource' class
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Statement stmt = null;
        //
        //try {
        //    stmt = db_connection.createStatement();
        //} catch (Exception e) { e.printStackTrace(); }
        //
        //return stmt;
    }



    public void closeDb() {
        if (db_connection != null) {
            try {
                db_connection.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }




    // -------------------------------- Config -------------------------------- //

    public void connectConfigDb() {
        if (db_config == null) {
            try {
                db_config = DriverManager.getConnection(CONFIG_URL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void closeConfig() {
        if (db_config != null) {
            try {
                db_config.close();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }


}