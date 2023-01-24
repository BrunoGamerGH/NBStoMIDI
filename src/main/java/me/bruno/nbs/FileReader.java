package me.bruno.nbs;

import java.io.DataInputStream;
import java.io.IOException;

public class FileReader {

    public static short readShort(DataInputStream dis) throws IOException {
        int val1 = dis.readUnsignedByte();
        int val2 = dis.readUnsignedByte();
        return (short) (val1 + (val2 << 8));
    }

    public static int readInt(DataInputStream dis) throws IOException {
        int val1 = dis.readUnsignedByte();
        int val2 = dis.readUnsignedByte();
        int val3 = dis.readUnsignedByte();
        int val4 = dis.readUnsignedByte();
        return (val1 + (val2 << 8) + (val3 << 16) + (val4 << 24));
    }

    public static String readString(DataInputStream dis) throws IOException {
        int lenght = readInt(dis);
        String s = "";
        for (int i = 0; i < lenght; i++) {
            char c = (char) dis.readByte();
            if (c == (char) 0x0D) {
                c = ' ';
            }
            s += c;
        }
        return s;
    }
}
