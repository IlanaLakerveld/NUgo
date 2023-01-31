package com.nedap.go.server;

import com.nedap.go.Protocol;
import com.nedap.go.spel.Board;
import com.nedap.go.spel.StoneColour;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * This is the class server.
 */

public class Server implements Runnable {

    private ServerSocket serverSocket;
    private int port;
    private boolean active;
    private Thread socketThread;
    private Queue playerQueue = new LinkedList();
    public List<Object> usernames = new ArrayList<>();
    private List<ClientHandler> clientHandlerList = new ArrayList();

    private List<Thread> clThreads = new ArrayList<>();

    private List<Thread> gameThreads = new ArrayList<>();

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
     * The starting function start the server. The function only works if not already active.
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
     * @return returns the port the server is on (between  0 and 65535). if the server is not active the get port returns -1.
     */
    public int getPort() {
        if (active) {
            return serverSocket.getLocalPort();
        }
        return -1;// if the server is not active
    }


    /**
     * If this function is called, this function is trying to stop the server nicely.
     * Can only stop the server is the server is started. The function stops also the thread the server is on.
     */
    public void stop() {
        if (!active) {
            System.out.println("Server is not active, so unable to stop the server");
            return;
        }


        try {
            serverSocket.close();

        } catch (IOException e) {
            System.out.println("Can not close the server socket");

        }
        try {
            socketThread.join();
        } catch (InterruptedException e) {
            System.out.println("Can not join the socket thread");

        }

        for( ClientHandler clientHandler: clientHandlerList){

            clientHandler.close();
        }

        try{
            for(Thread clThread :clThreads){

                clThread.join();
            }

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

                Thread thread = new Thread(clientHandler);//.start(); // The thread for new client
                clThreads.add(thread);
                thread.start();
            } catch (IOException e) {
                System.out.println("Server is closed");
            }
        }
    }

    /**
     * Starts a new game if there are more than 2 players in the queue.
     *This game starts on a new thread. The first players on the list is player black and the second player on the list will be player white.
     */
    public void startNewGame() {
        if(playerQueue.size()  >= 2){
            ServerPlayer player1 = new ServerPlayer(StoneColour.BLACK, (ClientHandler) playerQueue.poll()) ;
            ServerPlayer player2 = new ServerPlayer(StoneColour.WHITE, (ClientHandler) playerQueue.poll()) ;
            String message ="NEWGAME"+Protocol.delimiter+player1.getName()+ Protocol.delimiter+player2.getName() ;
            player1.sendMessage(message);
            player2.sendMessage(message);
            Thread thread = new Thread(new GameGo(player1, player2, Board.DIM));
            // TODO : hier nog voor zorgen dat je Threads kan opslaan zodat je die ook kan verwijderen.

//            gameThreads.add(thread);
            thread.start();
        }
    }



    /**
     * This will add the client handler of a client to the queue if the client is already in the queue he will be removed on from the queue.
     * After someone is added, the function startNewGame is run to check is there can be started a new game.
     * @param cl client handler.
     */
    public synchronized void addOrRemovePlayerFromQueue(ClientHandler cl){
        if(!playerQueue.isEmpty() && playerQueue.contains(cl)){
            playerQueue.remove(cl);
            System.out.println(cl.getMyUsername() + " is removed from the list");
        }
        else {
            playerQueue.add(cl);
            startNewGame();
        }
    }

    /**
     *
     * @return true if the server is active
     */
    public boolean isActive() {
        return active;
    }
}
