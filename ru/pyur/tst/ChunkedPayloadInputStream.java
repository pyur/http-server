package ru.pyur.tst;

import com.sun.org.apache.bcel.internal.generic.NEW;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


public class ChunkedPayloadInputStream extends InputStream {

    private InputStream input_stream;

    //protected byte buf[];
    //protected int pos;
    //protected int count;

    private int chunk_left;
    private boolean streamEnded = false;



    public ChunkedPayloadInputStream(InputStream is) {
        input_stream = is;
        chunk_left = 0;
    }




    @Override
    public int read() {
        // not used
        return 0;
    }


    @Override
    public int read(byte b[], int off, int len) throws IOException {
        if (streamEnded)  return -1;

        //System.out.println("ChunkedPayloadInputStream. read ("+off+","+len+")");
        int read_off = off;
        int read_len = len;
        int readed = 0;


        for(;;) {

            if (chunk_left == 0) {
                //System.out.println("ChunkedPayloadInputStream. chunk_left == 0");
                byte[] chunk_size = new byte[10];
                NewlineInputStream nis = new NewlineInputStream(input_stream, 10);

                readed = nis.read(chunk_size);
                //System.out.println("ChunkedPayloadInputStream. readed: " + readed);

                if (readed == -1) return -1;

                //not need skip \r\n

                //System.out.println("ChunkedPayloadInputStream. hex: " + new String(chunk_size));
                chunk_left = byte_HexToInt(chunk_size);
                //System.out.println("ChunkedPayloadInputStream. dec: " + chunk_left);

                if (chunk_left == 0) {
                    // stream ends
                    //System.out.println("ChunkedPayloadInputStream. stream ends. ("+(len - read_len)+")");
                    long skipped = input_stream.skip(2);
                    streamEnded = true;
                    return len - read_len;
                }
            }

            int to_read = (chunk_left < read_len) ? chunk_left : read_len;

            readed = input_stream.read(b, read_off, to_read);
            //System.out.println("ChunkedPayloadInputStream. input_stream.read ("+read_off+", "+to_read+"). readed: " + readed);
            //byte[] dump = Arrays.copyOfRange(b, read_off, read_off+to_read);
            //System.out.println("[" + new String(dump) + "]");

            if (readed == -1) {
                // maybe return remainings
                // return len - read_len;
                throw new IOException("unexpected chunk end.");
            }

            chunk_left -= readed;

            if (chunk_left == 0) {
                long skipped = input_stream.skip(2);
                // todo: check skipped
            }

            read_off += readed;
            read_len -= readed;

            if (read_len == 0)  return len;
        }  // for

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




    private int byte_HexToInt(byte[] p) {
        //Str p = string + strlen(string) - 1;
        int i = 0;  // p.length - 1;
        int nibble;
        int number = 0;
//        Bin val = (Bin)&number;
        //long val = 0;
        int val = 0;

        int limit = 4;
        while(i < p.length && (limit--) >= 0) {  // todo: not sure about '>='

//            // -- first nibble -- //
            nibble = p[i];

            if (nibble >= '0' && nibble <= '9')  nibble = nibble - '0';
            else if (nibble >= 'a' && nibble <= 'f')  nibble = nibble - 'a' + 10;
            else if (nibble >= 'A' && nibble <= 'F')  nibble = nibble - 'A' + 10;
            else  break;
            //*val = nibble;
            val <<= 4;
            val |= nibble & 0xF;
            //p--;
            i++;

            // -- second nibble -- //
//            if (!*p)  break;
//            nibble = *p;
//
//            if (nibble >= '0' && nibble <= '9')  nibble = nibble - '0';
//            else if (nibble >= 'a' && nibble <= 'f')  nibble = nibble - 'a' + 10;
//            else if (nibble >= 'A' && nibble <= 'F')  nibble = nibble - 'A' + 10;
//            else  break;
//
//            *val |= (nibble & 0xF) << 4;  // is `& 0xF` necessary?
//            p--;
//
//            val++;
        }

        //number = _byteswap_ulong(number);

        return val;  // number;
    }


}