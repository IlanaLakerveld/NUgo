package com.nedap.go.server;

import com.nedap.go.Protocol;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.assertTrue;


public class HandshakeTest {

    private Server server ;
    private BufferedReader bufferedReader;
    PrintWriter printWriter ;



    @Before
    public void setUp() throws IOException {
        // the server
        server = new Server(0);
        server.start();
        assertTrue(server.isActive());


        // the client
        InetAddress addressSever = InetAddress.getByName("localhost");
        Socket socketCL = new Socket(addressSever, server.getPort()) ;
        bufferedReader = new BufferedReader(new InputStreamReader(socketCL.getInputStream()));
        printWriter = new PrintWriter(socketCL.getOutputStream());

    }


    @Test
    public void TestWelcome() throws IOException {


        printWriter.println("HELLO");
        printWriter.flush();
        assertTrue(bufferedReader.readLine().contains("WELCOME~"));


    }


    @Test
    public void TestUsername() throws IOException {
        printWriter.println("USERNAME" + Protocol.delimiter + "ilana");
        printWriter.flush();
        assertTrue(bufferedReader.readLine().contains("JOINED"));
        printWriter.println("USERNAME" + Protocol.delimiter + "ilana");
        printWriter.flush();
        assertTrue(bufferedReader.readLine().contains("USERNAMETAKEN"+ Protocol.delimiter));
    }




}
