package ru.pyur.tst;

import java.io.InputStream;
import java.util.Arrays;


public class HeaderInputStream extends InputStream {

    private InputStream input_stream;

    protected byte buf[];
    //protected int pos;
    protected int count;

    //private byte[] trail;



    public HeaderInputStream(InputStream is) {
        input_stream = is;
        buf = new byte[8192];
        count = 0;
    }


    @Override
    public int read() {
        // not used
        return 0;
    }

/*
    @Override
    public int read(byte b[], int off, int len) {
        //byte[] bb = new byte[8192];  // in C 16384
        int received_size;

        for (;;) {
            try {
                //received_size = input_stream.read(bb);
                received_size = input_stream.read(buf, count, buf.length - count);
            } catch (Exception e) { e.printStackTrace(); return -1; }

            if (received_size == -1) {
                // connection failed
                //break;
                return -1;
            }

            //else if (received_size == 0) {
            //    System.out.println("received_size is 0");
            //    continue;
            //}

            System.out.println("header input stream. received " + received_size + " bytes.");
            //System.out.println(new String(buf));


//            protocolDispatcher.append(buf, received_size);
//            int result = protocolDispatcher.parseStream();

            //System.arraycopy(bb, 0, buf, count, received_size);


            // ---- search double new line ---- //
            // todo: continue search from last search end pos (minus 4)

            int search_end = buf.length - 3;

            int found = -1;
            for (int i = 0; i < search_end; i++) {
                if (buf[i] != '\r')  continue;

                if (buf[i+1] == '\n' && buf[i+2] == '\r' && buf[i+3] == '\n') {
                    found = i;
                    break;
                }
            }

          if (found != -1) {
              //byte[] header = Arrays.copyOfRange(buf, 0, found);
              trail = Arrays.copyOfRange(buf, found + 4, count);

              if (found > len) {
                  // todo: do something with lost (not fit) data
                  found = len;
              }
              System.arraycopy(buf, 0, b, off, found);

              return found;
          }

        }  // for


        //return 0;
    }



    public byte[] getTrail() {
        //byte[] trail = Arrays.copyOfRange(buf, found + 4, count);
        return trail;
    }
*/


    @Override
    public int read(byte b[], int off, int len) {
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
            //    System.out.println("received_size is 0");
                continue;
            }


            buf[count] = recv[0];
            count++;

            if (count > 3) {
                if (buf[count - 4] == '\r' && buf[count - 3] == '\n' && buf[count - 2] == '\r' && buf[count - 1] == '\n') {
                    int got_length = count - 4;
                    System.arraycopy(buf, 0, b, off, got_length);

                    return got_length;
                }
            }


//            if (found != -1) {
//                //byte[] header = Arrays.copyOfRange(buf, 0, found);
//                trail = Arrays.copyOfRange(buf, found + 4, count);
//
//                if (found > len) {
//                    // todo: do something with lost (not fit) data
//                    found = len;
//                }
//                System.arraycopy(buf, 0, b, off, found);
//
//                return found;
//            }


            if (count > (len - 4)) {  // todo: re-check len calculation!
                //throw exception: double new line not found within range
                return -1;
            }

        }  // for


        //return 0;
    }



    //@Override
    //public long skip(long n) {
    //}



    @Override
    public int available() {
        return 0;
    }


    @Override
    public void close() {}


}