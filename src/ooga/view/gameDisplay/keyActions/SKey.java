package ooga.view.gameDisplay.keyActions;


import ooga.controller.ViewerControllerInterface;
import ooga.view.gameDisplay.center.BoardView;
import ooga.view.gameDisplay.keyActions.KeyViewAction;

public class SKey extends KeyViewAction {
  private ViewerControllerInterface myController;

  public SKey(BoardView boardView, ViewerControllerInterface controller){
    super(boardView, controller);
    myController = controller;
  }

  /**
   * Adds one million score to the current game
   */
  @Override
  public void doAction(){
    myController.getGameController().RemoveOneMillionPoints();
  }
}

