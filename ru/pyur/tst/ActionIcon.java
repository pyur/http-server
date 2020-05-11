package ru.pyur.tst;


public class ActionIcon {

    public String name;
    //public int x;
    //public int y;
    public int position;


    //public ActionIcon(String name, int x, int y) {
    public ActionIcon(String name, int position) {
        this.name = name;
        //this.x = x;
        //this.y = y;
        this.position = position;
    }


//    public String getSpriteOffset() {
//        return "-" + x + "px -" + y + "px";
//    }


    public String getSpriteOffset() {
        int x = (position % 64) * 16;
        int y = (position / 64) * 16;

        return "-" + x + "px -" + y + "px";
    }

}