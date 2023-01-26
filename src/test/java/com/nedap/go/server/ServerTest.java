package com.nedap.go.server;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServerTest {
    private Server server ;


    @Before
    public void setUp(){
        server = new Server(0);
    }

    @Test
    public void testServer(){
        assertFalse(server.isActive());
        server.start();
        assertTrue(server.isActive());
        assertTrue(server.getPort() > 0);
        assertTrue(server.getPort() <= 65535);
        server.stop();
        assertFalse(server.isActive());

    }




}
