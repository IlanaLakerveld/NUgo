package com.nedap.go.client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    static Scanner scanner = new Scanner(System.in);


    private int port ;
    private InetAddress address ;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private Socket socket ;

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
//                        String s = scanner.nextLine();
                        printWriter.println("USERNAME~ilana");
                        printWriter.flush();
                        break;
                    case USERNAMETAKEN:
                        System.out.println(splittedLine[1]);
                        printWriter.println("USERNAME~2");
                        printWriter.flush();
                        break;
                    case JOINED:
                        System.out.println("you now joined the system if you want to play the game go type : GO");
                        printWriter.println("QUEUE");
                        printWriter.flush();
                        break;
//                    case NEWGAME:
//                    case MOVE:
//                    case GAMEOVER:
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



}
