package ru.pyur.tst.tags;

import java.util.ArrayList;


public class Tr extends Tag {

    private ArrayList<TrAction> actions = new ArrayList<>();

    private class TrAction {
        Url url;

        public TrAction(Url url) {
            this.url = url;
        }
    }



    public Tr() {
        tag_name = "tr";
        closing = false;
    }


    public void addAction(Url url) {
        actions.add(new TrAction(url));
    }



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


}