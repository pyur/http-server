package ru.pyur.tst;

import java.io.InputStream;


public class ChunkedReader extends ExpandableByteArray {

    private InputStream input_stream;

//x    private int chunk_size;
//x    private boolean streamEnded = false;



    public ChunkedReader(InputStream is) {
        input_stream = is;
//x        chunk_size = 0;
    }





    public byte[] read() throws Exception {
        init();
        //if (streamEnded)  return ;

        //System.out.println("ChunkedReader. read ("+off+","+len+")");
//x        int chunk_size = 0;
//x        int read_off = off;
//x        int read_len = len;


        for(;;) {

//x            if (chunk_size == 0) {
                //System.out.println("ChunkedReader. chunk_size == 0");
//x                byte[] chunk_size = new byte[10];
                NewlineReader nr = new NewlineReader(input_stream);

//x                readed = nr.read(chunk_size);
                byte[] chunk_size_b = nr.read();
                //System.out.println("ChunkedReader. readed: " + readed);

//x                if (readed == -1) return -1;

                //not need skip \r\n

                //System.out.println("ChunkedReader. hex: " + new String(chunk_size));
            int chunk_size = byte_HexToInt(chunk_size_b);
                //System.out.println("ChunkedReader. dec: " + chunk_size);

                if (chunk_size == 0) {
                    // stream ends
                    //System.out.println("ChunkedReader. stream ends. ("+(len - read_len)+")");
                    long skipped = input_stream.skip(2);
                    if (skipped < 2)  throw new Exception("unexpected stream end.");
//x                    streamEnded = true;
//x                    return len - read_len;
                    truncate();
                    return buf;
                }
//x            }

//x            int to_read = (chunk_size < read_len) ? chunk_size : read_len;

//x            readed = input_stream.read(b, read_off, to_read);
            byte[] chunk_buf = new byte[chunk_size];
            //int read_off = 0;

            //for(;;) {  // maybe it redundant.
            //    int readed = input_stream.read(chunk_buf, read_off, chunk_buf.length - read_off);
            //    read_off += readed;
            //    if (read_off >= chunk_buf.length)  break;
            //}
            int readed = input_stream.read(chunk_buf);
            if (readed < chunk_buf.length)  throw new Exception("unexpected stream end.");

            //System.out.println("ChunkedReader. input_stream.read ("+read_off+", "+to_read+"). readed: " + readed);
            //byte[] dump = Arrays.copyOfRange(b, read_off, read_off+to_read);
            //System.out.println("[" + new String(dump) + "]");

            append(chunk_buf);

            long skipped = input_stream.skip(2);
            if (skipped < 2)  throw new Exception("unexpected stream end.");

//x            if (readed == -1) {
                // maybe return remainings
                // return len - read_len;
//x                throw new IOException("unexpected chunk end.");
//x            }

//x            chunk_size -= readed;
//x            chunk_size -= readed.length;

//x            if (chunk_size == 0) {
//x                long skipped = input_stream.skip(2);
//x                // todo: check skipped
//x            }

//x            read_off += readed;
//x            read_len -= readed;

//x            if (read_len == 0)  return len;
        }  // for

    }




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