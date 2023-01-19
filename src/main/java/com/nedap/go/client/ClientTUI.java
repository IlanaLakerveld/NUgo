package com.nedap.go.client;

import com.nedap.go.getPortInput;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientTUI {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

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
        Client client = new Client(addressSever, port);
        client.connect();
        Boolean wantToQuit = false;
        while (!wantToQuit) {
            String message = scanner.nextLine();
            if (message.equals("quit")) {
                wantToQuit = true;
                client.close();

            }
        }
    }
}
