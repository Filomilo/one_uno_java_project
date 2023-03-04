package org.example;

import java.util.Base64;

public class Tools {

    static String bytesToString(byte[] bytes)
    {
        return new String(bytes);
    }

    static byte[] stringToByte(String string)
    {
        byte[] bytes=string.getBytes();
        return bytes;
    }



}
