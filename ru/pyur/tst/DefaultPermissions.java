package ru.pyur.tst;

import java.util.ArrayList;

public class DefaultPermissions extends Permissions {

    public DefaultPermissions() {
        modules = new ArrayList<>();

    }



    @Override
    public boolean hasModule(String module_name) { return false; }


    @Override
    public boolean hasPermission(String module_name, String permission_name) { return false; }


//    @Override
//    public String[] getModulesList() {
//        // return list of modules with non-authorized access
//        return null;
//    }


}