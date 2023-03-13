package org.example;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ReciverHandler extends  Thread{

    ClientConnectionManager clientConnectionManager;
    ReciverHandler(ClientConnectionManager clientConnectionManager)
    {
        this.clientConnectionManager=clientConnectionManager;

    }

    boolean shoudldRun=true;

    public boolean isShoudldRun() {
        return shoudldRun;
    }

    public void setShoudldRun(boolean shoudldRun) {
        this.shoudldRun = shoudldRun;
    }

    @Override
    public void run()
    {
        MessageFormat messageFormat;
        while(shoudldRun)
        {
            try {
                messageFormat = this.clientConnectionManager.getMesseage();
              //  System.out.println("recived");
                clientConnectionManager.handleMesseage(messageFormat);
            }
            catch (SocketTimeoutException e)
            {

            }
            catch (SocketException e)
            {
                return  ;
            }
            catch (IOException | ClassNotFoundException e) {
             e.printStackTrace(

             );
            }
        }
    }

}
