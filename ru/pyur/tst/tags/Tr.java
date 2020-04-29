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
        //this.row_id = row_id;
        addAttribute("data-id", "" + row_id);
    }



//r    public void addAction(Url url) {
//r        actions.add(new TrAction(url));
//r    }




//    @Override
//    public ArrayList<PStr> tagAttributes() {
//        ArrayList<PStr> additional_options = new ArrayList<>();
//        additional_options.add(new PStr("data-id", "" + row_id));
//        return additional_options;
//    }


/*
    @Override
    public String renderNestedPost() {
        //if (!hasHeader)  return "";

        StringBuilder sb = new StringBuilder();

        if (actions.size() != 0) {
            Td actions_td = new Td();
            for (TrAction ta : actions) {
                A act = new A();
                actions_td.add(act);
                //act.addClass("i"+);
                act.addClass("s");
                act.setHref(ta.url);
            }

            sb.append(actions_td.toString());
        }

        return sb.toString();
    }
*/


}