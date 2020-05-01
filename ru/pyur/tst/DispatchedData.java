package ru.pyur.tst;

import java.util.ArrayList;


public class DispatchedData {
    public int code;
    public ArrayList<PStr> options = new ArrayList<>();
    public byte[] payload;

    public DispatchedData(byte[] payload) {
        this.code = 200;
        this.payload = payload;
    }

    public DispatchedData(byte[] payload, ArrayList<PStr> options, int code) {
        this.code = code;
        if (options != null)  this.options = options;
        this.payload = payload;
    }
}