package ru.pyur.tst.sample_host.dbedit;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.FetchSingle;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbEditCommon {

    public static Connection getDatabase(Connection config_conn, String host_id) throws Exception {
        DbFetch db_host = new DbFetch(config_conn);
        db_host.table("db");
        db_host.col(new String[]{"host", "port", "login", "password"});
        db_host.where("`id` = ?");
        db_host.wa(host_id);

        FetchSingle fetch = db_host.fetchSingle();

        String host = fetch.getString("host");
        int port = fetch.getInt("port");
        String login = fetch.getString("login");
        String password = fetch.getString("password");

        String db_url = "jdbc:mariadb://" + host + ":" + port + "/";

        Connection conn = DriverManager.getConnection(db_url, login, password);
        return conn;
    }
}