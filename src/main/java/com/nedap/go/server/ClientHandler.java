package com.nedap.go.server;

import com.nedap.go.IncorrectServerClientInputException;
import com.nedap.go.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This is the class ClientHandlers. Every client has his own clientHandler. This functions communicate with the client.
 * This class implements Runnable.
 */
public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;
    private final PrintWriter pW;
    private final BufferedReader bR;

    private String myUsername;
    private int[] currentMove;
    private boolean valueRead; //This boolean is used for synchronisation.
    private boolean connectionLost;


    private static final String HELLO = "HELLO";
    private static final String USERNAME = "USERNAME";
    private static final String QUEUE = "QUEUE";
    private static final String PASS = "PASS";
    private static final String MOVE = "MOVE";
    private static final String QUIT = "QUIT";

    /**
     * constructor of the clientHandler.
     * The constructor makes the reader and writer.
     *
     * @param socket the socket from the connection.
     * @param server the server from which the clientHandler is initialized.
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
        connectionLost = false;
    }


    /**
     * This is the run function. While there is an open socket this function will keep running
     * This function is responsible for processing the input from the client.
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
                String[] splitLine = line.split("~");
                System.out.println("the input line is :" + line); // Prints the input line
                switchStatementInput(line, splitLine);

            } catch (IOException e) {
                System.out.println("Connection with a client is lost"); //This is seen on the server output
                break;
            }

        }
        close();
    }


    /**
     * handels the input in a switch statement
     * @param line the input line
     * @param splitLine the input line splitted by the ~
     */
    public void switchStatementInput(String line, String[] splitLine) {
        switch (splitLine[0].toUpperCase()) {
            case HELLO -> hello();
            case USERNAME -> username(splitLine[1]);
            case QUEUE -> queue();
            case PASS -> pass();
            case MOVE -> move(Integer.parseInt(splitLine[2]), Integer.parseInt(splitLine[3]));
            case QUIT -> quit();
            default ->
                    throw new IncorrectServerClientInputException("Does not understand the input: " + line) ;

        }
    }


    /**
     * This is one case of the switch command. This will add this client to the queue to start a game. if a client is already in the queue he will be removed.
     */
    private void queue() {
        server.addOrRemovePlayerFromQueue(this);

    }

    /**
     * This is case of the switch command.
     * Checks if the username already exist. If already exist the user should give another username otherwise this is the username.
     *
     * @param username the given username by the client.
     */
    private void username(String username) {
        if (!server.usernames.contains(username)) {
            this.myUsername = username;
            server.usernames.add(username);
            sendMessage("JOINED");

        } else {
            sendMessage("USERNAMETAKEN" + Protocol.delimiter + "username is already taken please use another username");
        }

    }

    /**
     * This is a case of the switch command.
     * Sends a messages about this server to the client.
     */
    private void hello() {
        sendMessage("WELCOME" + Protocol.delimiter + "This is the server van Ilana");
    }

    /**
     * This is a case of the switch command.
     */
    private void pass() {
        setMove(null);
    }

    /**
     * This is a case of the switch command.
     *
     * @param row row
     * @param col col
     */
    private void move(int row, int col) {
        int[] value = new int[]{row, col};
        setMove(value);

    }

    /**
     * This is a case of the switch command.
     * The client tells the server that it is quitting.
     */
    private void quit() {
        setConnectionLost(true);
    }


    /**
     * Closes the clientHandler nicely.
     */
    public void close() {
        try {
            socket.close();
            server.usernames.remove(myUsername);
            bR.close();
            setConnectionLost(true);
        } catch (IOException e) {
            System.out.println("cannot close the client Handler");
        }

    }

    /**
     * Sends messages to the client.
     *
     * @param message the messages that sends to the client.
     */
    public void sendMessage(String message) {
        pW.println(message);
        pW.flush();
    }

    /**
     * Username getter
     *
     * @return username of the client.
     */
    public String getMyUsername() {
        return myUsername;
    }

    /**
     * This function is used to get a given move from a client. THis is used by the gameGo.
     * The move can only be returned if there is a move there, otherwise the thread will wait until value is read.
     *
     * @return int[] = row,col
     */
    public synchronized int[] getMove() {
        while (valueRead && !connectionLost) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Something goes wrong with synchronisation");
            }
        }
        valueRead = true;
        notifyAll();
        return currentMove;
    }

    /**
     * This function is used by the clientHandler to set the given move of the game by the client. This move can then be reached throw getMove. THis can only be done if the there is no move that needs to be read yet.
     *
     * @param moveValue the move is given by the client.
     */
    public synchronized void setMove(int[] moveValue) {
        while (!valueRead) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Something goes wrong with synchronisation");
            }

        }
        if (moveValue == null) {
            currentMove = null;
        } else {
            currentMove = new int[]{moveValue[0], moveValue[1]};
        }
        valueRead = false;
        notifyAll();
    }


    /**
     * Set value before a game and after a game is finished.
     * This boolean is used for synchronisation.
     *
     * @param valueRead a boolean value that is true if there is a move to that can be read.
     */
    public void setValueRead(boolean valueRead) {
        this.valueRead = valueRead;
    }


    /**
     * @return returns true is there is no more connection with the client.
     */

    public boolean isConnectionLost() {
        return connectionLost;
    }

    /**
     * Used to handle the case the connection is lost on the moment the game is waiting for a move
     *
     * @param connectionLost boolean value that is true if the connection is lost
     */
    private synchronized void setConnectionLost(boolean connectionLost) {
        this.connectionLost = connectionLost;
        notifyAll();
    }
}

