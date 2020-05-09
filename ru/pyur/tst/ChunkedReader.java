package ru.pyur.tst;

import ru.pyur.tst.util.ExpandableByteArray;

import java.io.InputStream;


public class ChunkedReader extends ExpandableByteArray {

    private InputStream input_stream;



    public ChunkedReader(InputStream is) {
        input_stream = is;
    }




    public byte[] read() throws Exception {
        reset();

        for(;;) {

            NewlineReader nr = new NewlineReader(input_stream);

            byte[] chunk_size_b = nr.read();

            //not need skip \r\n

            //System.out.println("ChunkedReader. hex: " + new String(chunk_size));
            int chunk_size = byte_HexToInt(chunk_size_b);
            //System.out.println("ChunkedReader. dec: " + chunk_size);

            if (chunk_size == 0) {
                // stream ends
                //System.out.println("ChunkedReader. stream ends. ("+(len - read_len)+")");
                long skipped = input_stream.skip(2);
                if (skipped < 2)  throw new Exception("unexpected stream end.");

                truncate();
                return buf;
            }


            byte[] chunk_buf = new byte[chunk_size];

            int readed = input_stream.read(chunk_buf);
            if (readed < chunk_buf.length)  throw new Exception("unexpected stream end.");

            append(chunk_buf);

            long skipped = input_stream.skip(2);
            if (skipped < 2)  throw new Exception("unexpected stream end.");
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