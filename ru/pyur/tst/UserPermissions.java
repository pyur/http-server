package ru.pyur.tst;

public class UserPermissions extends Permissions {


    public UserPermissions(int user_id) {

    }



    @Override
    public boolean hasModule(String module_name) { return false; }


    @Override
    public boolean hasPermission(String module_name, String permission_name) { return false; }


    @Override
    public String[] getModulesList() {
        // return list of modules
        return null;
    }


}