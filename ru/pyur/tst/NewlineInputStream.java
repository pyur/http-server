package ru.pyur.tst;

import java.io.IOException;
import java.io.InputStream;


public class NewlineInputStream extends InputStream {

    private InputStream input_stream;

//    protected byte buf[];
    //protected int pos;
//    protected int count;
    protected int max_line;



    public NewlineInputStream(InputStream is, int max_line) {
        input_stream = is;
//        buf = new byte[max_line];
//        count = 0;
        this.max_line = max_line;
    }


    @Override
    public int read() {
        // not used
        return 0;
    }



    @Override
    public int read(byte b[], int off, int len) throws IOException {
        //System.out.println("NewlineInputStream. read ("+off+","+len+")");
        byte[] buf = new byte[max_line];
        int count = 0;

        int received_size;
        byte[] recv = new byte[1];

        for (;;) {
            try {
                received_size = input_stream.read(recv);
            } catch (Exception e) { e.printStackTrace(); return -1; }

            if (received_size == -1) {
                // connection failed
                //break;
                return -1;
            }

            else if (received_size == 0) {
                //System.out.println("received_size is 0");
                continue;
            }


            buf[count] = recv[0];
            count++;

            if (count > 1) {
                if (buf[count - 2] == '\r' && buf[count - 1] == '\n') {
                    int got_length = count - 2;
                    System.arraycopy(buf, 0, b, off, got_length);

                    return got_length;
                }
            }


            if (count > (len - 2)) {  // todo: re-check len calculation!
                throw new IOException("new line not found within range");
                //return -1;
            }

        }  // for


        //return 0;
    }



    //@Override
    //public long skip(long n) {
    //}



    //@Override
    //public int available() {
    //    return 0;
    //}


    //@Override
    //public void close() {}


}