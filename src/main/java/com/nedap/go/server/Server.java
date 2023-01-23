package com.nedap.go.server;

import com.nedap.go.spel.Board;
import com.nedap.go.spel.StoneColour;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int port;
    private boolean active;
    private Thread socketThread;

    private Queue playerQueue = new LinkedList();

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
            System.out.println("Can not close the serversocket");
//            throw new RuntimeException(e);
        }
        try {
            socketThread.join();
        } catch (InterruptedException e) {
            System.out.println("can not join the socket thread");
//            throw new RuntimeException(e);
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
        if(playerQueue.size()  >= 2){
            AbstractPlayer player1 = new AbstractPlayer(StoneColour.BLACK, (ClientHandler) playerQueue.poll()) ;
            AbstractPlayer player2 = new AbstractPlayer(StoneColour.WHITE, (ClientHandler) playerQueue.poll()) ;
            player1.sendMessage("NEWGAME~"+player1.getName()+"~"+player2.getName());
            player2.sendMessage("NEWGAME~"+player1.getName()+"~"+player2.getName());
            new Thread(new GameGo(player1,player2, Board.DIM) ).start();
        }
    }

// PLayerQueue moet door een thread tegelijkertijd moeten kunnen bereikt
    public synchronized void addOrRemovePlayerFromQueue(ClientHandler cl){
        if(!playerQueue.isEmpty() && playerQueue.contains(cl.getMyUsername())){
            playerQueue.remove(cl.getMyUsername());
        }
        else {
            playerQueue.add(cl);
            startNewGame();
        }
    }


}
