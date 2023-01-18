package com.nedap.go.client;


import java.net.InetAddress;

public class Client implements Runnable {

    private int port ;
    private InetAddress address ;

    private static final String WELCOME = "WELCOME";
    private static final String USERNAMETAKEN = "USERNAMETAKEN";
    private static final String JOINED = "JOINED";
    private static final String NEWGAME = "NEWGAME";
    private static final String MOVE = "MOVE";
    private static final String GAMEOVER = "GAMEOVER";

    public Client(InetAddress address, int port) {
        this.address=address;
        this.port=port;

    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {

    }
    public void close(){}



}
