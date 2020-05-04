package ru.pyur.tst.auth;

import ru.pyur.tst.HtmlContent;
import ru.pyur.tst.HttpSession;
import ru.pyur.tst.tags.*;


public class Html_Auth extends HtmlContent {


    public Html_Auth(HttpSession session) { initHtml(session); }



    @Override
    public void makeHtml() {

        title("Авторизация");  // аутентификация

        heading("Авторизация");


        Div container = new Div();
        container.width(200);
        container.addStyle("margin", "50px auto 0");
        tag(container);

//x        text("<form id=\"save\" action=\"/a/\">");
        Form form = new Form("form");
        form.setUrl("/a/" + getModule() + "/");
        container.add(form);


        Div div_login = new Div();
        div_login.addClass("edit_desc");
        div_login.add(new PlainText("Имя пользователя"));
        form.add(div_login);

//x        text("<input class=\"text\" name=\"login\" type=\"text\" value=\"\" style=\"width: 300px;\" autofocus>");
        InputText it_login = new InputText();
        it_login.addClass("text");
        it_login.setName("login");
        //it_login.setValue("");
        it_login.width(200);
        it_login.setAutofocus();
        form.add(it_login);


        Div div_password = new Div();
        div_password.addClass("edit_desc");
        div_password.add(new PlainText("Пароль"));
        form.add(div_password);

//x        text("<input class=\"text\" name=\"password\" type=\"text\" value=\"\" style=\"width: 300px;\">");
        InputText it_password = new InputText();
        it_password.addClass("text");
        it_password.setName("password");
        it_password.width(200);
        form.add(it_password);


        InputButton ib_submit = new InputButton();
        //ib_submit.addClass("text");
        ib_submit.setName("submit");
        ib_submit.setValue("Войти");
        ib_submit.addStyle("display", "block");
        ib_submit.addStyle("margin", "20px auto 0");
        form.add(ib_submit);


//x        text("</form>");
    }


}