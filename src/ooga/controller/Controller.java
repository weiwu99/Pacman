package ooga.controller;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import java.io.*;
import java.io.File;
import java.io.FileReader;

import javafx.stage.Stage;
import java.lang.Integer;
import ooga.models.creatures.cpuControl.CPUCreature;
import ooga.models.game.Board;
import ooga.models.game.CollisionManager;
import ooga.models.game.Game;
import ooga.view.gameDisplay.center.BoardView;
import ooga.view.home.HomeScreen;
import ooga.view.popups.ErrorView;
import org.json.simple.parser.ParseException;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import ooga.view.gameDisplay.gamePieces.MovingPiece;

public class Controller implements BasicController, ViewerControllerInterface {

    private static final String LANGUAGES = "languages";
    private static final char COMMA = ',';
    private static final String SIZE = "CELL_SIZE";
    private static final String CSS_FILE_NAME = "CSS_FILE_NAME";
    private static final String LANGUAGE = "LANGUAGE";
    private static final String WALL = "WALL";
    private static final String SCOREBOOSTER = "SCOREBOOSTER";
    private static final String STATECHANGER = "STATECHANGER";
    private static final String SCOREMULTIPLIER = "SCOREMULTIPLIER";
    private static final String PORTAL = "PORTAL";
    private static final String GHOSTSLOWER = "GHOSTSLOWER";
    private static final String EXTRALIFE = "EXTRALIFE";
    private static final String EMPTY = "EMPTY";
    private static final String INVINCIBILITY = "INVINCIBILITY";
    private static final String SPEEDCUTTER = "SPEEDCUTTER";
    private static final String WINLEVEL = "WINLEVEL";
    private static final int ZERO = 0;
    private static final int ONE_INDEX = 1;
    private static final int SECOND_INDEX = 2;
    private static final int THIRD_INDEX = 3;
    private static final int SIXTH_INDEX = 6;
    private static final int SEVENTH_INDEX = 7;
    private static final int EIGHTH_INDEX = 8;
    private static final int NINTH = 9;
    private static final int TENTH = 10;
    private static final int ELEVENTH = 11;
    private static final int TWELVE = 12;
    private static final int PACMAN_INDEX = 4;
    private static final String PACMAN = "PACMAN";
    private static final String CPUGHOST = "CPUGHOST";
    private static final int ENEMY_INDEX = 5;
    private static final CPUCreature NULL_CREATURE = null;
    private static final String TIMER = "TIMER";
    private static final String GAME_TITLE = "GAME_TITLE";
    private final double ANIMATION_SPEED = 0.3;
    private final int HIGH_SCORE_VALS = TENTH;
    private final int WIDTH = 1200;
    private final int HEIGHT = 700;
    public final int CELL_SIZE = 25;
    private int cellSize;
    private static final String CSS_FILE_EXTENSION = "%s.css";
    private final Dimension DEFAULT_SIZE = new Dimension(WIDTH, HEIGHT);
    public static final String TITLE = "Start Screen";
    private final String IOE_EXCEPTION_CSV = "IOE exceptions for CSV file path. Please check your CSV file";
    private final String IOE_EXCEPTION = "IOE exceptions";
    private final String NULL_POINTER_EXCEPTION = "Null pointer exception controller";
    private final String PARSE_EXCEPTION = "Parse exception!";
    private final String CLASS_NOT_FOUND = "Class not found!";
    private final String INVOCATION_TARGET = "Invocation target error!";
    private final String NO_SUCH_METHOD = "There is no such method! ";
    private final String INSTANTIATION_EXCEPTION = "Can't instantiate!";
    private final String ILLEGAL_ACCESS = "Access illegal! ";
    private final String DEFAULT_TITLE = "Game";
    private static final int MILLION = 1000000;
    private static final int ONE_HUNDRED = 100;
    private static final int FIVE_HUNDRED = 500;
    private static int DEFAULT_CELL_SIZE = 24;
    private final String[] BLANK_ENTRY = new String[]{"", "-1"};

    private Game myGame;
    private Board myBoard;
    private BoardView myBoardView;
    private double animationSpeed;
    private HomeScreen myStartScreen;
    private CollisionManager collisionManager;
    private Map<Integer, String> creatureMap;
    private JSONReader myReader;
    private CSVWriter myCSVWriter;
    private Map<Integer, String> gameObjectMap;
    private List<List<String>> stringBoard;
    private Stage myStage;
    private String myUsername;
    private ErrorView myErrorView;
    private ResourceBundle myLanguages;
    private GameSettings myGameSettings;
    private String language;
    private String UILanguage;
    private String cssFileName;
    private String cssUIFileName;
    private GameController gameController;

    private final String LANGUAGE_RESOURCE_PACKAGE = "ooga.models.resources.";
    private final String DEFAULT_CSS_FILE = "Default.css";
    private final String DEFAULT_LANGUAGE = "ENGLISH";
    private final String DEFAULT_USERNAME = "Guest";
    private final String SCORE_PATH = "./data/highscores/HighScores.csv";

    /**
     * The constructor of the game controller that starts and controls the overall communication between the frontend and backend
     *
     * @param stage the Stage object for the view
     */
    public Controller(Stage stage){
        cssFileName = DEFAULT_CSS_FILE;
        myUsername = DEFAULT_USERNAME;
        myLanguages = ResourceBundle.getBundle(LANGUAGE_RESOURCE_PACKAGE + LANGUAGES);
        language = myLanguages.getString(DEFAULT_LANGUAGE);

        myStartScreen = new HomeScreen(stage, DEFAULT_SIZE.width, DEFAULT_SIZE.height, this);
        myErrorView = new ErrorView(DEFAULT_LANGUAGE);
        initializeStage(stage);
        initializeCSVIO();

        myUsername = DEFAULT_USERNAME;
        cellSize = DEFAULT_CELL_SIZE;
    }

    private void initializeStage(Stage stage) {
        animationSpeed = ANIMATION_SPEED;
        myStage = stage;
        myStage.setTitle(TITLE);
        myStage.setScene(myStartScreen.createScene());
        myStage.show();
    }

    /*
    Initialize the reader and writer for the CSV IO.
     */
    private void initializeCSVIO(){
        try {
            File scoreFile = new File(SCORE_PATH);
            myCSVWriter = new CSVWriter(new FileWriter(scoreFile, true), COMMA, CSVWriter.NO_QUOTE_CHARACTER,
                    CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                    CSVWriter.DEFAULT_LINE_END);
        }
        catch (IOException e){}
    }

    /**
     * Initialize a Pacman game
     *
     * @param path The directory of a layout file
     */
    public void initializeGame(String path) {
        try {
            myReader = new JSONReader(language, path);
            assembleBoards();

        }
        catch (ClassNotFoundException e) {myErrorView.showError(CLASS_NOT_FOUND);}
        catch (InvocationTargetException e) {myErrorView.showError(INVOCATION_TARGET);}
        catch (IllegalAccessException e) {myErrorView.showError(ILLEGAL_ACCESS);}
        catch (NoSuchMethodException e) {myErrorView.showError(NO_SUCH_METHOD);}
        catch (IOException e) {myErrorView.showError(IOE_EXCEPTION);}
        catch (InstantiationException e) {myErrorView.showError(INSTANTIATION_EXCEPTION);}
        catch (ParseException e) {myErrorView.showError(PARSE_EXCEPTION);}
    }

    private void assembleBoards() throws IOException, ParseException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        JSONContainer container = myReader.readJSONConfig();
        if (container != null && !container.isMissingContent()) {
            int numOfRows = container.getMyNumOfRows();
            int numOfCols = container.getMyNumOfCols();

            extractInfoFromContainer(container);
            constructBoard(numOfRows, numOfCols);

            myBoardView = new BoardView(this);
            initializeBoardView(numOfRows, numOfCols, gameObjectMap, stringBoard, myBoardView);
            myGame = new Game(myBoard, myBoard.getNumPickupsAtStart(), myBoard.getMyUser(), myBoard.getMyCPUCreatures(),
                    cellSize, myGameSettings.getGeneralSettings()); //TODO assigning pickups manually assign from file!!
            gameController = new GameController(myGame);
        }
    }

    /*
    Extract setting information from the Container to set up the game
     */
    private void extractInfoFromContainer(JSONContainer container) {
        myGameSettings = container.getMyGameSettings();
        stringBoard = container.getMyStringBoard();
        setupLanguage();
        setupCSSFile();
        setupCellSize();
    }

    private void setupCellSize() {
        setCellSize(Integer.parseInt(myGameSettings.getGeneralSettings().getOrDefault(SIZE, String.valueOf(DEFAULT_CELL_SIZE)).trim()));
    }

    private void setupCSSFile() {
        cssFileName = myGameSettings.getGeneralSettings().getOrDefault(CSS_FILE_NAME, DEFAULT_CSS_FILE).trim();
    }

    private void setupLanguage() {
        language = myLanguages.getString(myGameSettings.getGeneralSettings().getOrDefault(LANGUAGE, DEFAULT_LANGUAGE).trim());
    }

    /*
    Create objects required and construct the game board
     */
    private void constructBoard(int numOfRows, int numOfCols) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        gameObjectMap = createGameObjectMap();
        creatureMap = createCreatureMap();
        myBoard = new Board(numOfRows, numOfCols);
        initializeBoard(numOfRows, numOfCols, gameObjectMap, stringBoard);
    }

    //TODO: hardcoded, need to refactored - enum
    public Map<Integer,String> createGameObjectMap() {
         return Map.ofEntries(
                 Map.entry(ZERO, WALL),
                 Map.entry(ONE_INDEX, SCOREBOOSTER),
                 Map.entry(SECOND_INDEX, STATECHANGER),
                 Map.entry(THIRD_INDEX, SCOREMULTIPLIER),
                 Map.entry(SIXTH_INDEX, PORTAL),
                 Map.entry(SEVENTH_INDEX, GHOSTSLOWER),
                 Map.entry(EIGHTH_INDEX, EXTRALIFE),
                 Map.entry(NINTH, EMPTY),
                 Map.entry(TENTH, INVINCIBILITY),
                 Map.entry(ELEVENTH, SPEEDCUTTER),
                 Map.entry(TWELVE, WINLEVEL)
         );
    }

    public Map<Integer,String> createCreatureMap() {
        return Map.ofEntries(Map.entry(PACMAN_INDEX, PACMAN),Map.entry(ENEMY_INDEX, CPUGHOST));
    }

    /*
    Initialize all game objects within the Board object
     */
    private void initializeBoard(int numOfRows, int numOfCols, Map<Integer, String> gameObjectMap, List<List<String>> stringBoard) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        for (int row = ZERO; row < numOfRows; row++) {
            for (int col = ZERO; col < numOfCols; col++) {
                String objectName = stringBoard.get(row).get(col);
                addObjects(gameObjectMap, row, col, objectName);
            }
        }
    }

    private void addObjects(Map<Integer, String> gameObjectMap, int row, int col, String objectName) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (gameObjectMap.containsValue(objectName) && !objectName.equals(EMPTY)) {
            myBoard.createGameObject(row, col, objectName);
        } else if (creatureMap.containsValue(objectName)) {
            myBoard.createCreature(col * cellSize + THIRD_INDEX, row * cellSize + THIRD_INDEX, objectName,
                    cellSize- ENEMY_INDEX);
        }
    }

    /*
    Initialize all pieces within the BoardView object
     */
    private void initializeBoardView(int numOfRows, int numOfCols, Map<Integer, String> gameObjectMap, List<List<String>> stringBoard, BoardView boardView) {
        for (int row = ZERO; row < numOfRows; row++) {
            for (int col = ZERO; col < numOfCols; col++) {
                String objectName = stringBoard.get(row).get(col);//TODO MAKE SURE NOT SETTINGS
                addPieces(gameObjectMap, boardView, row, col, objectName);
            }
        }
    }

    private void addPieces(Map<Integer, String> gameObjectMap, BoardView boardView, int row, int col, String objectName) {
        if (!objectName.equals(EMPTY)) {
            if (gameObjectMap.containsValue(objectName)) {
                boardView.addBoardPiece(row, col, objectName, myGameSettings.getAllSettings().get(objectName));
            } else {
                if (objectName.equals(PACMAN)) { //TODO I added this in as a temporary fix. We need a way to tell if the creature is user controlled or CPU controlled. Maybe have the user specify what piece they want to control in the json file?
                    boardView.addUserCreature(row, col, objectName, myGameSettings.getAllSettings().get(objectName));
                } else if (objectName.equals(CPUGHOST)) {
                    boardView.addCPUCreature(row, col, objectName, myGameSettings.getAllSettings().get(objectName));
                }

            }
        }
    }

    /**
     * Access the coordinates of cells
     * @param pixels
     * @return the coordinates of cells
     */
    public int getCellCoordinate(double pixels) {
        return ((int) pixels) / cellSize;
    }

    /**
     * Get the BoardView object of the game
     * @return the Boardview object
     */
    public BoardView getBoardView() {
        return myBoardView;
    }

    /**
     * Get the dimension of each cell
     * @return the size of a cell in the board
     */
    public int getCellSize() {
        return cellSize;
    }

    /**
     * Update and sync each frame of the game with the last direction used
     * @param direction the string value for the direction
     */
    public void step(String direction) {
        gameController.step(direction);
    }

    /**
     * Access the current coordinates of the user
     * @return (x, y) of the current position
     */
    public int[] getUserPosition() {
        int[] newPosition = {myBoard.getMyUser().getXpos(), myBoard.getMyUser().getYpos()};
        return newPosition;
    }

    /**
     * Gets the new ghost position of the ghost identified by the given ID
     *
     * @param nodeID
     * @return The current ghost coordinates
     */
    public int[] getGhostPosition(String nodeID) {
        if (myBoard.getMyCPU(nodeID) != NULL_CREATURE) {
            int[] newPosition = {myBoard.getMyCPU(nodeID).getXpos(), myBoard.getMyCPU(nodeID).getYpos()};
            return newPosition;
        }
        return null;
    }

    /**
     * METHOD ONLY FOR TESTFX TESTS. Needed some way to load in a file into the file chooser.
     */
    public void changeToGameScreen(String filePath) {
        myStartScreen.startNewGameForViewTests(filePath);
    }

    /**
     * Load the settings for next level
     * @param boardView BoardView object for next level
     */
    public void loadNextLevel(BoardView boardView) {
        myGame.resetGame();
        initializeBoardView(myBoard.getRows(), myBoard.getCols(), gameObjectMap, stringBoard, boardView);
    }

    /**
     * Receive the backend's command to reset the entire game
     */
    public void restartGame() {
        initializeGame(myReader.getMostRecentPath());
    }

    /**
     * Returns the language of the game. If the user has input a language from the dropdown, it has priority.
     * @return String representing language name.
     */
    public String getLanguage() {
        if(UILanguage != null){
            return UILanguage;
        }
        return language;
    }

    /**
     * Sets the UI input language for the game.
     * @param lang String representing the language name.
     */
    public void setUILanguage(String lang){UILanguage = lang;}

    /**
     * Sets the username string for the game.
     * @param username String inputted by user on the home screen. Defaults to "Guest"
     */
    public void setUsername(String username) {
        myUsername = username;
    }

    /**
     * Returns the username for the current game.
     * @return String representing username
     */
    public String getUsername() {
        return myUsername;
    }

    /**
     * Returns the CSS file being used. Priority returns the UI choice box selection css mode over data file css
     * @return the CSS file path as a string.
     */
    public String getViewMode() {
        if(cssUIFileName != null){
            return cssUIFileName;
        }
        return cssFileName;
    }

    /**
     * Set the cell size in the grid system
     * @param newSize The length of a cell size
     */
    public void setCellSize(int newSize) {
        cellSize = newSize;
    }

    /**
     * Sets the CSS file being used.
     * @param cssName CSS file name WITHOUT .css on the end.
     */
    public void setViewMode(String cssName){
        cssUIFileName = String.format(CSS_FILE_EXTENSION, cssName);
    }

    /**
     * Get the game controller
     * @return GameController object
     */
    public GameController getGameController() {
        return gameController;
    }

    /**
     * Adds a new Username:Score combo to the high score CSV file
     * @param nameAndScore String array where the first element is the name and the second element is the score
     */
    public void addScoreToCSV(String[] nameAndScore) {
        myCSVWriter.writeNext(nameAndScore);
        try {
            myCSVWriter.close();
        } catch (IOException e) {}
    }

    /**
     * Read high score CSV and get the top ten scores.
     * @return List of string arrays where each String array is a single username:score combo.
     */
    public List<String[]> getScoreData() {
        List allScoreData = readCSV();
        return findTopTenScores(allScoreData);
    }

    private List<String[]> findTopTenScores(List<String[]> allScores) {
        List<String[]> topTen = new ArrayList<>();
        int numToDisplay = HIGH_SCORE_VALS;
        if (allScores.size() < HIGH_SCORE_VALS) {
            numToDisplay = allScores.size();
        }
        for (int i = ZERO; i < numToDisplay; i++) {
            topTen.add(BLANK_ENTRY);
        }
        optimizeTopTen(allScores, topTen, numToDisplay);
        return topTen;
    }

    private void optimizeTopTen(List<String[]> allScores, List<String[]> topTen, int numToDisplay) {
        for (String[] score : allScores) {
            for (int i = ZERO; i < numToDisplay; i++) {
                if (Integer.parseInt(score[ONE_INDEX]) > Integer.parseInt(topTen.get(i)[ONE_INDEX])) {
                    topTen.add(i, score);
                    break;
                }
            }
        }
        while (topTen.size() > HIGH_SCORE_VALS) {
            topTen.remove(topTen.size() - ONE_INDEX);
        }
    }

    /**
     * Returns the top score for the given username.
     * @return String value representing the integer score.
     */
    public String getTopScoreForUser(){
        List<String[]> scoreData = readCSV();
        String score = Integer.toString(ZERO);
        for(int i = ZERO; i<scoreData.size(); i++){
            if(Integer.parseInt(scoreData.get(i)[ONE_INDEX]) > Integer.parseInt(score) && myUsername.equals(scoreData.get(i)[ZERO])){
                score = scoreData.get(i)[ONE_INDEX];
            }
        }
        return score;
    }

    private List<String[]> readCSV(){
        List<String[]> allCSVData = new ArrayList<>();
        try {
            CSVReader csvReader = new CSVReader(new FileReader(new File(SCORE_PATH)));
            allCSVData = csvReader.readAll();
        } catch (IOException e) {
        }
        return allCSVData;
    }

    /**
     * Get the timer condition
     * @return Whether the condition is timer
     */
    public int getTimer() {
        if (myGameSettings.getGeneralSettings().get(TIMER) != null) {
            return Integer.parseInt(myGameSettings.getGeneralSettings().get(TIMER));
        }
        return -ONE_INDEX;
    }

    public String getGameType() {
        if (myGameSettings.getGeneralSettings().get(GAME_TITLE) != null){
            return myGameSettings.getGeneralSettings().get(GAME_TITLE);
        }
        return DEFAULT_TITLE;
    }

    @Deprecated
    public Game getGame() {
        return myGame;
    }

    /**
     * Get the number of lives remained
     *
     * @return the number of lives remained
     */
    @Deprecated
    public int getLives() {
        return myGame.getLives(); //TODO change this to the model's get lives
    }

    /**
     * Get the current game scores
     *
     * @return the current game scores
     */
    @Deprecated
    public int getScore() {
        return myGame.getScore();
    }

    @Deprecated
    public boolean getIsPoweredUp() {
        return myGame.getUser().isPoweredUp();
    }

    @Deprecated
    public boolean getIsInvincible() {
        return myGame.getUser().isInvincible();
    }

    @Deprecated
    public int getLevel() {
        return myGame.getLevel();
    }

    @Deprecated
    public boolean isGameOver() {
        return myGame.isGameOver();
    }

    /**
     * Returns the current time of the game.
     *
     * @return Integer values representing time.
     */
    @Deprecated
    public int getGameTime() {
        return myGame.getTime();
    }

    @Deprecated
    public void addOneMillionPoints() {
        getGame().addScore(MILLION);
    }

    @Deprecated
    public void addOneHundredPoints() {
        getGame().addScore(ONE_HUNDRED);
    }

    @Deprecated
    public void addFiveHundredPoints() {
        getGame().addScore(FIVE_HUNDRED);
    }

    @Deprecated
    public void resetGhosts() {
        List<CPUCreature> ghosts = getGame().getCPUs();
        for (CPUCreature ghost : ghosts) {
            ghost.die();
        }
    }

    @Deprecated
    public void addLife() {
        getGame().addLives(ONE_INDEX);
    }

    @Deprecated
    public void goToNextLevel() {
        getGame().nextLevel();
    }

    @Deprecated
    public void powerUp() {
        getGame().getUser().setPoweredUp(true);
    }

    @Deprecated
    public void FreezeGhosts() {
        getGame().getCPUs().removeAll(getGame().getCPUs());
    }//TODO (billion, billion)

    @Deprecated
    public void RemoveOneMillionPoints() {
        getGame().addScore(-MILLION);
    }

    @Deprecated
    public void resetUserPosition() {
        getGame().getUser().die();
    }

    @Deprecated
    public void loseLife() {
        getGame().addLives(-ONE_INDEX);
    }

    @Deprecated
    public void gameOver() {
        getGame().endGame();
    }

    /**
     * Sends information about the collision to the backend
     *
     * @param nodeID
     * @return
     */
    @Deprecated
    public boolean handleCollision(String nodeID) {
        collisionManager.setCollision(nodeID);
        return myGame.dealWithCollision(collisionManager);
    }
}