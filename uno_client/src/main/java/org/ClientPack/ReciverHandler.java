package org.ClientPack;

import org.SharedPack.MessageFormat;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * a class that run thread to recive meseges from server
 */

public class ReciverHandler extends  Thread{

    /**
     * a varaible taht store referance for client connection manger
     */
   private final ClientConnectionManager clientConnectionManager;
    public ReciverHandler(ClientConnectionManager clientConnectionManager)
    {
        this.clientConnectionManager=clientConnectionManager;

    }

    /**
     * a boolean varaibles that stores information about that
     */
    public boolean shoudldRun=true;

    /**
     * setter for shouldRun varaibles
     * @param shoudldRun
     */
    public void setShoudldRun(boolean shoudldRun) {
        this.shoudldRun = shoudldRun;
    }

    /**
     * overwrtieen method that until shouldn't run recives messegaes and than sned them to handle method
     */
    @Override
    public void run()
    {
        MessageFormat messageFormat;
        while(shoudldRun)
        {
            try {
                messageFormat = this.clientConnectionManager.getMesseage();
                System.out.println("recived");
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
