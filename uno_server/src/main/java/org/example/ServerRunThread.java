package org.example;

public class ServerRunThread extends  Thread{
    ServerApp serverApp;

    public ServerRunThread(ServerApp serverApp) {
        this.serverApp = serverApp;
    }

    @Override
    public void run() {
        serverApp.startServer();
    }


}
