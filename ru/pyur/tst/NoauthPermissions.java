package ru.pyur.tst;

import ru.pyur.tst.db.DbFetch;
import ru.pyur.tst.db.FetchedArray;

import java.sql.Connection;
import java.util.ArrayList;

public class NoauthPermissions extends Permissions {

    public NoauthPermissions(Connection conn) {
        modules = new ArrayList<>();

        try {
            DbFetch db_all_modules = new FetchNoauthModules(conn);
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




    private class FetchNoauthModules extends DbFetch {
        public FetchNoauthModules(Connection conn) {
            setConnection(conn);

            table(new String[]{"module"});
            col(new String[]{"id", "perm", "name", "desc", "descb", "pos", "noauth", "auth"});
            where("`noauth` = 1");
            rawOrder("`pos`, `desc`");
        }
    }


}