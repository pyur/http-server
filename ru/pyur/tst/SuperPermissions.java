package ru.pyur.tst;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.FetchedArray;

import java.sql.Connection;
import java.util.ArrayList;

public class SuperPermissions extends Permissions {


    public SuperPermissions(Connection conn) {
        modules = new ArrayList<>();
//        try {
//            Statement stmt = conn.createStatement();
//
//            String query = "SELECT `id`, `name`, `perm`, `desc`, `descb`, `pos`, `noauth`, `auth` FROM `module` ORDER BY `pos`, `desc`";
//
//            ResultSet rs = stmt.executeQuery(query);
//
//            while (rs.next()) {
//                int id = rs.getInt("id");
//                String name = rs.getString("name");
//                String perm = rs.getString("perm");
//                String desc = rs.getString("desc");
//                String descb = rs.getString("descb");
//                int pos = rs.getInt("pos");
//                int noauth = rs.getInt("noauth");
//                int auth = rs.getInt("auth");
//
//                modules.add(new ModulePerm(id, name, perm, desc, descb, pos, noauth, auth));
//            }
//        } catch (Exception e) { e.printStackTrace(); }

        try {
            DbFetch db_all_modules = new FetchAllModules(conn);
            FetchedArray user_module = db_all_modules.fetchArray();

            while (user_module.available()) {
                int module_id = user_module.getInt("id");
                String name = user_module.getString("name");
                String perm = user_module.getString("perm");
                String desc = user_module.getString("desc");
                String descb = user_module.getString("descb");
                int pos = user_module.getInt("pos");
                int noauth = user_module.getInt("noauth");
                int auth = user_module.getInt("auth");

                ModulePerm mp = new ModulePerm(module_id, name, perm, desc, descb, pos, noauth, auth);
                modules.add(mp);
            }

            db_all_modules.finish();
        } catch (Exception e) { e.printStackTrace(); }
    }




    private class FetchAllModules extends DbFetch {
        public FetchAllModules(Connection conn) {
            setConnection(conn);

            table(new String[]{"module"});
            col(new String[]{"id", "perm", "name", "desc", "descb", "pos", "noauth", "auth"});
            order(new String[]{"pos", "desc"});
        }
    }


}