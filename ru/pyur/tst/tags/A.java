package ru.pyur.tst.tags;


public class A extends Tag {


    public A() {
        tag_name = "a";
    }



    public void setHref(ModuleUrl href) {
        //maybe check attribute for presence
        addAttribute("href", href.toString());
    }


}