package ru.pyur.tst;

import java.io.ByteArrayOutputStream;
import java.net.HttpRetryException;
import java.util.Arrays;


public class Http {

    //private Transport transport;  // todo
    private Session transport;

    private ByteArrayOutputStream stream;
    private ByteArrayOutputStream payload;

    private int state;
    private final int HTTP_STATE_SERVER = 1;

    boolean isHeaderReceived;

    private final int RECV_HEADER_LIMIT = 8192;

    private HttpRequest http_request;
    private HttpResponse http_response;



    public Http(Session transport) {
        this.transport = transport;
        stream = new ByteArrayOutputStream();
        //stream = new DataOutputStream();
        //stream = new FilterOutputStream();
        //stream = new BufferedOutputStream();

        payload = new ByteArrayOutputStream();

        isHeaderReceived = false;
    }



    public void CreateServer() {
        state = HTTP_STATE_SERVER;
    }



    public void append(byte[] data, int size) {
        try {
            stream.write(data, 0, size);
        } catch (Exception e) { e.printStackTrace(); }
    }



    public int processStream() {
        if (!isHeaderReceived)  return processStreamHeader();
        if (isHeaderReceived)  return processStreamPayload();

        return 0;
    }



    public int processStreamHeader() {

        byte[] stm = stream.toByteArray();

        int header_size = searchDoubleNewLine(stm);

        if (header_size < 0) {
            // -- "\r\n\r\n" not found -- //
            //if (stream.size() > RECV_HEADER_LIMIT) {
            if (stm.length > RECV_HEADER_LIMIT) {
                System.out.println("Http. Header limit exceed. Terminate.");
                return -2;
            }

            return 0;
        }


        // -- copy header from stream, and remove it from stream -- //
        byte[] crop = Arrays.copyOfRange(stm, 0, header_size);

        // display received header
        //System.out.println(new String(crop));

        stream = new ByteArrayOutputStream();
        byte[] trail = Arrays.copyOfRange(stm, header_size + 4, stm.length);  // 4 is for "\r\n\r\n"
        //System.out.println("beg: " + (header_size + 4) + ", end: " + stm.length);
        try {
            stream.write(trail);
        } catch (Exception e) { e.printStackTrace(); }


        // ---------------- split first line and options ---------------- //

      //  http->lsLines = String_ExplodeStr("\r\n", szHeader);
      //
      //  size_t lines_count = StringList_Size(http->lsLines);
      //  if (!lines_count) {
      //      DebugError("Invalid header. 0 lines.");
      //      return -5;
      //  }
        http_request = new HttpRequest(crop);

        isHeaderReceived = true;

        return 1;
    }



    private int searchDoubleNewLine(byte[] stm) {
        int search_end = stm.length - 3;

        for (int i = 0; i < search_end; i++) {
            if (stm[i] != '\r')  continue;

            if (stm[i+1] == '\n' && stm[i+2] == '\r' && stm[i+3] == '\n')  return i;
        }

        return -1;
    }



    public int processStreamPayload() {

        return 0;
    }



}