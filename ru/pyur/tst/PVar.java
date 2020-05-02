package ru.pyur.tst;

import java.util.ArrayList;


public class PVar {
    public String key;
    public Var value;

    //public PVar() {
        //this.key = "";
        //this.value = "";
    //}

    public PVar(String key, String value) {
        this.key = key;
        this.value = new Var(value);
    }

    public PVar(String key, int value) {
        this.key = key;
        this.value = new Var(value);
    }



//    public static PVar PairList_FindByKey(ArrayList<PVar> list, String key) {
//        if (list == null || list.size() == 0)  return null;
//
//        //for (int idx = 0; list[idx]; idx++) {
//        for (PVar opt : list) {
//            //if (!strcmp(list[idx]->key, key)) {
//            if (opt.key.equals(key)) {
//                return opt;
//            }
//        }
//
//        return null;
//    }


}