package com.nedap.go.client;

import com.nedap.go.getPortInput;

import java.net.InetAddress;

public class ClientTUI {
    public static void main(String[] args) {

        int port = getPortInput.getPort(true);

        System.out.println("what server address do you want? ");
        InetAddress addressSever = null;


    }
}
