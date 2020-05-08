package ru.pyur.tst;

import java.util.Arrays;

public abstract class ExpandableByteArray {

    protected byte buf[];

    // number of valid bytes in the buffer.
    protected int count;

    private int grow_size;


    protected void init() {
        buf = new byte[grow_size];
        count = 0;
        grow_size = 1024;
    }




    protected void append(byte[] data) {
        int data_length = data.length;
        ensureCapacity(data_length);
        System.arraycopy(data, 0, buf, count, data_length);
        count += data_length;
    }



    protected void ensureCapacity(int required_space) {
        int remaining_space = buf.length - count;

        if (remaining_space < required_space) {
            int new_space = (((count + required_space) / grow_size) + 1) * grow_size;
            buf = Arrays.copyOf(buf, new_space);
            //count = new_space;
        }
    }



    protected void truncate() {
        buf = Arrays.copyOf(buf, count);
    }



    protected void truncate(int size) {
        buf = Arrays.copyOf(buf, size);
    }

}