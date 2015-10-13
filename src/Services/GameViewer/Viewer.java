package Services.GameViewer;

import AcademicsInterface.IViewer;
import AcademicsInterface.ViewedPlayers;
import Common.LogManager;
import Services.Logs.LogType;
import TournamentServer.GameManagerChild;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

/**
 * Created by steveb on 8/08/2015.
 *
 * Modified by Nick Sifniotis 13/10/2015
 *
 */
public class Viewer implements IViewer
{
    private Group root = new Group();
    private Group board;
    private Group pieces;

    /* board layout */
    private static final double BOARD_SCALE = 1.0; //4.0/3.0;
    private static final int GAME_SIDE =  (int) (400*BOARD_SCALE);
    private static final int SQUARE_SIDE = (int) (15*BOARD_SCALE);
    private static final int BOARD_SIDE = 20*SQUARE_SIDE;
    private static final int BOARD_MARGIN = (GAME_SIDE-BOARD_SIDE)/2;


    /** describe a board position */
    static class Position {
        int x, y;

        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /* a description of each of the blokus omino shapes in terms of a 5x5 grid with origin at 0,1 */
    static final int[] SHAPE_A = {0};
    static final int[] SHAPE_B = {0,5};
    static final int[] SHAPE_C = {0,5,10};
    static final int[] SHAPE_D = {0,5,6};
    static final int[] SHAPE_E = {0,5,10,15};
    static final int[] SHAPE_F = {0,5,10,-11};
    static final int[] SHAPE_G = {0,5,6,10};
    static final int[] SHAPE_H = {0,1,5,6};
    static final int[] SHAPE_I = {0,1,6,7};
    static final int[] SHAPE_J = {0,5,10,15,20};
    static final int[] SHAPE_K = {0,5,10,15,-16};
    static final int[] SHAPE_L = {0,5,10,-11,-16};
    static final int[] SHAPE_M = {0,5,-6,10,-11};
    static final int[] SHAPE_N = {0,1,6,10,11};
    static final int[] SHAPE_O = {0,5,6,10,15};
    static final int[] SHAPE_P = {0,5,10,-11,11};
    static final int[] SHAPE_Q = {0,5,10,11,12};
    static final int[] SHAPE_R = {0,1,6,7,12};
    static final int[] SHAPE_S = {0,5,6,7,12};
    static final int[] SHAPE_T = {0,5,6,7,11};
    static final int[] SHAPE_U = {0,5,-6,6,10};

    /** An enumeration representing the shape of each of the blokus ominos and their symmetries */
    enum Omnio {
        A(SHAPE_A),
        B(SHAPE_B),
        C(SHAPE_C),
        D(SHAPE_D),
        E(SHAPE_E),
        F(SHAPE_F),
        G(SHAPE_G),
        H(SHAPE_H),
        I(SHAPE_I),
        J(SHAPE_J),
        K(SHAPE_K),
        L(SHAPE_L),
        M(SHAPE_M),
        N(SHAPE_N),
        O(SHAPE_O),
        P(SHAPE_P),
        Q(SHAPE_Q),
        R(SHAPE_R),
        S(SHAPE_S),
        T(SHAPE_T),
        U(SHAPE_U);

        int[] shape;
        Omnio(int[] shape) {
            this.shape = shape;
        }

        Position[] getSquares(int orientation, boolean flipped) {
            Position[] rtn = new Position[shape.length];
            for(int i = 0; i < shape.length; i++)
                rtn[i] = reorient(shape[i], orientation, flipped);
            return rtn;
        }

        private Position reorient(int position, int orientation, boolean flip) {
            Position rtn = new Position(0,0);
            int dx = (flip ? -1 : 1) * position % 5;
            int dy = Math.abs(position) / 5;
            switch (orientation) {
                case 1:  rtn.x -= dy; rtn.y += dx; break;
                case 2:  rtn.x -= dx; rtn.y -= dy; break;
                case 3:  rtn.x += dy; rtn.y -= dx; break;
                default: rtn.x += dx; rtn.y += dy; break;
            }
            return rtn;
        }
    }

    /** The three blokus players */
    enum Player {
        B, Y, R, G;
    }

    /**
     * This class represents a blokus piece as a group of squares
     */
    class Piece extends Group {
        private Omnio omino;
        private int orientation;
        private boolean flip;
        private Position position;

        public Piece(String state, Player player) {
            initializeState(state);

            /* add squares to this group */
            for(Position sq : getSquarePositions())
                getChildren().add(getSquare(sq, player));

            /* place this group */
            setLayoutX(BOARD_MARGIN + (position.x * SQUARE_SIDE));
            setLayoutY(BOARD_MARGIN + (position.y * SQUARE_SIDE));
        }

        /* indices for the four characters in a game state string */
        private static final int CHAR_OMINO = 0;
        private static final int CHAR_ORIENT = 1;
        private static final int CHAR_POSX = 2;
        private static final int CHAR_POSY = 3;

        /**
         * Initialize a piece according to an input game state string
         * @param state The state of the piece expressed as a four character string
         */
        private void initializeState(String state) {
            omino = Omnio.valueOf(state.substring(CHAR_OMINO, CHAR_OMINO + 1));

            int o = state.charAt(CHAR_ORIENT) - 'A';
            orientation = o % 4;
            flip = (o / 4) == 1;

            position = new Position((int) state.charAt(CHAR_POSX) - 'A', (int) (state.charAt(CHAR_POSY) - 'A'));
        }

        /**
         * Return a JavaFX square corresponding to a given relative position in an omino
         * @param relativePos which square we want
         * @param player which player's piece is it (and thus which color)
         * @return a JavaFX group rendering the square
         */
        private Group getSquare(Position relativePos, Player player) {
            int x = relativePos.x;
            int y = relativePos.y;

            Group rtn = new Group();
            Rectangle r1 = new Rectangle((x * SQUARE_SIDE) + 1, (y * SQUARE_SIDE) + 1, SQUARE_SIDE - 2, SQUARE_SIDE - 2);
            r1.setFill(getColor(player));
            r1.setOpacity(0.8);

            rtn.getChildren().add(r1);
            Rectangle r2 = new Rectangle((x * SQUARE_SIDE), (y * SQUARE_SIDE), SQUARE_SIDE - 2, SQUARE_SIDE - 2);
            r2.setFill(Color.TRANSPARENT);
            r2.setStroke(getColor(player));
            rtn.getChildren().add(r2);

            return rtn;
        }

        /**
         * Return the positions of each of the squares that comprise this piece in its given orientation
         * @return An array of positions for each of the squares in this piece in its given orientation
         */
        Position[] getSquarePositions() {
            return omino.getSquares(orientation, flip);
        }
    }

    /**
     * Return an array of pieces defined by the game state
     * @param gameState A string describing the game state
     * @return An array of pieces
     */
    Piece[] fromString(String gameState) {
        String in = gameState.replaceAll(" ","");
        Piece[] pieces = new Piece[in.length()/4];
        for (int i = 0, j = 0; i < pieces.length; i++) {
            String p = in.substring((4*i),(4*(i+1)));
            if (!p.equals("....")) {
                pieces[j++] = new Piece(p, Player.values()[i%4]);
            }
        }
        return pieces;
    }

    /**
     * Return the color for a given player
     * @param player One of the four standard blokus players
     * @return A JavaFX color for the player
     */
    private Color getColor(Player player) {
        switch (player) {
            case B: return Color.BLUE;
            case G: return Color.GREEN;
            case Y: return Color.GOLD;
        }
        return Color.RED;
    }

    /**
     * Draw the board
     */
    private void drawBoard() {
        board = new Group();
        Color lineColor = Color.color(.95, .95, .95);//javafx.scene.paint.Player.LIGHTGRAY;
        Rectangle boarder = new Rectangle(BOARD_MARGIN, BOARD_MARGIN, BOARD_SIDE, BOARD_SIDE);
        boarder.setFill(Color.TRANSPARENT);
        boarder.setStroke(Color.LIGHTGRAY);
        for (int i = 0; i < 20; i++) {
            Line l = new Line(BOARD_MARGIN+(i*(BOARD_SIDE/20)), BOARD_MARGIN, BOARD_MARGIN+(i*(BOARD_SIDE/20)), BOARD_MARGIN+BOARD_SIDE);
            l.setStroke(lineColor);
            board.getChildren().add(l);
            l = new Line(BOARD_MARGIN, BOARD_MARGIN+(i*(BOARD_SIDE/20)), BOARD_MARGIN+BOARD_SIDE, BOARD_MARGIN+(i*(BOARD_SIDE/20)));
            l.setStroke(lineColor);
            board.getChildren().add(l);
        }
        board.getChildren().add(boarder);
    }

    /**
     * Given a game state, draw the relevant pieces, replacing/destroying existing pieces if they exist
     * @param gameState The game state describing the pieces
     */
    private void drawPieces(String gameState)
    {
        if (gameState == null)
            return;

        if (pieces != null)
            root.getChildren().remove(pieces);
        pieces = new Group();

        for (Piece p : fromString(gameState)) {
            if (p != null) pieces.getChildren().add(p);
        }
        root.getChildren().add(pieces);
    }


    @Override
    public void InitialiseViewer(javafx.stage.Stage stage)
    {
        stage.setTitle("BlokGame");
        Scene scene = new Scene(root, GAME_SIDE, GAME_SIDE);
        stage.setScene(scene);
    }

    @Override
    public void NewGame(Object gameState, ViewedPlayers[] players)
    {
        drawBoard();
        root.getChildren().add(board);
        drawPieces((String) gameState);
    }

    @Override
    public void Update(Object gameState)
    {
        LogManager.Log(LogType.ERROR, "Within Viewer update");

        if (gameState == null)
            return;

        LogManager.Log(LogType.ERROR, "game.currentstate is : " + gameState.getClass().getName());

        drawPieces((String)(gameState));

//        try
//        {
//            Thread.sleep(500);
//        }
//        catch (Exception e)
//        {
//
//        }
    }

}