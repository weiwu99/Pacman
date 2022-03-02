package ooga.view.popups;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.util.logging.Level;

public class ErrorView {

  private static final String DEFAULT_RESOURCE_PACKAGE = "ooga.view.resources.";
  public static final String ERROR_TITLE = "ErrorTitle";
  private static final String LOGS_DEFAULT_LOG = "logs/default.log";
  private ResourceBundle myResources;
  private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

  public ErrorView(String language){
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language);
  }

  /**
   * Displays the error as an Alert in the GUI. Displays the Title of the
   * error as well as the error message to the user.
   *
   * @param message is the String that is displayed on the Alert in the GUI
   */
  public void showError(String message) {
    LOGGER.log(Level.WARNING, message);
    try {
      FileHandler fh=new FileHandler(LOGS_DEFAULT_LOG, true);

      LOGGER.addHandler(fh);
    }catch(IOException ioException){

    }
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(myResources.getString(ERROR_TITLE));
    alert.setContentText(message);
    alert.showAndWait();
  }

}
