package com.nedap.go.client;

import com.nedap.go.getPortInput;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

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
        var pr1 = new PipedReader();
        try {
            var pw1Piped = new PipedWriter(pr1);

            Client client = new Client(addressSever, port, pr1);
            PrintWriter pwr2PrintWriter = new PrintWriter(pw1Piped);
            client.connect();
        while (!wantToQuit) {
            String message = scanner.nextLine();
            if (message.equals("quit")) {
                wantToQuit = true;
                client.close();
                pwr2PrintWriter.close();
                pr1.close();
                pw1Piped.close();
            }
            else if(message.toUpperCase().equals("GO")){
                client.goToQueue();
            }
            else{
                pwr2PrintWriter.println(message);
                pwr2PrintWriter.flush();
            }

        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
