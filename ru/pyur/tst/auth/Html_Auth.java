package ru.pyur.tst.auth;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.tags.Div;
import ru.pyur.tst.tags.PlainText;


public class Html_Auth extends HtmlContent {


    public Html_Auth(HttpSession session) { initHtml(session); }



    @Override
    public void makeContent() {

        title("Авторизация");  // аутентификация

        heading("Авторизация");


        text("<form id=\"save\" action=\"/a/\">");


        Div div_login = new Div();
        div_login.addClass("edit_desc");
        div_login.add(new PlainText("Имя пользователя"));
        tag(div_login);

        text("<input class=\"text\" name=\"login\" type=\"text\" value=\"\" style=\"width: 300px;\" autofocus>");
        //InputText it_login = new InputText();


        Div div_password = new Div();
        div_password.addClass("edit_desc");
        div_password.add(new PlainText("Пароль"));
        tag(div_password);

        text("<input class=\"text\" name=\"password\" type=\"text\" value=\"\" style=\"width: 300px;\">");
        //InputText it_password = new InputText();


        // TODO: submit button


        text("</form>");
    }


}