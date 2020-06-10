/**
 * This is a graphical path-finder (using the A star algorithm) which displays
 * a grid (900 x 600) that you can interact with to generate/remove walls, set
 * the starting node, set the destination node and finally displays the shortest
 * path from the starting node to the destination node. The A star algorithm
 * uses the idea of a heuristic function which estimates the displacement from
 * the target node.
 * @author Rigved Deglurkar
 */

package project_package;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javafx.concurrent.Task;

public class Main extends Application
{
    private A_Star star = new A_Star();
    private Stage window;
    State state = new State();
    private Pane root = new Pane();
    public Task<Void> astar = new Task<>() {
        @Override protected Void call() throws Exception {
            state.isRunning = true;
            star.aStarSearch(state, this);
            return null;
        }
    };
    public Thread thread = new Thread(astar);

    public static void main(String[] args)
    {
        launch(args);
    }
    /**
     * Configures and displays the window when this function is invoked.
     */
    @Override
    public void start(Stage primaryStage)
    {
        window = primaryStage;
        window.setWidth(Constant.PANE_WIDTH.value + 1);
        window.setHeight(Constant.PANE_HEIGHT.value + 65);
        Scene scene = new Scene(display_content());
        window.resizableProperty().setValue(Boolean.FALSE);
        window.setScene(scene);
        window.setTitle("PathFinder");
        window.show();
    }

    /**
     * Configures a Pane object (root). Generates 4 buttons for starting the
     * search, displaying instructions for the program, generating random grids and
     * restarting the program after every search. These 4 buttons are added to
     * a set containing all of the root's children, allowing them to be
     * displayed on the scene.
     */
    private Parent display_content ()
    {
        thread.setDaemon(true);
        root.setPrefSize(Constant.PANE_WIDTH.value - 10, Constant.PANE_HEIGHT.value + 30);
        star.populateGrid(root);
        Button start = new Button("Start");
        setUpButton(start, Constant.PANE_WIDTH.value - Constant.PANE_WIDTH.value/4, Constant.PANE_HEIGHT.value + 7);
        start.setOnAction(e -> start_Search());
        Button info = new Button("Info");
        setUpButton(info, Constant.PANE_WIDTH.value/8, Constant.PANE_HEIGHT.value + 7);
        info.setOnAction(e -> AlertBox.display("Info", "Instructions: \nRight-click on tile to set target(red block)\nleft-click on tile to set/remove wall\nleft-drag on tile to set start(green block)\nTrivial paths won't be displayed\nYou can't set a wall on the target nor the start\n\nKey:\nCrimson red: shortest path\nOrange: nodes that have been fully explored\nYellow: nodes that weren't feasible to check \n\nAuthor: \nRigved Deglurkar", 370, 400));
        Button reset = new Button("Reset");
        setUpButton(reset, Constant.PANE_WIDTH.value - Constant.PANE_WIDTH.value/8, Constant.PANE_HEIGHT.value + 7);
        reset.setOnAction(event -> {
            if (astar.isCancelled() || astar.isDone()) {
                astar = null;
                state.isRunning = false;
                window.close();
                Main program = new Main();
                program.start(window);
                return;
            }
            astar.cancel();
        });
        Button genRandGrid = new Button("Generate Random Grid");
        setUpButton(genRandGrid, Constant.PANE_WIDTH.value/4, Constant.PANE_HEIGHT.value + 7);
        genRandGrid.setOnAction(e -> randomize_grid());
        root.getChildren().addAll(start, info, reset, genRandGrid);
        return root;
    }

    /**
     * Sets the width and height of a given button.
     * @param b : button whose width and height need to be set
     * @param w : the width of the button as a floating point number
     * @param h : the height of the button as a floating point number
     */
    private void setUpButton(Button b, double w, double h)
    {
        b.setLayoutX(w);
        b.setLayoutY(h);
    }

    /**
     * starts searching for the target node and ensures that if the start
     * button was pressed in a running instance you wont be able to press
     * start until you press reset, which will reduce the chances of any
     * null-pointer exceptions being thrown
     */
    private void start_Search ()
    {
        if (!state.isRunning)
        {
            thread.start();
        }
    }

    private void randomize_grid () {
        if (!state.isRunning) {
            star.randomizedGrid();
        }
    }
}
