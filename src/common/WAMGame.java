package common;

import client.WAMClient;

import java.util.LinkedList;
import java.util.List;

/**
 * this class forms the board, which acts as the model
 * for the WhackAMole game.
 * @author Soumya Dayal
 * username: sd9829
 * @author Kelsey Donovam
 * username: ksd4250
 */

public class WAMGame {

    private WAMClient client;
    /**
     * the number of rows
     */
    private int ROWS;
    /**
     * the number of columns
     */
    private int COLS;

    /**
     * Used to indicate if mole is up/down
     */
    public enum Mole_status {
        MOLE_UP, MOLE_DOWN, NONE;

        public Mole_status opponent() {
            return this == MOLE_UP ?
                    MOLE_DOWN :
                    this == MOLE_DOWN ?
                            MOLE_UP :
                            this;
        }
    }


    /**
     * Possible statuses of game
     */
    public enum Status {
        NOT_OVER, I_WON, I_LOST, TIE, ERROR;

        private String message = null;

        public void setMessage(String msg) {
            this.message = msg;
        }

        @Override
        public String toString() {
            return super.toString() + this.message == null ? "" : ('(' + this.message + ')');
        }
    }

//    /**
//     * How many moves are left to make before end of game
//     */
//    private int timeLeft;


    /**
     * current game status
     */
    private Status status;

    private Mole_status[][] board; // = new Mole_status[ROWS][COLS];

    /**
     * the observers of this model
     */
    private List<common.Observer<WAMGame>> observers;

    public WAMGame(int ROWS, int COLS, WAMClient client) {
        this.ROWS = ROWS;
        this.COLS = COLS;
        this.board = new Mole_status[ROWS][COLS];
        this.observers = new LinkedList<>();

        this.client = client;

        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                this.board[row][col] = Mole_status.NONE;
            }
        }

        this.status = Status.NOT_OVER;
    }


    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(common.Observer<WAMGame> observer) {
        this.observers.add(observer);
    }

    /**
     * when the model changes, the observers are notified via their update() method
     */
    private void alertObservers() {
        for (common.Observer<WAMGame> obs : this.observers) {
            obs.update(this);
        }
    }

//   public int getScore() {
//        return
//    }

    public void error(String arguments) {
        this.status = Status.ERROR;
        this.status.setMessage(arguments);
        alertObservers();
    }

    public int getROWS() {
        return ROWS;
    }

    public int getCOLS() {
        return COLS;
    }

    /**
     * Get game status.
     *
     * @return the Status object for the game
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * What is at this square?
     *
     * @param row row number of square
     * @param col column number of square
     * @return the player (or {@link Mole_status#NONE}) at the given location
     */
    public Mole_status getContents(int row, int col) {
        return this.board[row][col];
    }

    public void setContents(int num, Mole_status stat) {
        int counter = 0;
        while (counter < client.getRow()) {
            if (num < ((counter + 1) * client.getCol())) {
                this.board[counter][num % (client.getCol())] = stat;
                break;
            } else {
                counter++;
            }
        }
        this.alertObservers();
    }

    public void reset () {
        for (int col = 0; col < COLS; col++) {
            for (int row = 0; row < ROWS; row++) {
                this.board[row][col] = Mole_status.NONE;
            }
        }
    }

    /**
     * Called when the game has been won by this player.
     */
    public void gameWon() {
        this.status = Status.I_WON;
        alertObservers();
    }

    /**
     * Called when the game has been won by the other player.
     */
    public void gameLost() {
        this.status = Status.I_LOST;
        alertObservers();
    }

    /**
     * Called when the game has been tied.
     */
    public void gameTied() {
        this.status = Status.TIE;
        alertObservers();
    }

    /**
     * The user they may close at any time
     */
    public void close() {
        alertObservers();
    }

}
