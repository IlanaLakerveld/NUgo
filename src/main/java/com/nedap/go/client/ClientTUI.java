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


    static String help = """
                            Hello! to play a game first the username needs to be unique and correct.
                            when you want to play a game type GO
                            when you want do not want to be waiting for a game anymore but you are already in queue type GO to go out of the queue
                            when you are in the game wait until there is told what you want to do
                            if you want to quit type quit
                            NEVER use ~ in anything :)
                            If you want you want to rules of go type : rules

                            """ ;

    static String rules = """
                Black makes the first move, after which white and black alternate.
                A move consists of placing one stone of a player their own color on an empty intersection on the board.
                A player may pass their turn at any time.
                A stone or solidly connected group of stones of one color is captured and removed from the board when all the intersections directly orthogonally adjacent to it are occupied by the opponent.
                Self-capture/suicide is allowed.
                When a suicide move results in capturing a group, the group is removed from the board first and the suiciding stone stays alive.
                Two consecutive passes will end the game.

                """;



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int port = getPortInput.getPort(true);
        boolean okeInputAddress = false;
        InetAddress addressSever = null;
        while (!okeInputAddress) {
            System.out.println("what server address do you want? ");
            String inputAddress = scanner.nextLine();


            try {
                addressSever = InetAddress.getByName(inputAddress);
                okeInputAddress = true;
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
            if(connectionCorrect) {
                System.out.println("If you need help type help. If you want to quit type quit");
            }
            // reads text input if its quit or go it does something otherwise sends it to the client
            while (!wantToQuit && connectionCorrect && client.isConnectionWithServer()) {

                String message = scanner.nextLine();

                if (message.contains("~")) {
                    System.out.println("you are not allowed to use the ~");

                } else if (message.equalsIgnoreCase("quit")) {
                    wantToQuit = true;
                    client.close();
                    printWriter.close();
                    pipedReader.close();
                    pipedWriter.close();

                } else if (message.equalsIgnoreCase("GO")) {
                    if (client.isAbleToStartAGame()) {
                        client.goToQueue();
                    } else {
                        System.out.println("you can not start a game because you are already in a game or needs to handle the handshake first");
                    }
                } else if (message.equalsIgnoreCase("help")) {
                    System.out.println(help);
                }
                else if(message.equalsIgnoreCase("rules")){
                    System.out.println(rules);
                }
                else {

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
