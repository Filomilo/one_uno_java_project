package org.ServerPack;

/**
 * a thread to run server aopp
 */
public class ServerRunThread extends  Thread{
    /**
     * caraibel to store refeeracne to main server app object
     */
    ServerApp serverApp;

    /**
     * constructor that get referacne to main server app class
     * @param serverApp
     */
    public ServerRunThread(ServerApp serverApp) {
        this.serverApp = serverApp;
    }

    /**
     * overwritten run method of thread class that starts server
     */
    @Override
    public void run() {
        serverApp.startServer();
    }


}
