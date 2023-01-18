package com.nedap.go.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter pW;
    private BufferedReader bR;

    private String myUsername;


    private static final String HELLO = "HELLO";
    private static final String USERNAME = "USERNAME";
    private static final String QUEUE = "QUEUE";
    private static final String PASS = "PASS";
    private static final String MOVE = "MOVE";
    private static final String QUIT = "QUIT";


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
                line = bR.readLine();

                String[] splittedLine = line.split("~");
                String command = splittedLine[0].toUpperCase();
                switch (command) {
                    case HELLO:
                        hello();
                        break ;
                    case USERNAME:
                        username(splittedLine[1]);
                        break ;
                    case QUEUE:
                        queue();
                        break ;
                    case PASS:
                        pass() ;
                        break ;
                    case MOVE:
                        move(Integer.parseInt(splittedLine[1]),Integer.parseInt(splittedLine[2])) ;
                        break ;
                    case QUIT:
                        quit();
                        break ;
                    default:
                        System.out.println("Does not understand the import");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }


    public void close() {
        try {
            socket.close();
            bR.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private void queue() {
        server.addToPlayerQueue(this);

    }

    private void username(String username){
        if(!server.usernames.contains(username)) {
            this.myUsername = username;
            server.usernames.add(username) ;
            pW.println("JOINED");
            pW.flush();
        }
        else{
            pW.println("USERNAMETAKEN");
            pW.flush();
        }

    }

    private void hello(){
        pW.println("WELCOME~This is the server van Ilana");
        pW.flush();
    }

    private void pass(){
        // hier iets wat doorstuurd naar het spell
        String s = myUsername + "~" + "pass" ;
        server.messageSender(s); // send message to all the clients in the game .
    }
    private void move(int row, int col){
        // hier moet nog wel iets dat die daadwerkelijk leest
        String s = ""+row+"~"+col ;
        server.messageSender(s);
    }
    private void quit(){
        close() ;
    }


    public void sendMessage(String message){
        pW.println(message);
        pW.flush();
    }

}
