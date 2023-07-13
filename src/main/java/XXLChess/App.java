package XXLChess;

import XXLChess.Piece.King;
import XXLChess.Piece.Piece;
import XXLChess.AI;
import processing.core.PApplet;
import processing.data.JSONObject;
import processing.event.MouseEvent;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The App class represents the main application for the XXLChess game.
 * It extends the PApplet class from the Processing library and provides methods for setting up the game window and handling game logic.
 */
public class App extends PApplet {

    public static final int SPRITESIZE = 480;
    public static final int CELLSIZE = 48;
    public static final int SIDEBAR = 120;
    public static final int BOARD_WIDTH = 14;
    public static int WIDTH = CELLSIZE*BOARD_WIDTH+SIDEBAR;
    public static int HEIGHT = BOARD_WIDTH*CELLSIZE;
    public static final int FPS = 60;
    public String configPath;
    private Board board;
    private Tile selectedTile;
    private int frame = 0;
    private int whiteFrame = 0;
    private int blackFrame = 0;
    private int whiteTimeRemaining;
    private int blackTimeRemaining;
    private boolean whiteTurn;
    private int incrementSeconds;
    private int baseTimeSeconds;
    public boolean gameOver = false;
    private String statusMessage = "";
    private int checkmarkCounter = 0;
    public boolean aiEnabled = false;
    private String playerColour = "white";
    public boolean illegalMoveAttempted = false;
    public boolean checkState = false;
    private int flashCount = 0;
    public boolean flashState = false;
    public boolean checkMateStatus=false;
    private int aiActionCounter = 0;
    private boolean aiActionTriggered = false;

    /**
     * Constructs an App object with the default configuration path.
     */
    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * This method is responsible for setting up the game environment and initializing necessary variables.
     * It sets the frame rate to a specified value (FPS).
     * It loads the configuration file containing game settings.
     * It initializes the game board and sets up the initial layout of the chess pieces based on the configuration.
     * It sets the movement parameters for the chess pieces, including the speed of movement and maximum movement time.
     * It sets the time controls for the players based on the configuration, including base time and time increment.
     * It initializes the remaining time for both white and black players.
     * It determines which player's turn it is based on the player's chosen color.
     * @see #frameRate(float)
     * @see #loadJSONObject(File)
     * @see Board#setupBoard(String)
     * @see Board#setMovementParameters(double, double)
     */
    public void setup() {
        frameRate(FPS);

        // Load images during setup

        // PImage spr = loadImage("src/main/resources/XXLChess/"+...);

        // load config
        JSONObject conf = loadJSONObject(new File(this.configPath));
        if (conf == null) {
            System.out.println("Failed to load config.");
        } else {
            System.out.println("Config loaded successfully.");
        }
        frame= 0;
        board = new Board(this);
        board.setupBoard(conf.getString("layout"));
        double pieceMovementSpeed = conf.getDouble("piece_movement_speed");
        double maxMovementTime = conf.getDouble("max_movement_time");
        board.setMovementParameters(pieceMovementSpeed, maxMovementTime);
        JSONObject timeControls = conf.getJSONObject("time_controls");
        JSONObject playerTimeControls = timeControls.getJSONObject("player");
        this.baseTimeSeconds = playerTimeControls.getInt("seconds");
        this.incrementSeconds = playerTimeControls.getInt("increment");
        this.whiteTimeRemaining = baseTimeSeconds;
        this.blackTimeRemaining = baseTimeSeconds;
        whiteTurn = playerColour.equalsIgnoreCase("white");
        playerColour = conf.getString("player_colour");
    }

    /**
     * This method is responsible for handling keyboard inputs during the game.
     * If the 'r' or 'R' key is pressed, it restarts the game by calling the setup() method,
     * resumes the draw() loop, resets the game over status, resets the remaining time for both players,
     * resets the frame count for white and black players, sets the turn to start with the white player,
     * and disables AI mode.
     * If the 'a' or 'A' key is pressed, it toggles the AI mode on or off.
     * If AI mode is enabled, it resets the game by calling the setup() method, resumes the draw() loop,
     * resets the game over status, resets the remaining time for both players,
     * resets the frame count for white and black players, and sets the turn to start with the white player.
     * If the 'e' or 'E' key is pressed (Escape key), it ends the game by setting the game over status to true.
     * It determines the winner based on the current turn and displays the appropriate message on the screen.
     * @see #setup()
     * @see #loop()
     * @see #fill(int)
     * @see #textSize(float)
     * @see #text(String, float, float)
     */
    public void keyPressed(){
        if (key == 'r' || key == 'R') {
            setup(); // Restart the game
            loop(); // Resume draw() loop
            gameOver = false; // Reset game over status
            whiteTimeRemaining = baseTimeSeconds;
            blackTimeRemaining = baseTimeSeconds;
            whiteFrame = 0;
            blackFrame = 0;
            whiteTurn = true; // Reset to start with the white player
            aiEnabled = false; // Reset AI mode to off
        }
        if (key == 'a' || key == 'A') {
            aiEnabled = !aiEnabled; // Toggle AI mode

            // Optionally, you can also reset the game here
            if (aiEnabled) {
                setup();
                loop();
                gameOver = false;
                whiteTimeRemaining = baseTimeSeconds;
                blackTimeRemaining = baseTimeSeconds;
                whiteFrame = 0;
                blackFrame = 0;
                whiteTurn = true; // Reset to start with the white player
            }
        }
        if (key == 'e' || key == 'E') { // Escape key
            gameOver = true; // End the game
            String winner = whiteTurn ? "Black" : "White"; // The other player wins
            // Show message and stop the game
            fill(255, 0, 0); // Red color for message
            textSize(14); // Smaller font size
            text("You resigned.", WIDTH - SIDEBAR / 2, HEIGHT / 2 - 15);
            textSize(20); // Regular font size
            text(winner + " wins.", WIDTH - SIDEBAR / 2, HEIGHT / 2 + 15);
            noLoop(); // Stop draw() from looping
        }
    }


    /**
     * Receive key released signal from the keyboard.
     */
    public void keyReleased(){

    }

    /**
     * This method is an overridden implementation of the mouseClicked() method from the MouseListener interface.
     * It is responsible for handling mouse click events during the game.
     * If the game is already over, the method returns immediately.
     * If a tile on the board is clicked, the method performs the following actions:
     * Converts the mouse coordinates to board coordinates.
     * Retrieves the clicked tile on the board.
     * If no tile was previously selected, sets the clicked tile as the selected tile.
     * If the selected tile is not null and contains a piece of the correct color, it highlights the tile and highlights the legal moves for the selected piece.
     * Otherwise, it sets the selected tile back to null.
     * If a tile was previously selected, retrieves the piece on the selected tile.
     * If the selected piece is not null, it checks if the clicked tile is a valid move for the piece.
     * If the clicked tile is a valid move, it moves the piece from the selected tile to the clicked tile, updates the remaining time for the current player, and switches the turn to the other player.
     * If the player tries to move a piece while in check, it sets the illegalMoveAttempted flag to true.
     * Finally, it sets the selected tile back to null and de-highlights all tiles on the board.
     * @param e The MouseEvent object representing the mouse click event.
     * @see Board#getTileAt(int, int)
     * @see Tile#drawHighlight(int, int, int)
     * @see Board#highlightLegalMoves(Piece, List)
     * @see Piece#safeMove(List, int, int, Board)
     * @see Piece#getLegalMoves(int, int, Board)
     * @see Board#movePiece(int, int, int, int)
     * @see Tile#getPiece()
     * @see Piece#setMoved(boolean)
     * @see Board#deHighlightTiles()
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver) {
            return;
        }
        // Convert the mouse coordinates to board coordinates
        int x = e.getX() / CELLSIZE;
        int y = e.getY() / CELLSIZE;
        // Get the clicked tile on the board
        Tile clickedTile = board.getTileAt(x, y);
        if (selectedTile == null) {
            System.out.println("tile clicked");
            selectedTile = clickedTile;
            if (selectedTile != null) {
                Piece piece = selectedTile.getPiece();

                // Make sure the selected piece is of the correct color
                if (piece != null && piece.getColor() == (whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)) {
                    if(!aiEnabled || (aiEnabled && ((whiteTurn && piece.getColor() == PieceColor.WHITE) || (!whiteTurn && piece.getColor() == PieceColor.BLACK)))) {
                        clickedTile.drawHighlight(105, 138, 76);
                        board.highlightLegalMoves(piece, piece.safeMove(piece.getLegalMoves(x, y, board), piece.getX(), piece.getY(), board));
                    }
                }
                else {
                selectedTile = null;
            }
            }
        } else {
            Piece selectedPiece = selectedTile.getPiece();
            if (selectedPiece != null) { // Check if the selectedPiece is not null
                //List<int[]> legalMoves = selectedPiece.getLegalMoves(selectedTile.getX(), selectedTile.getY(), board);
                List<int[]> legalMoves = selectedPiece.safeMove(selectedPiece.getLegalMoves(selectedTile.getX(), selectedTile.getY(), board), selectedPiece.getX(), selectedPiece.getY(), board);
                if(clickedTile != null) {
                int[] targetArray = new int[]{clickedTile.getX(), clickedTile.getY()};
                boolean containsArray = legalMoves.stream()
                        .anyMatch(move -> Arrays.equals(move, targetArray));
                if (containsArray) {
                    System.out.println("bb");
                    System.out.println(clickedTile.getX());
                    System.out.println(clickedTile.getY());
                    board.getTileAt(selectedTile.getX(), selectedTile.getY()).getPiece().setMoved(true);
                    board.movePiece(selectedTile.getX(), selectedTile.getY(), clickedTile.getX(), clickedTile.getY());
                    if (whiteTurn) {
                        whiteTimeRemaining += incrementSeconds;
                    } else {
                        blackTimeRemaining += incrementSeconds;
                    }
                    whiteTurn = !whiteTurn;
                }else {
                    // If the player tries to move a piece while in check
                    if (checkState && selectedPiece.getColor() == (whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)) {
                        illegalMoveAttempted = true;
                    }
                }
            }
            selectedTile = null;
            board.deHighlightTiles();
        }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    /**
     * This method is an overridden implementation of the draw() method from the PApplet class.
     * It is responsible for rendering the game graphics and updating the game state on each frame.
     * Increments the frame count.
     * Draws the game board and updates the movement of any moving piece on the board.
     * Draws the moving piece on the board.
     * Draws the sidebar on the right side of the board.
     * Displays the remaining time for the white and black players in the sidebar.
     * If the player's color is white and AI mode is enabled, triggers the execution of AI action.
     * If the player's color is black and AI mode is enabled, triggers the execution of AI action.
     * Checks if the time for either player has run out and handles the game over scenario accordingly.
     * Checks if the game is in a checkmate state and handles the game over scenario accordingly.
     * If the game is in a check state, displays a "Check!" message on the screen.
     * If an illegal move is attempted, displays a warning message and flashes the board.
     * Displays the status message in the center of the sidebar.
     * @see PApplet#draw()
     * @see Board#draw()
     * @see Board#updateMovingPiece()
     * @see Board#drawMovingPiece()
     * @see PApplet#fill(float, float, float)
     * @see PApplet#rect(float, float, float, float)
     * @see PApplet#textAlign(int, int)
     * @see PApplet#textSize(float)
     * @see PApplet#text(String, float, float)
     * @see #executeAIActionAfterFrames()
     * @see #checkTimeUp(int, int)
     * @see #checkCheckmate()
     */
    @Override
    public void draw() {
        frame++;
        board.draw();
        board.updateMovingPiece();
        board.drawMovingPiece();
        fill(180,180,180);
        rect(WIDTH - SIDEBAR, 0,SIDEBAR,CELLSIZE * BOARD_WIDTH);
        fill(255);  // 白色
        textAlign(CENTER, CENTER);
        textSize(20);
        int whiteLeftTime = whiteTimeRemaining - (whiteFrame/60);
        int blackLeftTime = blackTimeRemaining - (blackFrame/60) ;
        if(whiteTurn)
        {
            whiteFrame++;
            whiteLeftTime = whiteTimeRemaining - (whiteFrame/60);
        }else {
            blackFrame++;
            blackLeftTime = blackTimeRemaining - (blackFrame/60);
        }
        text(formatTime(whiteLeftTime), WIDTH - SIDEBAR / 2, 3*(HEIGHT / 4));
        text(formatTime(blackLeftTime), WIDTH - SIDEBAR / 2, HEIGHT / 4);

        if(playerColour.equalsIgnoreCase("white")){
        if (aiEnabled && !whiteTurn && !aiActionTriggered) {
            executeAIActionAfterFrames();
        }
        }else{
            if (aiEnabled && whiteTurn && !aiActionTriggered) {
                executeAIActionAfterFrames();
            }
        }
        checkTimeUp(whiteLeftTime, blackLeftTime);
        checkCheckmate();
        if(!checkMateStatus){
        if (checkState) {
            fill(255, 0, 0); // Red color for message
            textSize(20); // Smaller font size
            text("Check!", WIDTH - SIDEBAR / 2, HEIGHT / 2 - 30);
        }
        }
        if (illegalMoveAttempted) {
            fill(255, 0, 0); // Red color for message
            textSize(14); // Smaller font size
            text("You must", WIDTH - SIDEBAR / 2, HEIGHT / 2 - 15);
            textSize(14); // Smaller font size
            text("defend your king!", WIDTH - SIDEBAR / 2, HEIGHT / 2);
            flashCount++;
            if (flashCount > 180) { // Flash for 3 seconds (assuming 60 frames per second)
                illegalMoveAttempted = false;
                flashCount = 0;
            }
            flashState = flashCount / 30 % 2 == 0; // Toggle flashState every half second
        } else {
            flashState = true; // Always show the highlight when not flashing
        }
        fill(255);  // 白色
        textAlign(CENTER, CENTER);
        textSize(14);  // Smaller font size
        text(statusMessage, WIDTH - SIDEBAR / 2, HEIGHT / 2 - 15);

    }

    /**
     * This private method is responsible for executing the AI's action after a certain number of frames.
     * It increments a counter to track the number of frames elapsed.
     * Once the counter reaches a specified threshold (120 frames), it performs the following actions:
     * Instantiates an AI object with the appropriate color based on the player's color.
     * Invokes the AI's getMove() method to calculate the best move.
     * Extracts the start and end coordinates of the best move.
     * Executes the best move on the game board by calling the movePiece() method.
     * Switches the turns between white and black players.
     * Resets the counter back to 0 for the next AI action.
     * @see AI
     * @see AI#getMove(Board)
     * @see Board#movePiece(int, int, int, int)
     * @return None
     */
    private void executeAIActionAfterFrames() {
        // Increment counter
        aiActionCounter++;

        // After 120 frames, execute AI action
        if(aiActionCounter >= 120) {
            // Instantiate AI if necessary
            PieceColor aiColor = playerColour.equalsIgnoreCase("white") ? PieceColor.BLACK : PieceColor.WHITE;
            AI ai = new AI(aiColor);

            // Calculate best move
            int[] bestMove = ai.getMove(board);
            int startX = bestMove[0];
            int startY = bestMove[1];
            int endX = bestMove[2];
            int endY = bestMove[3];

            // Execute best move
            board.movePiece(startX, startY, endX, endY);

            // Switch turns
            whiteTurn = !whiteTurn;

            // Reset counter
            aiActionCounter = 0;
        }

    }

    /**
     * This private method is responsible for formatting a given time in seconds into a string representation of minutes and seconds.
     * It takes the input time in seconds and performs the following operations:
     * Calculates the number of minutes by dividing the timeInSeconds by 60.
     * Calculates the number of seconds by taking the remainder of the timeInSeconds divided by 60.
     * Formats the minutes and seconds into a string using the format "%d:%02d", where "%d" represents the minutes and "%02d" represents the seconds with leading zeros if necessary.
     * Returns the formatted time as a string.
     * @param timeInSeconds The time duration in seconds.
     * @return The formatted time as a string representation in the format "MM:SS".
     */
    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    private void checkTimeUp(int whiteTime, int blackTime) {
        if (whiteTime <= 0) {
            // Show message and stop the game
            fill(255, 0, 0); // Red color for message
            textSize(14); // Smaller font size
            text("You lost on time", WIDTH - SIDEBAR / 2, HEIGHT / 2 - 15);
            textSize(20); // Regular font size
            text("Black wins.", WIDTH - SIDEBAR / 2, HEIGHT / 2 + 15);
            noLoop(); // Stop draw() from looping
            gameOver = true;
        } else if (blackTime <= 0) {
            // Show message and stop the game
            fill(255, 0, 0); // Red color for message
            textSize(14); // Smaller font size
            text("You won on time", WIDTH - SIDEBAR / 2, HEIGHT / 2 - 15);
            textSize(20); // Regular font size
            text("White wins.", WIDTH - SIDEBAR / 2, HEIGHT / 2 + 15);
            noLoop(); // Stop draw() from looping
            gameOver = true;
        }
    }

    /**
     * This private method is responsible for checking if either player's time has run out and handling the game over scenario accordingly.
     * It takes the remaining time for the white player (whiteTime) and the remaining time for the black player (blackTime) as parameters.
     * It performs the following actions:
     * If the white player's time has reached or fallen below 0, it displays a "You lost on time" message on the screen,
     * declares black as the winner, stops the draw() loop, and sets the game over status to true.
     * If the black player's time has reached or fallen below 0, it displays a "You won on time" message on the screen,
     * declares white as the winner, stops the draw() loop, and sets the game over status to true.
     * @see PApplet#fill(float, float, float)
     * @see PApplet#textSize(float)
     * @see PApplet#text(String, float, float)
     * @see PApplet#noLoop()
     */
    public void checkCheckmate() {
        PieceColor currentTurn = whiteTurn ? PieceColor.WHITE : PieceColor.BLACK;
        //PieceColor opponentColor = whiteTurn ? PieceColor.BLACK : PieceColor.WHITE;
        checkMateStatus=board.checkmate(board, currentTurn);
        if (checkMateStatus) {
            // Increase frameCounter
            checkmarkCounter++;

            if(checkmarkCounter >= 10) {
                // Show message and stop the game
                fill(255, 0, 0); // Red color for message
                textSize(14); // Smaller font size
                text("You won", WIDTH - SIDEBAR / 2, HEIGHT / 2 - 30);
                textSize(14); // Smaller font size
                text("by checkmate", WIDTH - SIDEBAR / 2, HEIGHT / 2 - 15);
                textSize(20); // Regular font size
                text((currentTurn == PieceColor.WHITE ? "Black": "White" ) + " wins.", WIDTH - SIDEBAR / 2, HEIGHT / 2 + 15);
                noLoop(); // Stop draw() from looping
                gameOver = true;
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("XXLChess.App");
    }

}
