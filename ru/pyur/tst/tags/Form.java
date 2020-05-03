package ru.pyur.tst.tags;


public class Form extends Tag {


    public Form() {
        tag_name = "form";
    }


    public void setUrl(String url) {
        addAttribute("action", url);
    }


}