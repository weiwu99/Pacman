package ooga.view.gameDisplay.gamePieces;

import java.util.List;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import ooga.controller.Controller;

/**
 * Abstract class that still extends the GamePiece class, but it is the common ancestor for moving
 * pieces like pacman and ghosts.
 */
public abstract class MovingPiece extends GamePiece{

  private ImageView myCreature;

  public MovingPiece(Integer cellSize){
    super(cellSize);
  }



  /**
   * Updates the position of the MovingPiece ImageView Node object.
   * @param x x location in the Group.
   * @param y y location in the Group.
   */
  public void updatePosition(double x, double y){
    myCreature = (ImageView) getPiece();
    myCreature.setX(x);
    myCreature.setY(y);
  }

  @Deprecated
  /**
   * Returns the X position in pixels of the moving piece
   * @return pixel value
   */
  public double getX() {
    myCreature = (ImageView) getPiece();
    return myCreature.getLayoutX();
  }

  @Deprecated
  /**
   * Returns the Y position in pixels of the moving piece
   * @return pixel value
   */
  public double getY() {
    myCreature = (ImageView) getPiece();
    return myCreature.getLayoutX();
  }

  /**
   * Rotates the moving piece. Positive input angle rotates clockwise, negative--ccw. (angle in degrees)
   * @param rotationAngle angle the moving piece should turn clockwise.
   */
  public void rotatePiece(double rotationAngle){
    myCreature.setRotate(rotationAngle);
  }

  /**
   * Checks if the current user creature is colliding with a node in the game group.
   * @param nodeList Nodes to check for collisions with.
   * @return The ID of the collided node if there is a collision, null otherwise.
   */
  public String getCreatureCollision(List<MovingPiece> nodeList){
    for(MovingPiece node: nodeList){
      if(myCreature.getBoundsInParent().contains(node.getPiece().getBoundsInParent().getCenterX(), node.getPiece().getBoundsInParent().getCenterY()) && myCreature != node.getPiece()){
        return node.getPiece().getId();
      }
    }
    return null;
  }

  /**
   * Checks if the current user creature is colliding with a node in the game group.
   * @param nodeList Nodes to check for collisions with.
   * @return The ID of the collided node if there is a collision, null otherwise.
   */
  public String getCollision(List<Node> nodeList){
    for(Node node: nodeList){
      if(myCreature.getBoundsInParent().contains(node.getLayoutX(), node.getLayoutY()) && myCreature != node){
        return node.getId();
      }
    }
    return null;
  }

  /**
   * 
   * @return
   */
  public ImageView getMyCreature() {
    return myCreature;
  }
}