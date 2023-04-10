package org.example;

public class ChatMesseage {

    private String nick;
    private String messeage;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getMesseage() {
        return messeage;
    }

    public void setMesseage(String messeage) {
        this.messeage = messeage;
    }

    public ChatMesseage(String nick, String messeage) {
        this.nick = nick;
        this.messeage = messeage;
    }

    @Override
    public String toString() {
        return getNick() + " >> " + getMesseage();
    }
}
