package ru.pyur.tst;

public class SuperPermissions extends Permissions {


    @Override
    public boolean hasModule(String module_name) { return true; }


    @Override
    public boolean hasPermission(String module_name, String permission_name) { return true; }


    @Override
    public String[] getModulesList() {
        // return list of all (non hidden) modules
        return null;
    }


}