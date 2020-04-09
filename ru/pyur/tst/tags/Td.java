package ru.pyur.tst.tags;

public class Td extends Tag {

    public Td() {
        common_constructor();
    }

    public Td(String text) {
        common_constructor();
        this.text.append(text);
    }

    public Td(int number) {
        common_constructor();
        this.text.append(number);
    }


    private void common_constructor() {
        tag_name = "td";
        closing = false;
    }

}