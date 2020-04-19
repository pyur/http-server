package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;


public class TrimmableByteArrayOutputStream extends ByteArrayOutputStream {

    public void shift(int shift_count) {
        int len = count - shift_count;
        //arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
        System.arraycopy(buf, count, buf, 0, len);
        count -= shift_count;
    }


    public byte[] shiftGet(int shift_count) {
        //Arrays.copyOfRange(byte[] original, int from, int to)
        byte[] shift_get =  Arrays.copyOfRange(buf, 0, shift_count);

        shift(shift_count);

        return shift_get;
    }

}