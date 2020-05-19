package ru.pyur.tst.tags;


public class InputHidden extends Tag {

    public InputHidden() {
        tag_name = "input";
        addAttribute("type", "hidden");
    }

}