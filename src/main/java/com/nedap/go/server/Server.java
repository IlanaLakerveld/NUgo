package com.nedap.go.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int port;
    private boolean active;
    private Thread socketThread;

    private Queue playerQueue;

    public List usernames = new ArrayList<>();



    private List<ClientHandler> clientHandlerList = new ArrayList();

    /**
     * Constructor
     *
     * @param port on which the server is.
     */
    public Server(int port) {
        this.port = port;
        active = false;
    }


    /**
     * The starting function start the server. The function only works if not already active
     */

    public void start() {
        if (active) {
            System.out.println("The server is already active");
            return;
        }
        if (port < 0 || port > 65535) {
            System.out.println(port + " is not a valid port number");
            return;
        }
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.out.println("Error : Cannot connect to the server.");
        }
        socketThread = new Thread(this);
        socketThread.start();
        active = true;

    }

    /**
     * @return returns the port the server is on. if the server is not active the get port returns -1
     */
    public int getPort() {
        if (active) {
            return serverSocket.getLocalPort();
        }
        return -1;// if the server is not active
    }


    /**
     * If this function is called, this function is trying to stop the server nicely
     */
    public void stop() {
        if (!active) {
            System.out.println("Server is not active, so unable to stop the server");
            return;
        }
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socketThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        active = false;
    }

    /**
     * While the server is open this is on a separate thread handling the incoming clients.
     * Each of these clients get his one client handler on a separate thread.These client handlers can be found in the list clientHandlerList.
     */
    @Override
    public void run() {

        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept(); // Here it waits until it has a connection
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlerList.add(clientHandler);
                new Thread(clientHandler).start(); // The thread for nieuw client
            } catch (IOException e) {
                System.out.println("Server is closed");
            }
        }
    }

    /**
     * Starts a new game if there are more than 2 players in the queue.
     *
     * Hier zit waarschijnlijk het probleem. Deze functie moet bedenken dat er een mensen in de wachtrij zitten  en met die speler het spel starten
     */
    public void startNewGame() {
//
//        while (playerQueue.size() < 2) {}
                    // wacht hier totdat dit niet zo is
//
        // als wel zo is dan haal die twee client handlers uit de wacht ij
        // start een spel met die twee Clienthandlers
        // Die twee clienthandlers zitten hebben allebei een apparte thread.
        // het spel kan gestart worden door de functie gameGo te starten.
        // gameplay heeft abstract players als input functie iedere clienthandler word wat de client handler is en een de kleur die ze zijn.
        // (nog niet helemaal uitgewerkt idee maar zoiets)
        // meest logische is volgens mij om de gameGO op een aparte thread te zetten maar daarvoor wil je eerst van de ClientHandler threads af.
        // op moment dat spel if afgelopoen zou je wel graag de aparte threads weer terug hebben zodat clients nog een keer spel kunnen spelen
    }


    public void addToPlayerQueue(ClientHandler cl){
        playerQueue.add(cl) ;
    }



/// DIT KAN NIET HIERBIJ GAAN SPELLEN DOORELKAAR HEEN DINGEN NAAR ELKAAR SCHREEUWEN
    // Deze functie gaat weg
    public void messageSender(String message) {
        for(ClientHandler cl : clientHandlerList){
          cl.sendMessage(message);
        }
    }



}
