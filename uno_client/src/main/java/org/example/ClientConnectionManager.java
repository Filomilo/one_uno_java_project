package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientConnectionManager {


    void testFunction() throws IOException {
        Socket socket = new Socket("localhost", 25565);
        OutputStream outStream= socket.getOutputStream();
        DataOutputStream doutStream= new DataOutputStream(outStream);
        for(int i=0;i<200;i++) {
            String text =new String("a meesage nr" + i);
            doutStream.writeUTF(text);
            System.out.println("Wrtiirng " + text);
        }
        doutStream.close();
        socket.close();

    }
}
