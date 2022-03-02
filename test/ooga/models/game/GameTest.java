package ooga.models.game;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javafx.stage.Stage;
import ooga.controller.Controller;
import ooga.controller.JSONContainer;
import ooga.controller.JSONReader;
import ooga.models.creatures.cpuControl.CPUCreature;
import ooga.models.creatures.userControl.UserCreature;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.DukeApplicationTest;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest extends DukeApplicationTest {
    private Map<String,String> map;
    private List<CPUCreature> creatureList;
    private Board newBoard;
    private Game g;
    private UserCreature userPacman;
    private int numPickups;
    private Controller myController;
    private Controller myController2;

    @Override
    public void start(Stage stage)
            throws IOException, ParseException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        myController = new Controller(stage);
        myController2 = new Controller(stage);
    }
    @BeforeEach
    public void initializeGame() throws IOException, ParseException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        map=Map.of("LANGUAGE","ENGLISH","GAME_TITLE","PACMAN",
            "TIMER","-1","LIVES","3","CELL_SIZE","24","CSS_FILE_NAME","DEFAULT.CSS","USER_IS_PREDATOR","0","HARD","1","IS_PICKUPS_A_VALID_WIN_CONDITION","1");
        JSONReader reader = new JSONReader("English","data/test/vanillaTest.json");
        JSONContainer container = reader.readJSONConfig();

        myController2.initializeGame("data/test/chocolateTest.json");
        myController.initializeGame("data/test/vanillaTest.json");
        List<List<String>> stringBoard = container.getMyStringBoard();
        newBoard=myController.getGameController().getGame().getMyBoard();
        myController.getGameController().getGame().setLastDirection("RIGHT");
       // myController.initializeBoard(numOfRows, numOfCols, gameObjectMap, stringBoard);
        numPickups = 10;
        userPacman= myController.getGameController().getGame().getUser();
        CPUCreature c1 = new CPUCreature(100,100);
        c1.setId("CREATURE123");
        c1.setCurrentDirection(new int[]{0,1});
        creatureList=new ArrayList<CPUCreature>();
        creatureList.add(c1);
        g=myController.getGameController().getGame();
    }
    @Test
    public void testGetUserControlled(){
        g=myController.getGameController().getGame();
        UserCreature a = g.getUser();
        assert(a.equals(userPacman));
    }
    @Test
    public void testGetGameObjectIsWall(){
        g=new Game(newBoard);
        assert (g.getGameObject(0,0).isWall());
        assert (!g.getGameObject(0,3).isWall());
    }
    @Test
    public void testGetCPUCreatures(){
        g=myController.getGameController().getGame();
        assert(g.getCPUs().size()==1);
    }
    @Test
    public void testAddScore(){
        g=myController.getGameController().getGame();
        g.addScore(2000);
        g.addScore(200);
        assert (g.getScore()==2200);
    }
    @Test
    public void testGetScore(){
        g=myController.getGameController().getGame();
        g.addScore(2000);
        int num=g.getScore();
        assert (num==2000);
    }
    @Test
    public void testPickupsLeft() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        for(int i=0;i<21;i++) {
            g.updatePickupsLeft();

        }
        myController.step("RIGHT");
        assert(g.getLevel()==2);
    }

    @Test
    public void testGetLevel() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        assert(g.getLevel()==1);

        for(int i=0;i<21;i++) {
            g.updatePickupsLeft();
        }
        myController.step("RIGHT");
        assertEquals(g.getLevel(),2);
    }
    @Test
    public void testDealWithCollisionCreatureNotPoweredUp(){
        g=myController.getGameController().getGame();
        CollisionManager cm = new CollisionManager();
        cm.setCollision("CREATURE123");
        g.dealWithCollision(cm);
        assert (g.getLives()==2);
    }
    @Test
    public void testDealWithCollisionCreaturePoweredUp(){
        g=myController.getGameController().getGame();
        userPacman.setPoweredUp(true);
        CollisionManager cm = new CollisionManager();
        cm.setCollision("CREATURE123");
        g.dealWithCollision(cm);
        assert (g.getScore()==400);
    }
    @Test
    public void testNextLevel(){
        g=myController.getGameController().getGame();
        assertEquals(1,g.getLevel());
        g.nextLevel();
        assertEquals(2,g.getLevel());
    }
    @Test
    public void testGameOver() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        CollisionManager cm = new CollisionManager();
        for(int i=0;i<3;i++) {
            cm.setCollision("CREATURE123");
            g.dealWithCollision(cm);
        }
        myController.step("RIGHT");
        assert(g.isGameOver());
    }
    @Test
    public void testDealWithCollisionPickup(){
        g=myController.getGameController().getGame();
        userPacman.setPoweredUp(true);
        CollisionManager cm = new CollisionManager();
        cm.setCollision("3,3");
        g.dealWithCollision(cm);
        assert (g.getScore()==100);
    }
    @Test
    public void resetGame(){
        g=myController.getGameController().getGame();
        userPacman.setPoweredUp(true);
        CollisionManager cm = new CollisionManager();
        cm.setCollision("3,3");
        g.dealWithCollision(cm);
        myController.restartGame();
        g=myController.getGameController().getGame();
        assert(g.getScore()==0);
        assert (g.getLives()==3);
        assert (g.getLevel()==1);
        g.resetGame();
        assertEquals(g.getCPUs().get(0).getHomeX(),g.getCPUs().get(0).getXpos());

    }
    @Test
    public void resetGame2(){
        g=myController.getGameController().getGame();
        userPacman.setPoweredUp(true);
        CollisionManager cm = new CollisionManager();
        cm.setCollision("3,3");
        g.dealWithCollision(cm);
        myController.restartGame();
        g=myController.getGameController().getGame();
        assert(g.getScore()==0);
        assert (g.getLives()==3);
        assert (g.getLevel()==1);
        g.resetGame();
        assertEquals(g.getCPUs().get(0).getHomeY(),g.getCPUs().get(0).getYpos());

    }
    @Test
    public void testLevel() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        assert (g.getLevel()==1);
        for(int i=0;i<21;i++) {
            g.updatePickupsLeft();
        }
        myController.step("LEFT");

        assert (g.getLevel()==2);

    }
    @Test
    public void testSetLastDirection(){
        g=myController.getGameController().getGame();
        assert (g.setLastDirection("DOWN"));

    }
    @Test
    public void testMove() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("UP");
        myController.step("UP");

        assert(g.getUser().getXpos()==75);
        assert(g.getUser().getYpos()==146);
    }
    @Test
    public void testMoveRight() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("RIGHT");
        myController.step("RIGHT");

        assert(g.getUser().getXpos()==76);
        assert(g.getUser().getYpos()==147);
    }
    @Test
    public void testMoveRightTwice() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("RIGHT");
        for(int i=0;i<2;i++) {
            myController.step("RIGHT");
        }


        assert(g.getUser().getXpos()==77);
        assert(g.getUser().getYpos()==147);
    }
    @Test
    public void testMoveRightIntoWall() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("RIGHT");
        for(int i=0;i<3;i++) {
            myController.step("RIGHT");
        }


        assert(g.getUser().getXpos()==77);
        assert(g.getUser().getYpos()==147);
    }




    @Test
    public void testMoveLeft() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("LEFT");
        myController.step("LEFT");

        assert(g.getUser().getXpos()==74);
        assert(g.getUser().getYpos()==147);
    }
    @Test
    public void testMoveLeftTwice() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("LEFT");
        for(int i=0;i<2;i++) {
            myController.step("LEFT");
        }


        assert(g.getUser().getXpos()==73);
        assert(g.getUser().getYpos()==147);
    }
    @Test
    public void testMoveLeftThrice() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("LEFT");
        for(int i=0;i<3;i++) {
            myController.step("LEFT");
        }


        assert(g.getUser().getXpos()==72);
        assert(g.getUser().getYpos()==147);
    }
    @Test
    public void testMoveLeftIntoWall() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        g=myController.getGameController().getGame();
        g.setLastDirection("LEFT");
        for(int i=0;i<4;i++) {
            myController.step("LEFT");
        }


        assert(g.getUser().getXpos()==72);
        assert(g.getUser().getYpos()==147);
    }
    @Test
    public void testGetGameObject(){
        assert (g.getGameObject(0,0).isWall());
    }
    @Test
    public void testSetUserSpeed(){
        g.setUserSpeed(2);
        myController.step("LEFT");
        myController.step("LEFT");
        assert(g.getUser().getXpos()==74);
        assert(g.getUser().getYpos()==147);
    }
    @Test
    public void testSetCPUSpeed(){
        g.setCPUSpeed(2);
        System.out.println(g.getCPUs().get(0).getXpos());
        System.out.println(g.getCPUs().get(0).getYpos());
        myController.step("LEFT");
        myController.step("LEFT");

        assert(g.getCPUs().get(0).getXpos()==147);
        assert(g.getCPUs().get(0).getYpos()==170);
    }

    @Test
    public void testTimer(){
        int num=0;
        for(int i=0;i<98;i++){
            myController.step("LEFT");
            assertEquals(num,g.getTime());

        }
    }
    @Test
    public void testMoveCreatureToCell(){

        g.moveCreatureToCell(new int[]{1,1});
        assert(25==userPacman.getXpos());
        assert(25==userPacman.getYpos());
    }
    @Test
    public void testGetMyBoard(){
        g=myController.getGameController().getGame();
        assert (g.getMyBoard().equals(newBoard));
    }
    @Test
    public void testEndGame(){
        g=myController.getGameController().getGame();
        assertFalse(g.isGameOver());
        g.endGame();
        assertTrue(g.isGameOver());

    }
    @Test
    public void testAddLife(){
        assertEquals(3, g.getLives());
        g.addLife();
        assertEquals(g.getLives(),4);
    }
    @Test
    public void testAddLives(){
        assertEquals(3, g.getLives());
        g.addLives(1);
        assertEquals(g.getLives(),4);
        g.addLives(9);
        assertEquals(g.getLives(),13);
    }

    @Test
    public void testPredatorWinLossTimer0(){
g=myController2.getGameController().getGame();
        myController2.step("LEFT");
        myController2.step("LEFT");
        assertTrue(g.isGameOver());
    }
    @Test
    public void testPredatorWinNoLives(){
        g=myController2.getGameController().getGame();
        g.addLives(-3);
        myController2.step("LEFT");
        assertEquals(g.getLevel(),2);
    }
    @Test
    public void testPreyLoseNoLives(){
        g.addLives(-3);
        myController.step("LEFT");
        assertEquals(g.isGameOver(),true);
    }
    @Test
    public void testresetpowerups(){
        g.setPowerupEndtime(1);
        myController.step("LEFT");
        assertEquals(g.getUser().isPoweredUp(),false);
    }
    @Test
    public void testmultScore(){
        g.multiplyScore(2);
        myController.step("LEFT");
        assertEquals(g.getScore(),0);
    }
    @Test
    public void testGetPortals(){

        assertEquals(g.getPortalLocations().size(),0);
    }
    @Test
    public void testGetStepCounter(){

        assertEquals(g.getStepCounter(),0);
    }
    @Test
    public void testSetPortalsGone(){
        g.setPortalsGone();
        assertEquals(g.getPortalLocations(),null);
    }
    @Test
    public void testremovePortalsGone(){
        try {
            g.removePortal(new int[]{1, 1});
        }
        catch(IndexOutOfBoundsException e) {
            assertEquals(g.getPortalLocations().size(), 0);
        }
    }
    @Test
    public void testReSetPortalsGone(){
        g.setPortalsGone();
        assertEquals(g.getPortalLocations(),null);
    }



}
