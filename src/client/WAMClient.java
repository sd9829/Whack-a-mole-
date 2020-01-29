package client;

import common.WAMProtocol;
import common.WAMGame;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The client side network interface to a Whack-A-Mole game server.
 * Each of the players in a game gets its own connection to the server.
 * This class represents the controller part of a model-view-controller
 * triumvirate, in that part of its purpose is to forward user actions
 * to the remote server.
 *
 * @author Kelsey Donovan
 * username: ksd4250
 * @author Soumya Dayal
 * username: sd9829
 */
public class WAMClient {
    //Turn on if the standard output debug messages are desired.
    private static final boolean DEBUG = true;

    /**
     * Print method that does something only if DEBUG is true
     *
     * @param logMsg the message to log
     */
    private static void dPrint( Object logMsg ) {
        if ( WAMClient.DEBUG ) {
            System.out.println( logMsg );
        }
    }

    /** client socket to communicate with server */
    private Socket clientSocket;
    /** used to read requests from the server */
    private Scanner networkIn;
    /** Used to write responses to the server. */
    private PrintStream networkOut;
    /** the model which keeps track of the game */
    private WAMGame board;
    /** sentinel loop used to control the main loop */
    private boolean go;
    private  int Row;
    private int Col;
    private static int Game_time;
    private int NumPLayers;
    private int score;



    /**
     * Hook up with a Whack-A-Mole game server already running and waiting for
     * the players to connect. Because of the nature of the server
     * protocol, this constructor actually blocks waiting for the first
     * message (connect) from the server.  Afterwards a thread that listens for
     * server messages and forwards them to the game object is started.
     *
     * @param host  the name of the host running the server program
     * @param port  the port of the server socket on which the server is listening
     *      * @throws exception.WhackMoleException If there is a problem opening the connection
     */


    public WAMClient(String host, int port)
            throws exception.WhackMoleException {
        try {
            this.clientSocket = new Socket(host, port);
            this.networkIn = new Scanner(clientSocket.getInputStream());
            this.networkOut = new PrintStream(clientSocket.getOutputStream());
            this.board = board;
            this.go = true;

            // Block waiting for the CONNECT message from the server.
            String request = this.networkIn.nextLine();
            //String arguments = this.networkIn.nextLine();
            String[] lst;
            lst = request.split(" ");
            if (!lst[0].equals(WAMProtocol.WELCOME )) {
                throw new exception.WhackMoleException("Expected CONNECT from server");
            }
            else {
                WAMClient.dPrint("Connected to server " + this.clientSocket);
                Row = Integer.parseInt(lst[1]);
                Col = Integer.parseInt(lst[2]);
                this.board = new WAMGame(Row,Col, this);
                Game_time = Integer.parseInt(lst[4]);
                NumPLayers = Integer.parseInt(lst[3]);

            }
        }
        catch(IOException e) {
            throw new exception.WhackMoleException(e);
        }
    }

    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean goodToGo() { return this.go; }

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop() {
        this.go = false;
    }

    /**
     * Called when the server sends a message saying that
     * gameplay is damaged. Ends the game.
     *
     * @param arguments The error message sent from the reversi.server.
     */
    public void error( String arguments ) {
        WAMClient.dPrint( '!' + WAMProtocol.ERROR + ',' + arguments );
        dPrint( "Fatal error: " + arguments );
        this.board.error( arguments );
        this.stop();
    }



    public WAMGame getBoard() {
        return this.board;
    }

    /**
     * to return the column number of the program.
     * @return: column (int)
     */
    public int getCol() {
        return Col;
    }

    /**
     * to return the row number.
     * @return: row (int)
     */
    public int getRow() {
        return Row;
    }

    /**
     * return the time the game will run for.
     * @return Game_time (int)
     */
    public static int getGame_time() {
        return Game_time;
    }

    /**
     * returns the number of players playing the game
     * @return: the number of players (int)
     */
    public int getNumPLayers() {
        return NumPLayers;
    }

    /**
     * Called from the GUI when it is ready to start receiving messages
     * from the server.
     */
    public void startListener() {
        new Thread(this::run).start();
    }

    /**
     * Tell the local user to choose a move. How this is communicated to
     * the user is up to the View (UI).
     */
    private void makeMove() {
        this.makeMove();
    }

    /**
     * A move has been made by one of the players
     *
     * @param arguments string from the server's message that
     *                  contains the row, then column where the
     *                  player made the move
     */
    public void moveMade( String arguments ) {
        WAMClient.dPrint( '!' + WAMProtocol.WHACK + ',' + arguments );

        String[] fields = arguments.trim().split( " " );
        //int column = Integer.parseInt(fields[1]);
        int num = Integer.parseInt(fields[0]);


        //this.moveMade(fields[0]);
    }

    /**
     * method which gets the mole to go up on the game boards.
     * @param arguments: the arguments which contain the information
     *                 about the row and column number, and other
     *                 required information.
     */
    public void Moleup(String arguments) {
        WAMClient.dPrint( '!' + WAMProtocol.MOLE_UP + ',' + arguments );
        String[] fields = arguments.trim().split( " " );
        int num = Integer.parseInt(fields[0]);
        board.setContents(num, WAMGame.Mole_status.MOLE_UP);
    }

    /**
     * the method which makes the mole go down on the game board/
     * @param arguments: the arguments (string) which contain the
     *                 information about the row and column
     *                 number and other required information.
     */
    public void Moledown(String arguments) {
        WAMClient.dPrint( '!' + WAMProtocol.MOLE_DOWN + ',' + arguments );
        String[] fields = arguments.trim().split( " " );
        int num = Integer.parseInt(fields[0]);
        board.setContents(num, WAMGame.Mole_status.MOLE_DOWN);
    }

//    public void notify_this() {
//
//    }

    public void Score(String arguments) {
        WAMClient.dPrint( '!' + WAMProtocol.SCORE + ',' + arguments );
        String[] fields = arguments.trim().split( " " );
        score = Integer.parseInt(fields[0]);
    }

    public int getScore() {
        return score;
    }

    /**
     * Called when the server sends a message saying that the
     * board has been won by this player. Ends the game.
     */
    public void gameWon() {
        WAMClient.dPrint( '!' + WAMProtocol.GAME_WON );

        dPrint( "You won! Yay!" );
        this.board.gameWon();
        this.stop();
    }

    /**
     * Called when the server sends a message saying that the
     * game has been won by the other player. Ends the game.
     */
    public void gameLost() {
        WAMClient.dPrint( '!' + WAMProtocol.GAME_LOST );
        dPrint( "You lost! Boo!" );
        this.board.gameLost();
        this.stop();
    }

    /**
     * Called when the server sends a message saying that the
     * game is a tie. Ends the game.
     */
    public void gameTied() {
        WAMClient.dPrint( '!' + WAMProtocol.GAME_TIED );
        dPrint( "You tied! Meh!" );
        this.board.gameTied();
        this.stop();
    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    public void close() {
        try {
            this.clientSocket.close();
        }
        catch( IOException ioe ) {
            // squash
        }
        this.board.close();
    }

    /**
     * UI wants to send a new move to the server.
     *
     * @param col the column
     */
    public void sendMove(int col) {
        this.networkOut.println( WAMProtocol.WHACK + " " + col );
    }



    /**
     * Run the main client loop. Intended to be started as a separate
     * thread internally. This method is made private so that no one
     * outside will call it or try to start a thread on it.
     */
    private void run() {
        while (this.goodToGo()) {
            try {
                String request = this.networkIn.next();
                String arguments = this.networkIn.nextLine().trim();
                WAMClient.dPrint( "Net message in = \"" + request + '"' );

                switch ( request ) {
                    case WAMProtocol.SCORE:
                        Score(arguments);
                        break;
                    case WAMProtocol.MOLE_DOWN:
                        Moledown(arguments);
                        break;
                    case WAMProtocol.MOLE_UP:
                        Moleup(arguments);
                        break;
                    case WAMProtocol.WHACK:
                        moveMade( arguments );
                        break;
                    case WAMProtocol.GAME_WON:
                        gameWon();
                        board.reset();
                        break;
                    case WAMProtocol.GAME_LOST:
                        gameLost();
                        board.reset();
                        break;
                    case WAMProtocol.GAME_TIED:
                        gameTied();
                        board.reset();
                        break;
                    case WAMProtocol.ERROR:
                        error( arguments );
                        break;
                    default:
                        System.err.println("Unrecognized request: " + request);
                        this.stop();
                        break;
                }
            }
            catch( NoSuchElementException nse ) {
                // Looks like the connection shut down.
                this.error( "Lost connection to server." );
                this.stop();
            }
            catch( Exception e ) {
                this.error( e.getMessage() + '?' );
                this.stop();
            }
        }
        this.close();
    }


}
