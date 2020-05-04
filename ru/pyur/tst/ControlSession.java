package ru.pyur.tst;

public interface ControlSession {
    void setCode(int code);
    void addOption(String name, String value);
    void setCookie(String name, String value, int expires, String path);
}