package project_package;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The class below (AlertBox) consists of a single method, display().
 */

class AlertBox
{
    /**
     * display() generates a new modal window that blocks events from being
     * delivered to any other application window
     * @param title : sets the title of the window
     * @param message : sets the message to be displayed on the window
     * @param x : sets the width of the window
     * @param y : sets the height of the window
     */

    static void display(String title, String message, int x, int y)
    {
        Stage window = new Stage();

        window.setTitle(title);
        window.setWidth(x);
        window.setHeight(y);
        window.initModality(Modality.APPLICATION_MODAL);
        window.resizableProperty().setValue(Boolean.FALSE);

        Text text = new Text(message);
        text.setFont(Font.font(14));
        text.setLineSpacing(2);

        Button close = new Button("ok");
        close.setOnAction(e -> window.close());

        VBox layout = new VBox(20.0D);

        layout.getChildren().addAll(new Node[]{text, close});
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout);

        window.setScene(scene);
        window.showAndWait();
    }
}
