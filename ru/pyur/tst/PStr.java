package ru.pyur.tst;

import java.util.ArrayList;


public class PStr {
    public String key;
    public String value;

    public PStr() {
        this.key = "";
        this.value = "";
    }

    public PStr(String key, String value) {
        this.key = key;
        this.value = value;
    }



    public static PStr PairList_FindByKey(ArrayList<PStr> list, String key) {
        if (list == null || list.size() == 0)  return null;

        //for (int idx = 0; list[idx]; idx++) {
        for (PStr opt : list) {
            //if (!strcmp(list[idx]->key, key)) {
            if (opt.key.equals(key)) {
                return opt;
            }
        }

        return null;
    }


}