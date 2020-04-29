package ru.pyur.tst.tags;

import ru.pyur.tst.PStr;

import java.util.ArrayList;


public class Tr extends Tag {

//    private int row_id;
//r    private ArrayList<TrAction> actions = new ArrayList<>();

//    private class TrAction {
//        Url url;
//
//        public TrAction(Url url) {
//            this.url = url;
//        }
//    }



    public Tr() {
        tag_name = "tr";
        closing = false;
    }


    public Tr(int row_id) {
        tag_name = "tr";
        closing = false;
        addAttribute("data-id", "" + row_id);
    }


    public Tr(String row_id) {
        tag_name = "tr";
        closing = false;
        addAttribute("data-id", row_id);
    }




//    @Override
//    public ArrayList<PStr> tagAttributes() {
//        ArrayList<PStr> additional_options = new ArrayList<>();
//        additional_options.add(new PStr("data-id", "" + row_id));
//        return additional_options;
//    }


}