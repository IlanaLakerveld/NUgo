package com.nedap.go.client;


import com.nedap.go.spel.Move;
import com.nedap.go.spel.StoneColour;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
     Scanner scanner ;


    private int port ;
    private InetAddress address ;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private Socket socket ;

    private String name ;
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

    public Client(InetAddress address, int port,Scanner scanner ) {
        this.address=address;
        this.port=port;
        this.scanner =scanner ;
        canStartAGame = false;

    }

    public void connect(){
        try {
            socket= new Socket(address,port);
            bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printWriter=new PrintWriter(socket.getOutputStream());
            Thread clientThread = new Thread(this);
            clientThread.start();
        } catch (IOException e) {
            System.out.println("Sorry, unable to make an connection");
        }

    }

    private void handShake(){
        printWriter.println("HELLO~client description");
        printWriter.flush();

//        try {
//            String line = bufferedReader.readLine();
//            System.out.println(line) ;
//
//
//
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println("type your username, you can not use ~ in this ");
//        String s = scanner.nextLine();
//        printWriter.println("USERNAME~"+s);
//        printWriter.flush();
//        try {
//            System.out.println(bufferedReader.readLine());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

    }

    /**
     * Runs this operation.
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
                        name = "ilana" ;
                        printWriter.println("USERNAME~ilana");
                        printWriter.flush();
                        break;
                    case USERNAMETAKEN:
                        System.out.println(splittedLine[1]);
                        name = "versie2naam" ;
                        printWriter.println("USERNAME~versie2naam");
                        printWriter.flush();
                        break;
                    case JOINED:
                        System.out.println("you now joined the system if you want to play the game go type : GO");
                        canStartAGame = true ;
                        break;
                    case NEWGAME:
                        System.out.println("WELKOM TO THIS GAME");
                        if(splittedLine[1].equals(name)){
                            newGame(StoneColour.BLACK);
                        }
                        else{
                            newGame(StoneColour.BLACK);
                        }
                        canStartAGame = false ;
                        break;
                    case YOURTURN :
                        yourturn();
                        break;
                    case INVALIDMOVE :
                        System.out.println("you send a illegal move try another move ");
//                        yourturn();
                        break;
                    case MOVE :
                        if(splittedLine[2].equals("PASS")){
                           movePass();
                        }
                        else if(splittedLine[1].equals(name)) {
                            move(Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]),true);
                        }
                        else{
                            move(Integer.parseInt(splittedLine[2]), Integer.parseInt(splittedLine[3]),false);
                        }
                        break ;
                    case GAMEOVER:
                        System.out.println("Game over  because: "+splittedLine[1] +" the winner is: "+splittedLine[2]);
                        canStartAGame = true ;
                        break;
                    default:
                        System.out.println("default");
                        break;
                }

            } catch (IOException e) {
                System.out.println("socket is closed");
            }


        }
    }
    public void close(){
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("unable to close the socket");
        }
        printWriter.close();
    }

    private void newGame(StoneColour colour) {
         player = new HumanPlayer(name, colour,scanner);
    }

    private void yourturn(){
        Move move = player.determineMove();
        String message;
        if(move != null) {
            message= "MOVE~" + name + "~" + move.getRow() + "~" + move.getCol();
        }
        else{
            message= "MOVE~" + name +"~" +"PASS";
        }
        sendMessage(message);
    }

    private void move(int row, int col,boolean ownMove){
        Move move;
        if(ownMove){
            move=new Move(row,col,player.getColour());
        }
        else{
            move=new Move(row,col,player.getColourOpponend());
        }
        player.updateBoard(move);
    }
    private void movePass(){
        player.updateBoard(null);
    }

    private void sendMessage(String message){
        printWriter.println(message);
        printWriter.flush();

    }
    public void goToQueue(){
        sendMessage("QUEUE");
    }


}
