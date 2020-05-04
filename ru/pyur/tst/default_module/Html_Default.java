package ru.pyur.tst.default_module;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;

import java.io.File;
import java.io.FileInputStream;


public class Html_Default extends HtmlContent {


    public Html_Default(HttpSession session) { initHtml(session); }



    @Override
    public void makeHtml() {

        title("Добро пожаловать");

        heading("Reference Http Java Server");


    }


}