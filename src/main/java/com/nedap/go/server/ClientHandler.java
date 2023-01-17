package com.nedap.go.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private Socket socket ;
    private Server server ;
    private PrintWriter pW ;
    private BufferedReader bR ;


    private static final String HELLO = "HELLO";
    private static final String USERNAME = "USERNAME";
    private static final String QUEUE = "QUEUE";
    private static final String PASS = "PASS";
    private static final String MOVE = "MOVE";
    private static final String QUIT = "QUIT" ;





    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            pW = new PrintWriter(socket.getOutputStream());
            bR = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        while (!socket.isClosed()) {

            try {
                String line;
                line= bR.readLine();

                String[] splittedLine = line.split("~");
                String command = splittedLine[0].toUpperCase() ;
                 switch (command) {
                     case HELLO:
                     case USERNAME:
                     case QUEUE:
                     case PASS:
                     case MOVE:
                     case QUIT:
                     default:
                         System.out.println("Does not understand the import");
                 }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }



    }


    public void close(){
        try {
            socket.close();
            bR.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }




}
