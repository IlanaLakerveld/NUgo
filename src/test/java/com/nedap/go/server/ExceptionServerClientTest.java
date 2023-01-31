package com.nedap.go.server;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ExceptionServerClientTest {

    private Server server ;
    private BufferedReader bufferedReader;
    PrintWriter printWriter ;



    @Before
    public void setUp(){
        server = new Server(0);

    }


    @Test
    public void exceptionTest() throws IOException {
        // the server
        server.start();
        assertTrue(server.isActive());


        // the client
        InetAddress addressSever = InetAddress.getByName("localhost");
        Socket socketCL = new Socket(addressSever, server.getPort()) ;
            bufferedReader = new BufferedReader(new InputStreamReader(socketCL.getInputStream()));
            printWriter = new PrintWriter(socketCL.getOutputStream());
            // the client says hello
            printWriter.println("incorrecte input");
            printWriter.flush();
            server.stop();



    }

}
