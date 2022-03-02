package ooga.view.gameDisplay.gamePieces;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Map;

/**
 * Class that represents the wall pieces in the view board.
 */
public class WallPiece extends GamePiece{

  private static final Color DEFAULT_COLOR = Color.DARKBLUE;
  public static final String WALL_COLOR = "WALL_COLOR";
  private Color myColor;
  private static final String CSS_ID = "wallPiece";


  public WallPiece(Integer cellSize, Map<String, String> myValues){
    super(cellSize);
    myColor = DEFAULT_COLOR;
    if (myValues != null) {
      if(myValues.containsKey(WALL_COLOR)){
        String rgbValues= myValues.get(WALL_COLOR); //TODO PARSE OUT NEGATIVE DATA
        myColor=parseRGBs(rgbValues);
      }

    }
    setMyPiece(makeNode());
  }

  @Override
  protected Rectangle makeNode(){
    Rectangle wall = new Rectangle(getCellSize(), getCellSize());
    wall.setId(getCellIndexID());
    wall.setFill(myColor);
    return wall;
  }

}
