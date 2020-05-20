package ru.pyur.tst;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class SuperPermissions extends Permissions {


    public SuperPermissions(Connection conn) {
        modules = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();

            String query = "SELECT `id`, `name`, `perm`, `desc`, `descb`, `pos`, `noauth`, `auth` FROM `module` ORDER BY `pos`, `desc`";

            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String perm = rs.getString("perm");
                String desc = rs.getString("desc");
                String descb = rs.getString("descb");
                int pos = rs.getInt("pos");
                int noauth = rs.getInt("noauth");
                int auth = rs.getInt("auth");

                modules.add(new ModulePerm(id, name, perm, desc, descb, pos, noauth, auth));
            }
        } catch (Exception e) { e.printStackTrace(); }
    }


    @Override
    public boolean hasModule(String module_name) { return true; }


    @Override
    public boolean hasPermission(String module_name, String permission_name) { return true; }


//    @Override
//    public String[] getModulesList() {
//        // return list of all (non hidden) modules
//        return null;
//    }


}