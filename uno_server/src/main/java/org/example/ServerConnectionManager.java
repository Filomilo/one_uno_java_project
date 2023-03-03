package org.example;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionManager {


    void testFunction() throws IOException {
        ServerSocket serverSocket = new ServerSocket(25565);
        Socket socket = serverSocket.accept();
        InputStream inStream= socket.getInputStream();
        DataInputStream dinStream= new DataInputStream(inStream);
        while(true) {
            String text = new String(dinStream.readUTF());
            System.out.println(text);
        }


    }
}
