package com.nedap.go.client;


import com.nedap.go.Protocol;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This is the class client. This communicates with the client handler.
 */
public class Client implements Runnable {
    private BufferedReader reader;
    private int port;
    private InetAddress address;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private Socket socket;
    private String name;
    private Player player;
    private boolean canStartAGame;


    private static final String WELCOME = "WELCOME";
    private static final String USERNAMETAKEN = "USERNAMETAKEN";
    private static final String JOINED = "JOINED";
    private static final String NEWGAME = "NEWGAME";
    private static final String MOVE = "MOVE";
    private static final String YOURTURN = "YOURTURN";
    private static final String INVALIDMOVE = "INVALIDMOVE";
    private static final String GAMEOVER = "GAMEOVER";

    /**
     * Constructor
     *
     * @param address The inetAddress of the server
     * @param port    The port the server is listening on
     * @param input   The input from the scanner to the client
     */
    public Client(InetAddress address, int port, Reader input) {
        this.address = address;
        this.port = port;
        this.reader = new BufferedReader(input);
        canStartAGame = false; // can only start a game when the handshake is done and is not already in a game

    }

    /**
     * Makes a connection with the server.
     */
    public boolean connect() {
        try {
            socket = new Socket(address, port);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter = new PrintWriter(socket.getOutputStream());
            Thread clientThread = new Thread(this);
            clientThread.start();
            return true;
        } catch (IOException e) {
            System.out.println("Sorry, unable to make an connection. ");
            return false;
        }

    }

    /**
     * Initiation of the handshake.
     */
    private void handShake() {
        printWriter.println("HELLO" + Protocol.delimiter + "client description");
        printWriter.flush();

    }

    /**
     * Runs this operation. Stops when the is no connection anymore.
     * Handles the input from the server.
     */
    @Override
    public void run() {
        handShake();
        while (!socket.isClosed()) {

            try {
                String line;
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                String[] splittedLine = line.split("~");
                String command = splittedLine[0].toUpperCase();
                switch (command) {
                    case WELCOME:
                        System.out.println(splittedLine[1]);
                        System.out.println("please type your username");
                        name = reader.readLine();
                        sendMessage("USERNAME" + Protocol.delimiter + name);
                        break;
                    case USERNAMETAKEN:
                        System.out.println(splittedLine[1]);
                        name = reader.readLine();
                        sendMessage("USERNAME" + Protocol.delimiter + name);
                        break;
                    case JOINED:
                        System.out.println("you now joined the system if you want to play the game go type : GO");
                        canStartAGame = true;
                        break;
                    case NEWGAME:
                        newGame(splittedLine[1], splittedLine[2]);
                        break;
                    case YOURTURN:
                        yourTurn();
                        break;
                    case INVALIDMOVE:
                        System.out.println("you send a illegal move try another move ");
//                        yourTurn();
                        break;
                    case MOVE:
                        if (splittedLine[2].equals("PASS")) {
                            movePass();
                        } else if (splittedLine[1].equals(name)) {
                            move(Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]), true);
                        } else {
                            move(Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]), false);
                        }
                        break;
                    case GAMEOVER:
                        System.out.println("Game over  because: " + splittedLine[1] + " the winner is: " + splittedLine[2]);
                        canStartAGame = true;
                        break;
                    default:
                        System.out.println("Do not understand this line :" + line);
                        break;
                }

            } catch (IOException e) {
                System.out.println("socket is closed");
            }


        }
    }

    /**
     * This function handles the closing of the client nicely.
     */
    public void close() {


        try {
            socket.close();
            printWriter.close();
        } catch (IOException e) {
            System.out.println("unable to close the socket");
        } catch (NullPointerException e) {
            // This is if never been open.
            System.out.println(" ");
        }

        try {
            reader.close();
        } catch (IOException e) {
            System.out.println("unable to close the reader");
        }


    }


    /**
     * This is a case of the switch command.
     * Makes a player because the game is started.
     *
     * @param player1 first name server sends
     * @param player2 second name server sends
     */
    private void newGame(String player1, String player2) {
        System.out.println("WELKOM TO THIS GAME");
        System.out.println("" + player1 + " is black and " + player2 + " is white");
        System.out.println("Do you want to play with a computer player type : PC");
        String playerType;
        try {
            playerType = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (playerType.equalsIgnoreCase("PC")) {
            if (player1.equals(name)) {
                player = new ComputerPlayer(name, StoneColour.BLACK);
            } else {
                player = new ComputerPlayer(name, StoneColour.WHITE);
            }

        } else {
            if (player1.equals(name)) {
                player = new HumanPlayer(name, StoneColour.BLACK, reader);
            } else {
                player = new HumanPlayer(name, StoneColour.WHITE, reader);
            }
        }
        canStartAGame = false;

    }

    /**
     * This is a case of the switch command.
     * Asks player which move he wants to make.
     * Invoke messages sender to send the move to the player.
     */
    private void yourTurn() {
        Move move = player.determineMove();
        String message;
        if (move == null) {

            message = "PASS";

        } else if (move.getCol() == -1) {
            message = "QUIT";
        } else  { //move != null
            message = "MOVE" + Protocol.delimiter + name + Protocol.delimiter + move.getRow() + Protocol.delimiter + move.getCol();
        }
        sendMessage(message);
    }


    /**
     * Sends to last made move to the player (which updates the board on the gui and board used to check validity of the move)
     *
     * @param row     row
     * @param col     col
     * @param ownMove true is it is a move made by this player
     */
    private void move(int row, int col, boolean ownMove) {
        Move move;
        if (ownMove) {
            move = new Move(row, col, player.getColour());
        } else {
            move = new Move(row, col, player.getColourOpponent());
        }
        player.updateBoard(move);
    }


    /**
     * Sends the board update that the last move was pass.
     */
    private void movePass() {
        player.updateBoard(null);
    }


    /**
     * Sends a message to the server that the player want to go to the queue.
     */
    public void goToQueue() {
        sendMessage("QUEUE");
    }

    /**
     * boolean to check is someone is may start a game .
     *
     * @return true if it is oke to start a game.
     */
    public boolean isAbleToStartAGame() {
        return canStartAGame;
    }

    /**
     * Sends messages to the server
     *
     * @param message the message that need to be sent to the server.
     */
    private void sendMessage(String message) {
        printWriter.println(message);
        printWriter.flush();

    }

}

