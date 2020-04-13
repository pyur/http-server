package ru.pyur.tst;

import java.util.ArrayList;


public class DispatchedData {
    public byte[] payload;
    public ArrayList<PStr> options = new ArrayList<>();

    public DispatchedData(byte[] payload) {
        this.payload = payload;
    }

    public DispatchedData(byte[] payload, ArrayList<PStr> options) {
        this.payload = payload;
        if (options != null)  this.options = options;
    }
}