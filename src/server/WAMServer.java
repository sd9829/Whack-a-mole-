package server;

import common.Duplexer;
import common.WAMProtocol;
import exception.WhackMoleException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

/**
 * The WAMServer waits for incoming client connections and
 * pairs them off to play the WhackMole game handled by multiple classes
 * in the same package server.
 * @author: Kelsey
 * username: ksd4250
 * @author: Soumya Dayal
 * username: sd9829
 */


public class WAMServer implements WAMProtocol, Runnable {

    /**
     * The {@link ServerSocket} used to wait for incoming client connections.
     */
    private ServerSocket server;
    /** used to read requests from the server */
    private Scanner networkIn;
    /** Used to write responses to the server. */
    private PrintStream networkOut;

    private int port_num;

    private int rows;

    private int cols;

    private int num_players;

    private int game_time;

    //private WAMClient client;

    //WAMGame board = new WAMGame(rows, cols);

    private ArrayList<Duplexer> players;

    private ArrayList<Thread> moles;
    private ArrayList<Thread> listeners;
    private int score;

    /**
     * Creates a new {@link WAMServer} that listens for incoming
     * connections on the specified port.
     *
     * @param port_num The port on which the server should listen for incoming
     *             connections.
     * @throws implements WAMProtocol, Closeable WhackMoleException
     *
     */
    public WAMServer(int port_num, int rows, int cols, int num_players, int game_time) throws WhackMoleException {
        try {
            this.port_num = port_num;
            this.rows = rows;
            this.cols = cols;
            this.game_time = game_time;
            this.num_players = num_players;
            server = new ServerSocket(port_num);
        } catch (IOException e) {
            throw new WhackMoleException(e);
        }
    }

    public void start() throws IOException{
        for (int i =0; i < num_players; i++) {
            this.players.add(new Duplexer(server.accept()));
            this.players.get(i).sendMess(WELCOME + " " + rows + " " + cols + " " + num_players + " " + i);
        }
        this.moles = new ArrayList<>(cols * rows);
        for (int i = 0; i < cols * rows; i++) {
            Thread mole_thread = new Thread(this);
            moles.add(mole_thread);
        }
        for (Duplexer client: players) {
            Thread listen = new Thread(() -> startListener(client));
            listeners.add(listen);
            listen.start();
        }
        for (Thread mole_thread: moles) {
            mole_thread.start();
        }
        int time = game_time;
        while (time > 0) {
            time--;
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            time--;
        }
    }

    public void startListener (Duplexer client) {
    boolean b = true;
    //while (if the game is running) {
        String mes = client.getMess();
        String[] tokens = mes.split(",");
        if (tokens[0].equals(WHACK)) {
            score = score + 1;
            client.sendMess(SCORE + "," + score);
        }
        else {
            client.sendMess("error invalid message");
            b = false;
            //break;
        }
    //}

    }




    @Override
    public void run() {
        Random rand = new Random();
        while (true) { //game is running
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);
            for (Duplexer client: players) {
                int x = setContents(row, col);
                client.sendMess(MOLE_UP + "," + x);
                try {
                    int time = (new Random().nextInt(3)) + 8;
                    Thread.sleep(time * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    client.sendMess(MOLE_DOWN + "," + x);
                }
            }
            }
        }

    public int setContents(int rol, int col) {
        int y = (rol + rol) + col;
        return y;
    }

    /**
     * Starts a new {@link WAMServer}. Simply creates the server and
     * calls {@link #run()} in the main thread.
     *
     * @param args Used to specify the port on which the server should listen
     *             for incoming client connections.
     * @throws WhackMoleException If there is an error starting the server.
     */
    public static void main(String[] args){

        if (args.length != 5) {
            System.out.println("Wrong values");
        }
        else {
            try {
                WAMServer WhackMole = new WAMServer(Integer.parseInt(args[0]), Integer.parseInt(args[1]),Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]),Integer.parseInt(args[4]) );
            }
            catch (WhackMoleException ee) {
                ee.printStackTrace();
            }
        }
    }

    }
