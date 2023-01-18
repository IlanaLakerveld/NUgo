package com.nedap.go.server;

import com.nedap.go.getPortInput;

import java.util.Scanner;

/**
 * This is the TUI for the server. To start the server run main.
 */
public class ServerTUI {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        int port = getPortInput.getPort(false);
        Server server = new Server(port);
        server.start();
        System.out.println("The serverport is " + server.getPort());


        boolean quit = false;
        while (!quit) {
            if (scanner.nextLine().equals("quit")) {
                server.stop();
                quit = true;
                System.out.println("bye");
            }
        }
    }


}
