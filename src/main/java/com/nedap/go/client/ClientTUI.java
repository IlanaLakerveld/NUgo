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

        System.out.println("what server address do you want? ");
        String inputAddress = scanner.nextLine();
        InetAddress addressSever = null;
        try {
            addressSever = InetAddress.getByName(inputAddress);
        } catch (UnknownHostException e) {
            System.out.println("please choose a correct input address");
        }
        System.out.println("Trying to connect with " + inputAddress + " on port : " + port);

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
            if (message.equals("quit")) {
                wantToQuit = true;
                client.close();
                printWriter.close();
                pipedReader.close();
                pipedWriter.close();

            }
            else if(message.toUpperCase().equals("GO")){
                if(client.isAbleToStartAGame()) {
                    client.goToQueue();
                }
                else{
                    System.out.println("you can not start a game because you are already in a game or needs to handle the handshake first");
                }
            }
            else{
                printWriter.println(message);
                printWriter.flush();
            }

        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // window does not close itself
        System.out.println("you stopped the game please close the board window");
    }
}
