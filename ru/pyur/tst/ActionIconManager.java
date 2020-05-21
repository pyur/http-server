package ru.pyur.tst;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.FetchedArray;

import java.sql.Connection;
import java.util.ArrayList;


public class ActionIconManager {

    private Connection conn_config;

    private ArrayList<ActionIcon> action_icons;




    public ActionIconManager() {}

    public ActionIconManager(Connection conn) { conn_config = conn; }




    // --------------------------------  -------------------------------- //

    public ActionIcon getActionIcon(String name) throws Exception {
        if (action_icons == null) {
            //try {
                fetchActionIcons();
            //} catch (Exception e) { e.printStackTrace(); return null; }
        }

        for (ActionIcon action_icon : action_icons) {
            if (name.equals(action_icon.name)) {
                return action_icon;
            }
        }

        //return null;
        throw new Exception("requested action icon \"" + name + "\" not found");
    }



    public void fetchActionIcons() throws Exception {
//x        Connection conn = DbManager.getConfigDb();
        if (conn_config == null)  throw new Exception("can't fetch, no config_db specified.");

        action_icons = new ArrayList<>();

        DbFetch db_ai = new DbFetch(conn_config);
        db_ai.table("action_icon");
        db_ai.col(new String[]{"name", "position"});

        FetchedArray fetch = db_ai.fetchArray();

        while (fetch.available()) {
            String name = fetch.getString("name");
            int position = fetch.getInt("position");

            //int x = (pos % 16) * 16;
            //int y = (pos / 16) * 16;

            //ActionIcon action_icon = new ActionIcon(name, x, y);
            action_icons.add(new ActionIcon(name, position));
        }

    }





    }