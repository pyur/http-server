package ru.pyur.tst;

import java.util.ArrayList;


public abstract class Permissions {

    protected ArrayList<ModulePerm> modules;  // rename to 'ModulePerm'



    public abstract boolean hasModule(String module_name);

    public abstract boolean hasPermission(String module_name, String permission_name);

    public ArrayList<ModulePerm> getModules() { return modules; }





}