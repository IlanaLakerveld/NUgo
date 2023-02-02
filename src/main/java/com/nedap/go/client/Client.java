package com.nedap.go.client;


import com.nedap.go.IncorrectServerClientInputException;
import com.nedap.go.Protocol;
import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

/**
 * This is the class client. This communicates with the clientHandler.
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
    private boolean canStartAGame; // makes sure that you can only start a game when the handshake is done and is not already in a game
    private boolean connectionWithServer;


    private static final String WELCOME = "WELCOME";
    private static final String USERNAMETAKEN = "USERNAMETAKEN";
    private static final String JOINED = "JOINED";
    private static final String NEWGAME = "NEWGAME";
    private static final String MOVE = "MOVE";
    private static final String YOURTURN = "YOURTURN";
    private static final String INVALIDMOVE = "INVALIDMOVE";
    private static final String GAMEOVER = "GAMEOVER";

    /**
     * Constructor of the client
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
            connectionWithServer=true;
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
        printWriter.println("HELLO" + Protocol.delimiter + "Hello! A client ");
        printWriter.flush();

    }

    /**
     * Runs this operation. Stops when the is no connection anymore.
     * Handles the input from the server.
     */
    @Override
    public void run()  {

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
                    case WELCOME -> welcome(splittedLine[1]);
                    case USERNAMETAKEN -> usernameTaken(splittedLine[1]);
                    case JOINED -> joined();
                    case NEWGAME -> newGame(splittedLine[1], splittedLine[2]);
                    case YOURTURN -> yourTurn();
                    case INVALIDMOVE -> invalidMove();
                    case MOVE -> switchMove(splittedLine);
                    case GAMEOVER -> gameOver(splittedLine[1], splittedLine[2]);
                    default -> throw new IncorrectServerClientInputException("Do not understand this line :" + line) ;
                }

            } catch (IOException e) {
                System.out.println("socket is closed");
                break;
            }


        }
        System.out.println("The server is closed or you stopped the game , please enter to close everything nicely ");
        connectionWithServer=false;
    }

    /**
     * This function handles the closing of the client nicely.
     */
    public void close() {

        printWriter.println("QUIT");
        printWriter.flush();

        try {
            socket.close();
            printWriter.close();
        } catch (IOException e) {
            System.out.println("unable to close the socket");
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
        if (playerType !=null && playerType.equalsIgnoreCase("PC")) {
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
     * Tells the user that the game is over and why
     * @param reason the game is stopped
     * @param winner the winner of the game
     */
    private void gameOver(String reason, String winner) {
        if(reason.equals("VICTORY")){
            System.out.println("Game over because somebody won the game" );
            System.out.println("The score is as follows : ");
            player.endScore() ;
            System.out.println("The winner is :" +winner);
        } else if (reason.equals("DISCONNECT")) {
            System.out.println("The game is over because someone disconnected from the server the winner is :" +winner);
        }
        player.stopGUI();
        canStartAGame = true;
    }

    private void switchMove(String[] splittedLine ) {
        if (splittedLine[2].equals("PASS")) {
            movePass();
        } else if (splittedLine[1].equals(name)) {
            move(Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]), true);
        } else {
            move(Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]), false);
        }

    }

    /**
     * This is a case of the switch command.
     * Asks for the username.
     *
     * @param input string input from the server
     */
    private void welcome(String input) {
        System.out.println(input);
        System.out.println("please type your username");
        try {
            name = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendMessage("USERNAME" + Protocol.delimiter + name);
    }


    /**
     * This is a case of the switch command.
     * Aks for new username if the username is taken
     *
     * @param input input string from the server
     */
    private void usernameTaken(String input) {
        System.out.println(input);
        try {
            name = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendMessage("USERNAME" + Protocol.delimiter + name);


    }

    /**
     * This is a case of the switch command.
     * Tells the user it can now join the queue.
     */
    private void joined() {
        System.out.println("you now joined the system if you want to play the game go type : GO");
        canStartAGame = true;
    }


    /**
     * This is a case of the switch command.
     * Tells the user the move was invalid.
     */
    private void invalidMove() {
        System.out.println("you send a illegal move try another move ");
//                        yourTurn();

    }

    /**
     * This is a case of the switch command.
     * Asks player which move he wants to make.
     * Invoke messages sender to send the move to the player.
     */
    private void yourTurn() {

        Move move ;

        try {
            move = player.determineMove();
            String message;
            if (move == null) {

                message = "PASS";


            } else { //move != null
                message = "MOVE"  + Protocol.delimiter + move.getRow() + Protocol.delimiter + move.getCol();
            }
            sendMessage(message);

        } catch (QuitGameException e) {
            sendMessage("QUIT");
        }
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

    public boolean isConnectionWithServer() {
        return connectionWithServer;
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

