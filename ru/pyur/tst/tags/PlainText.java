package ru.pyur.tst.tags;

public class PlainText extends Tag {


    public PlainText(String text) {
        //tag_name = null;
        plainText = text;
    }


    public PlainText(int number) {
        //tag_name = null;
        plainText = "" + number;
    }

}