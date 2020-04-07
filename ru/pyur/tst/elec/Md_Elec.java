package ru.pyur.tst.elec;

import ru.pyur.tst.Module;
import ru.pyur.tst.Session;
import java.sql.*;


public class Md_Elec extends Module {

    private static final String DB_URL = "jdbc:mariadb://127.0.0.1/skdev";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1";


    public Md_Elec(Session session) {
        this.session = session;
    }


    public void prepare() {
        b("Hello from Md_Elec!");


        Connection conn = null;
        Statement stmt = null;

        try {
            // -- Open a connection
            System.out.println("Connecting to a selected database...");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected database successfully...");

            // -- Execute a query
            stmt = conn.createStatement();

            String sql = "SELECT `id`, `name`, `cat`, `login`, `dtx`, `idx` FROM `user`";

            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int cat = rs.getInt(3);
                String login = rs.getString(4);
                String dtx = rs.getString(5);
                int idx = rs.getInt(6);

                System.out.println(id + "  " + name + "  " + cat + "  " + login + "  " + dtx + "  " + idx);
            }

        } catch (SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch (Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }// do nothing
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }//end try
        System.out.println("Goodbye!");


    }


}