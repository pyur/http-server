package ru.pyur.tst;

public class DefaultPermissions extends Permissions {


    @Override
    public boolean hasModule(String module_name) { return false; }


    @Override
    public boolean hasPermission(String module_name, String permission_name) { return false; }


    @Override
    public String[] getModulesList() {
        // return list of modules with non-authorized access
        return null;
    }


}