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
        System.out.println("Trying to connect with " + addressSever  + " on port : " + port);

        boolean wantToQuit = false;
        PipedReader pipedReader = new PipedReader();
        try {
            PipedWriter pipedWriter = new PipedWriter(pipedReader);

            Client client = new Client(addressSever, port, pipedReader);
            PrintWriter printWriter = new PrintWriter(pipedWriter);
            client.connect();

            // reads text input if its quit or go it does something otherwise sends it to the client
            while (!wantToQuit) {
                String message = scanner.nextLine();
                if (message.toLowerCase().equals("quit")) {
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
