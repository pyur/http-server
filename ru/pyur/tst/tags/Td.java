package ru.pyur.tst.tags;

public class Td extends Tag {

    public Td() {
        common_constructor();
    }

    public Td(String text) {
        common_constructor();
        text(text);
    }

    public Td(int number) {
        common_constructor();
        text("" + number);
    }


    private void common_constructor() {
        tag_name = "td";
        closing = false;
    }

}