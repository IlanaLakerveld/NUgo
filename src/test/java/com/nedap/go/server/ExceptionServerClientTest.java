package com.nedap.go.server;

import com.nedap.go.IncorrectServerClientInputException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test of het een exception throws
 */
public class ExceptionServerClientTest {

    private Server server ;
    private BufferedReader bufferedReader;
    PrintWriter printWriter ;



    @Before
    public void setUp(){
        server = new Server(0);

    }


//    @Test(expected = IIncorrectServerClientInputException)
    @Test
    public void exceptionTest() {

        IncorrectServerClientInputException incorrectServerClientInputException = Assert.assertThrows(IncorrectServerClientInputException.class, () -> test2());
        assertEquals(" Does not understand the input: incorrect input", incorrectServerClientInputException.getMessage());





    }

    private void test2() throws IOException {
        // the server
        server.start();
        assertTrue(server.isActive());


        // the client
        InetAddress addressSever = InetAddress.getByName("localhost");
        Socket socketCL = new Socket(addressSever, server.getPort()) ;
        bufferedReader = new BufferedReader(new InputStreamReader(socketCL.getInputStream()));
        printWriter = new PrintWriter(socketCL.getOutputStream());
        printWriter.println("incorrect input");
        printWriter.flush();
        server.stop();

    }

}
