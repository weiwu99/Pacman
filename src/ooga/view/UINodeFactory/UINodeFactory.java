package ooga.view.UINodeFactory;

import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import ooga.controller.ViewerControllerInterface;

public class UINodeFactory {

  private static final String DEFAULT_RESOURCE_PACKAGE = "ooga.view.resources.";
  public static final String INPUT_FIELD = "input-field";
  public static final String COLOR_PICKER = "color-picker";
  private static String DEFAULT_STYLESHEET;
  private ResourceBundle myResources;
  private static final double MAX_SIZE = 150.0;
  private static final double SPACING = 5.0;
  public UINodeFactory(ViewerControllerInterface myController){
    myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + myController.getLanguage());
    DEFAULT_STYLESHEET = "/" + DEFAULT_RESOURCE_PACKAGE.replace(".", "/") + myController.getViewMode();
  }

  /**
   * Creates buttons for the UI.
   * @param property The Text for the Button
   * @param icon Button icon image path
   * @param buttonStyle CSS ID for the button
   * @param ID The Node ID for the Button
   * @param response Button action.
   * @return Button node
   */
  public Button makeButton(String property, ImageView icon, String buttonStyle, String ID,
      EventHandler<ActionEvent> response) {
    Button result = new Button(property, icon);
    result.setOnAction(response);
    result.getStyleClass().add(buttonStyle);
    result.getStyleClass().add(ID);
    result.setFocusTraversable(false);
    return (Button)setID(ID, result);
  }

  /**
   * Creates a text input field Node.
   * @param ID CSS ID and Node ID
   * @param response Action on entry
   * @param initial Initial text in field
   * @return Text field Node
   */
  public TextField makeInputField(String ID, Consumer<String> response, String initial) {
    TextField result = new TextField();
    result.getStyleClass().add(INPUT_FIELD);
    result.setOnKeyReleased(e -> response.accept(result.getText()));
    result.setText(initial);
    result.setId(ID);
    return result;
  }

  /**
   * Creates a label node.
   * @param text text to put in label.
   * @param ID CSS ID and NOde ID of label.
   * @return Label Node.
   */
  public Label makeLabel(String text, String ID) {
    Label label = new Label(text);
    label.getStyleClass().add(ID);
    return (Label)setID(ID, label);
  }

  /**
   * Makes an HBox Node.
   * @param rowFormatting CSS ID for HBox.
   * @param nodes Nodes to put into HBox.
   * @return Hbox full of nodes.
   */
  public HBox makeRow(String rowFormatting, Node... nodes) {
    HBox row = new HBox();
    row.getChildren().addAll(nodes);
    row.setSpacing(SPACING);
    row.getStyleClass().add(rowFormatting);
    return row;
  }

  /**
   * Creates a VBox Node.
   * @param rowFormatting CSS ID for VBox.
   * @param nodes Nodes to put in VBox.
   * @return VBox Node.
   */
  public VBox makeCol(String rowFormatting, Node... nodes) {
    VBox col = new VBox();
    col.getChildren().addAll(nodes);
    col.setSpacing(SPACING);
    col.getStyleClass().add(rowFormatting);
    return col;
  }

  /**
   * Creates choice box node.
   * @param boxID CSS ID for the choice box
   * @param entries The string entries to have in the box.
   * @return The choice box node
   */
  public ChoiceBox makeChoiceBox(String boxID, String... entries){
    ChoiceBox cb = new ChoiceBox(FXCollections.observableList(Arrays.asList(entries)));
    cb.getStyleClass().add(boxID);
    return (ChoiceBox) setID(boxID, cb);
  }

  private Node setID(String id, Node node) {
    node.setId(id);
    return node;
  }

}
