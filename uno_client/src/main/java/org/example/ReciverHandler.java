package org.example;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketTimeoutException;

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
                System.out.println("recived");
                clientConnectionManager.handleMesseage(messageFormat);
            }
            catch (SocketTimeoutException e)
            {

            }
            catch (IOException | ClassNotFoundException e) {
             e.printStackTrace(

             );
            }
        }
    }

}
