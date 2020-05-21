package ru.pyur.tst;

import java.sql.Connection;
import java.util.ArrayList;


public abstract class Permissions {

    //private Connection connection;

    protected ArrayList<ModulePerm> modules;  // rename to 'ModulePerm'



    public boolean hasModule(String module_name) {
//        if (modules == null)  return false;
//
//        for (ModulePerm module : modules) {
//            if (module.name.equals(module_name))  return true;
//        }

//        return false;
        return (getModule(module_name) != null);
    }



    public boolean hasPermission(String module_name, String permission_name) {
        //if (!hasModule(module_name))  return false;

        ModulePerm module = getModule(module_name);
        if (module == null)  return false;
        if (module.perms == null)  return false;

        for (String perm : module.perms) {
            if (perm.equals(permission_name))  return true;
        }

        return false;
    }



    private ModulePerm getModule(String module_name) {
        if (modules == null)  return null;

        for (ModulePerm module : modules) {
            if (module.name.equals(module_name))  return module;
        }

        return null;
    }



    public ArrayList<ModulePerm> getModules() { return modules; }


    // ---- setters, getters ----

    //public void setConnection(Connection connection) { this.connection = connection; }


}