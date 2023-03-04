package org.example;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class ReciverHandler extends  Thread{

    ClientConnectionManager clientConnectionManager;
    ReciverHandler(ClientConnectionManager clientConnectionManager)
    {
        this.clientConnectionManager=clientConnectionManager;

    }

    @Override
    public void run()
    {
        MessageFormat messageFormat;
        while(true)
        {
            try {
                messageFormat = this.clientConnectionManager.getMesseage();
                if (messageFormat.type == MessageFormat.messegeTypes.MESSAGE) {
                    System.out.println(messageFormat.text);
                }
                if(messageFormat.type == MessageFormat.messegeTypes.DISCONNECT)
                {
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getStackTrace());
            }
        }
    }

}
