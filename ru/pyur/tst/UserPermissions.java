package ru.pyur.tst;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.FetchedArray;
import ru.pyur.tst.db.FetchedSingle;

import java.sql.Connection;
import java.util.ArrayList;


public class UserPermissions extends Permissions {


    public UserPermissions(Connection connection, int user_id) {
//    public UserPermissions(Connection connection, int user_category) {
        modules = new ArrayList<>();
        //setConnection(connection);

        try {
            DbFetch db_user_module = new FetchUserModule(connection, user_id);
//            DbFetch db_user_module = new FetchUserModule(connection, user_category);
            FetchedArray user_module = db_user_module.fetchArray();

            while (user_module.available()) {
                int module_id = user_module.getInt("id");
                String name = user_module.getString("name");
                String desc = user_module.getString("desc");
                String descb = user_module.getString("descb");
                int pos = user_module.getInt("pos");
                int noauth = user_module.getInt("noauth");
                int auth = user_module.getInt("auth");

                String perm = user_module.getString("perm");

                ModulePerm mp = new ModulePerm(module_id, name, perm, desc, descb, pos, noauth, auth);
                modules.add(mp);
            }

            db_user_module.finish();
        } catch (Exception e) { e.printStackTrace(); }
    }




    private class FetchUserModule extends DbFetch {
        public FetchUserModule(Connection conn, int user_id) {
//        public FetchUserModule(Connection conn, int user_category) {
            setConnection(conn);

            table(new String[]{"user", "ucat_module", "module"});
            rawCol(new String[]{"`module`.`id`", "`module`.`name`", "`module`.`desc`", "`module`.`descb`", "`module`.`pos`", "`module`.`noauth`", "`module`.`auth`",
                    "`ucat_module`.`perm`"});  // "`module`.`perm`",
            where(new String[]{
                    "`user`.`id` = ?",
                    "`user`.`cat` = `ucat_module`.`ucat`",
                    "`ucat_module`.`module` = `module`.`id`"
            });
            wa(user_id);
//            wa(user_category);
            rawOrder("`module`.`pos`, `module`.`desc`");
        }
    }

}