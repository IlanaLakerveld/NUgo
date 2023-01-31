package com.nedap.go.server;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
        server.start();

        try (Socket socketCL = new Socket("localhost", server.getPort())) {
            bufferedReader = new BufferedReader(new InputStreamReader(socketCL.getInputStream()));
            printWriter = new PrintWriter(socketCL.getOutputStream());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @Test
    public void exceptionTest() throws IOException {
        assertTrue(server.isActive());
        printWriter.println("hello");
        System.out.println(bufferedReader.readLine());

    }

}
