package client.gui;

import common.WAMGame;
import client.WAMClient;
import common.Observer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

/**
 * A JavaFX GUI for the networked Whack-A-Mole game, which handles
 * the view part of the model-view-controller template.
 *
 * @author Soumya Dayal
 * username: sd9829
 * @author Kelsey Donovan
 * username: ksd4250
 */


public class WAMGUI extends Application implements Observer<WAMGame> {

    private WAMGame board;
    private BorderPane borderPane;
    public int COLL;
    public int ROL;
    private WAMClient client;
    private Image empty = new Image(getClass().getResourceAsStream("white.png"));
    private Image mole = new Image(getClass().getResourceAsStream("mole.png"));
    private Label time = new Label();
    private Label score = new Label();
    private Label status = new Label();
    Button id = new Button();


    /**
     * the init method of a java FX class.
     */
    @Override
    public void init() {
        try {

            // get the command line args
            List<String> args = getParameters().getRaw();

            // get host info and port from command line
            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));

            try {
                this.client = new WAMClient(host, port);
                this.board = client.getBoard();
                this.COLL = this.board.getCOLS();
                this.ROL = this.board.getROWS();
                this.board.addObserver(this);
            } catch (NumberFormatException e) {
                System.err.println(e);
                throw new RuntimeException(e);
            }
        }
        catch (Exception ee) {
            System.err.println(ee);

        }
    }

    /**
     * method constructs the layout for the game.
     *
     * @param stage container (window) in which to render the GUI
     * @throws Exception if there is a problem
     */
    public void start( Stage stage ) throws Exception {
        this.borderPane = new BorderPane();
        GridPane gridPane = new GridPane();
        for (int i = 0; i < COLL; i++) {
            for (int inx = 0; inx < ROL; inx++) {
                Button button = new Button();
                ImageView empty = new ImageView(this.empty);
                empty.setFitHeight(100.00);
                empty.setFitWidth(100.00);
                button.setGraphic(empty);
                gridPane.add(button, i, inx);
                button.setOnAction(actionEvent -> {id = button;
                update(board);});
            }
        }

        HBox hBox = new HBox();
        hBox.getChildren().add(score);
        score.setText("Score: " + client.getScore());
        hBox.getChildren().add(status);
        status.setText("Status: " + board.getStatus());
        borderPane.setBottom(hBox);
        hBox.setVisible(true);
        borderPane.setCenter(gridPane);
        gridPane.setVisible(true);
        stage.setScene(new Scene(borderPane));
        stage.show();
        this.client.startListener();
    }

    /**
     * GUI is closing, so the method closes the network connection.
     * Server gets the message.
     */
    @Override
    public void stop() {
        this.client.close();
        this.board.close();
    }

    /**
     * method which makes GUI updates.
     */
    private void refresh(WAMGame board) {
        for (int i = 0; i < ROL; i++) {
            for (int j = 0; j < COLL; j++) {
                if (board.getContents(i,j) == WAMGame.Mole_status.NONE ) {
                    ImageView empty = new ImageView(this.empty);
                    empty.setFitHeight(100.00);
                    GridPane gridPane = (GridPane) borderPane.getCenter();
                    List child = gridPane.getChildren();
                    ((Button) child.get(i * ROL + j)).setGraphic(empty);
                }
                if (board.getContents(i,j) == WAMGame.Mole_status.MOLE_DOWN ) {
                    ImageView empty = new ImageView(this.empty);
                    empty.setFitHeight(100.00);
                    empty.setFitWidth(100.00);
                    GridPane gridPane = (GridPane) borderPane.getCenter();
                    List child = gridPane.getChildren();
                    ((Button) child.get(i * ROL + j)).setGraphic(empty);
                }
                if (board.getContents(i,j) == WAMGame.Mole_status.MOLE_UP ) {
                    ImageView mole = new ImageView(this.mole);
                    mole.setFitHeight(100.00);
                    mole.setFitWidth(100.00);
                    GridPane gridPane = (GridPane) borderPane.getCenter();
                    List child = gridPane.getChildren();
                    ((Button) child.get(i * ROL + j)).setGraphic(mole);
                }
            }
        }
    }

    /**
     * Called by the model, common.WAMGame, whenever there is a state change
     * that needs to be updated by the GUI.
     *
     * @param moleBoard
     */
    @Override
    public void update(WAMGame moleBoard) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh(moleBoard);
        }
        else {
            Platform.runLater( () -> this.refresh(moleBoard) );
        }
    }

    /**
     * The main method expects the host and port.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Wrong values");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
