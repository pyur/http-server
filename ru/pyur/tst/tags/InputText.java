package ru.pyur.tst.tags;


public class InputText extends Tag {


    public InputText() {
        tag_name = "input";
        addAttribute("type", "text");
    }


    public void setValue(String value) {
        addAttribute("value", value);
    }


    public void setAutofocus() {
        addAttribute("autofocus", null);
    }


}