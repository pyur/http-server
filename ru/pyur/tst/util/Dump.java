package ru.pyur.tst.util;

public class Dump {

    private char hex[] = {'0', '1', '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 'A' , 'B' , 'C' , 'D' , 'E' , 'F'};


    public void dumpBinary(byte[] data) {
        System.out.println("length: " + data.length);
        byte[] line = new byte[16];
        int line_used = 0;

        for (int i = 0; i < data.length; i++) {
            //if ((i % 16) == 0)  System.out.println("");

            int val = line[i % 16] = data[i];
            line_used++;

            char c1 = hex[(val & 0xF0) >> 4];
            char c2 = hex[val & 0x0F];

            System.out.print(c1 + "" + c2 + " ");

            if (((i+1) % 16) == 0) {
                System.out.print("  ");
                printLine(line, 16);
                System.out.println("");
                line_used = 0;
            }
        }

        if (line_used > 0) {
            System.out.print("  ");
            printLine(line, line_used);
            System.out.println("");
        }

        System.out.println("");
    }



    private void printLine(byte[] line, int line_used) {
        for (int i = 0; i < line_used; i++) {
            char val = (char)line[i];
            char pr = ' ';
            if (val >= 32 && val <= 127)  pr = val;

            System.out.print(pr);
        }
    }

}