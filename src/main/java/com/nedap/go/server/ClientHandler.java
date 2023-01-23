package com.nedap.go.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

public class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter pW;
    private BufferedReader bR;

    private String myUsername;
    private int[] currentMove;
    private boolean valueRead ;  ;


    private static final String HELLO = "HELLO";
    private static final String USERNAME = "USERNAME";
    private static final String QUEUE = "QUEUE";
    private static final String PASS = "PASS";
    private static final String MOVE = "MOVE";
    private static final String QUIT = "QUIT";

    /**
     * constructor
     *
     * @param socket
     * @param server
     */
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
     * Reads the input and depending on this input with the switch command goes to the right output
     * <p>
     * hier mist nog een boolean die ervoor zorgt dat je in bepaalde situaties dingen niet kan (zoals een spel proberen te starten als je nog geen username is gegeven)
     * De functies die dezelfde naam hebben als de input zijn nog niet af en of niet correct
     */
    @Override
    public void run() {
        while (!socket.isClosed()) {

            try {
                String line;

                line = bR.readLine();
                if (line == null) {
                    break;
                }
                String[] splittedLine = line.split("~");
                String command = splittedLine[0].toUpperCase();
                System.out.println(command); // MOET OP HET EINDE NOG WEG MAAR VOOR NU HOUDEN
                switch (command) {
                    case HELLO:
                        hello();
                        break;
                    case USERNAME:
                        username(splittedLine[1]);
                        break;
                    case QUEUE:
                        queue();
                        break;
                    case PASS:
                        pass();
                        break;
                    case MOVE:
                        move(Integer.parseInt(splittedLine[1]), Integer.parseInt(splittedLine[2]));
                        break;
                    case QUIT:
                        quit();
                        break;
                    default:
                        System.out.println("Does not understand the import");
                }

            } catch (IOException e) {
                System.out.println("Connection with a client is lost");
                break;
            }
        }

        close();
    }


    public void close() {
        try {
            bR.close();
            server.usernames.remove(myUsername);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private void queue() {
        server.addOrRemovePlayerFromQueue(this);

    }

    private void username(String username) {
        if (!server.usernames.contains(username)) {
            this.myUsername = username;
            server.usernames.add(username);
            sendMessage("JOINED");

        } else {
            sendMessage("USERNAMETAKEN~username is already taken please use another username");
        }

    }

    private void hello() {
        sendMessage("WELCOME~This is the server van Ilana");
    }

    private void pass() {
        setMove(null);
    }

    private void move(int row, int col) {
        int[] value = new int[2] ;
        value[0] = row ;
        value[1] = col  ;
        setMove(value);

    }

    private void quit() {
        close();
    }


    public void sendMessage(String message) {
        pW.println(message);
        pW.flush();
    }

    public String getMyUsername() {
        return myUsername;
    }


    public synchronized int[] getMove() {
        while(valueRead){
           try {
               wait();
           } catch (InterruptedException e) {
               throw new RuntimeException(e);
           }
        }
        valueRead= true ;
        notifyAll();
        return currentMove;
    }

    public synchronized void setMove(int[] val) {
        while(!valueRead){
            try{
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
       if(val == null){
           currentMove = null ;
       }
       else {
           currentMove[0] = val[0];
           currentMove[1] = val[1];
       }
        valueRead=false ;
        notifyAll();
    }


    /**
     * set value before a game and after a game is finished.
     * @param valueRead
     */
    public void setValueRead(boolean valueRead) {
        this.valueRead = valueRead;
    }
}

