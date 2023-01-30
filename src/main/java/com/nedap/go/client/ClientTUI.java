package com.nedap.go.client;

import com.nedap.go.getPortInput;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * This is the client TUI run this method to start a client.
 */
public class ClientTUI {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int port = getPortInput.getPort(true);
        boolean okeInputAdress = false;
        InetAddress addressSever = null;
        while (!okeInputAdress) {
            System.out.println("what server address do you want? ");
            String inputAddress = scanner.nextLine();


            try {
                addressSever = InetAddress.getByName(inputAddress);
                okeInputAdress = true;
            } catch (UnknownHostException e) {
                System.out.println("please choose a correct input address");

            }
        }
        System.out.println("Trying to connect with " + addressSever + " on port : " + port);

        boolean wantToQuit = false;
        PipedReader pipedReader = new PipedReader();
        try {
            PipedWriter pipedWriter = new PipedWriter(pipedReader);

            Client client = new Client(addressSever, port, pipedReader);
            PrintWriter printWriter = new PrintWriter(pipedWriter);
            boolean connectionCorrect = client.connect();
            System.out.println("If you need help type help. If you want to quit type quit");

            // reads text input if its quit or go it does something otherwise sends it to the client
            while (!wantToQuit && connectionCorrect) {
                String message = scanner.nextLine();
                if (message.contains("~")) {
                    System.out.println("you are not allowed to use the ~");

                } else if (message.toLowerCase().equals("quit")) {
                    wantToQuit = true;
                    client.close();
                    printWriter.close();
                    pipedReader.close();
                    pipedWriter.close();

                } else if (message.toUpperCase().equals("GO")) {
                    if (client.isAbleToStartAGame()) {
                        client.goToQueue();
                    } else {
                        System.out.println("you can not start a game because you are already in a game or needs to handle the handshake first");
                    }
                } else if (message.toLowerCase().equals("help")) {
                    System.out.print("""
                            Hello! to play a game first the username needs to be unique and correct. 
                            when you want to play a game type GO
                            when you want do not want to be waiting for a game anymore but you are already in queue type GO to go out of the queue
                            when you are in the game wait until there is told what you want to do 
                            if you want to quit type quit
                            NEVER use ~ in anything :) 
             
                            """);
                } else {
                    printWriter.println(message);
                    printWriter.flush();
                }

            }
        } catch (IOException e) {
            System.out.println("pipedWriter is closed");
        }
        // window does not close itself
        System.out.println("you stopped the game please close the board window");
    }
}
