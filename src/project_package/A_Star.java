/**
 * A_Star class consists exclusively of fields and operations required by the
 * A star algorithm i.e. start and target points, the matrix, open/closed
 * ArrayLists and the current selected node.
 */

package project_package;

import javafx.concurrent.Task;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.Random;

class A_Star
{
    private static int xTarget;
    private static int yTarget;
    private static int xStart;
    private static int yStart;
    private static Node[][] grid;
    private ArrayList<Node> open;
    private ArrayList<Node> closed;

    private Node current;
    private double gnext, hnext, fnext;

    /**
     * Node class which consists of properties and operations of every Node
     * displayed on the screen. Attaches event filters such as drag, primary
     * press and secondary press.
     */
    private class Node extends StackPane
    {
        private int x;
        private int y;
        private double H;
        private double G;
        private double F;
        private int xParent;
        private int yParent;
        private boolean isWall;
        private Rectangle border;

        private Node(int x, int y)
        {
            this.x = x;
            this.y = y;
            F = Double.MAX_VALUE;
            H = 0;
            G = 0;
            xParent = -1;
            yParent = -1;
            isWall = false;
            border = new Rectangle(Constant.NODE_SIZE.value, Constant.NODE_SIZE.value );
            border.setStroke(Color.DIMGRAY);
            border.setFill(Color.rgb(88, 89, 90));
            getChildren().addAll(border);
            setTranslateX(x * Constant.NODE_SIZE.value);
            setTranslateY(y * Constant.NODE_SIZE.value);
            border.addEventFilter(MouseEvent.MOUSE_PRESSED, e ->
            {
                // secondary click sets target node
                if (e.isSecondaryButtonDown())
                {
                    // grid[...][...] returns a node associated with that index
                    if (grid[xTarget][yTarget].x == xStart && grid[xTarget][yTarget].y == yStart)
                    {
                        grid[xTarget][yTarget].isWall = false;
                        grid[xTarget][yTarget].border.setFill(Color.rgb(0, 255, 0));
                    }
                    else
                    {
                        grid[xTarget][yTarget].border.setFill(Color.rgb(88, 89, 90));
                        grid[xTarget][yTarget].isWall = false;
                    }
                    xTarget = this.x;
                    yTarget = this.y;
                    this.border.setFill(Color.rgb(0, 0, 255));
                }
                // primary click removes wall if it has been previously recorded
                // as a wall
                if (isWall && e.isPrimaryButtonDown())
                {
                    border.setFill(Color.rgb(88, 89, 90));
                    isWall = !this.isWall;
                }
                // else assigns a wall
                else if (!isWall && e.isPrimaryButtonDown() && !(this.x == xStart && this.y == yStart) && !(this.x == xTarget && this.y == yTarget))
                {
                    this.border.setFill(Color.rgb(37, 37, 37));
                    this.isWall = !this.isWall;
                }
            });

            // dragging on a node sets the start node
            border.addEventFilter(MouseEvent.MOUSE_DRAGGED, e ->
            {
                if (grid[xStart][yStart].x == xTarget && grid[xStart][yStart].y == yTarget)
                {
                    grid[xStart][yStart].isWall = false;
                    grid[xStart][yStart].border.setFill(Color.rgb(0, 0, 255));
                }
                else
                {
                    grid[xStart][yStart].border.setFill(Color.rgb(88, 89, 90));
                    grid[xStart][yStart].isWall = false;
                }

                xStart = this.x;
                yStart = this.y;
                border.setFill(Color.rgb(0, 255, 0));
            });
        }
        // accessor and mutator methods
        void setBorder(Color c)
        {
            this.border.setFill(c);
        }

        void setG(double g)
        {
            this.G = g;
        }

        void setH(double h)
        {
            this.H = h;
        }

        void setF(double f) {
            this.F = f;
        }

        int getX()
        {
            return this.x;
        }

        int getY()
        {
            return this.y;
        }

        double getG()
        {
            return this.G;
        }

        double getF() {
            return this.F;
        }

        boolean isWall() {
            return this.isWall;
        }
    }

    /**
     * Constructor that initialises the A_Star class variables
     */
    A_Star()
    {
        xTarget = Constant.X_NODES.value - 1;
        yTarget = Constant.Y_NODES.value - 1;
        xStart = 0;
        yStart = 0;
        grid = new Node[Constant.X_NODES.value][Constant.Y_NODES.value];
        open = new ArrayList<>();
        closed = new ArrayList<>();
    }

    /**
     * aStarSearch() is the implementation of the A star algorithm, which uses
     * heuristics to guide the algorithm to find the shortest path.
     */
    void aStarSearch(State state, Task task)
    {
        if (!state.isRunning) {
            return;
        }
        if (xStart == xTarget && yStart == yTarget)
        {
            AlertBox.display("---", "Already at target", 280, 140);
            return;
        }
        grid[xStart][yStart].setF(0);
        grid[xStart][yStart].setG(0);
        grid[xStart][yStart].setH(0);
        grid[xStart][yStart].xParent = xStart;
        grid[xStart][yStart].yParent = yStart;

        // add starting index to the open ArrayList
        open.add(grid[xStart][yStart]);

        int[][] succ = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}, {-1, 1}, {1, -1}, {-1, -1}, {1, 1}};

        while (!open.isEmpty()) {
            int fIndex = smallestFIndex();
            current = open.get(fIndex);

            // remove current node from the open ArrayList
            open.remove(fIndex);
            // add current node to the closed ArrayList
            closed.add(current);
            if (current.getX() == xStart && current.getY() == yStart) {
                current.setBorder(Color.rgb(0, 255, 0));
            } else {
                current.setBorder(Color.ORANGE);
            }
            //neighbours
            for (int i = 0; i < succ.length; i++) {
                if (isValid(current.getX() + succ[i][0], current.getY() + succ[i][1])) {
                    if (isDest(current.getX() + succ[i][0], current.getY() + succ[i][1])) {
                        grid[current.getX() + succ[i][0]][current.getY() + succ[i][1]].xParent = current.getX();
                        grid[current.getX() + succ[i][0]][current.getY() + succ[i][1]].yParent = current.getY();
                        displayPath();
                        AlertBox.display("Success", "Target was found, press reset", 280, 140);
                        return;
                    }
                    if (succ[i][0] == 0 || succ[i][1] == 0)
                        updateNeighbour(current.getX() + succ[i][0], current.getY() + succ[i][1], false);
                    else
                        updateNeighbour(current.getX() + succ[i][0], current.getY() + succ[i][1], true);
                }
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                task.cancel();
            }
        }
        return;
    }

    /**
     * Calculates the new G, H and F values of the neighbouring node and if the
     * new F value is smaller than the previously recorded F, then replace all
     * of the values.
     * @param x : x value of the node we are checking
     * @param y : y value of the node we are checking
     * @param diag : if the neighbouring node is diagnally away (true) use the value
     *               sqrt(2) else use 1.0.
     */
    private void updateNeighbour(int x, int y, boolean diag)
    {
        if (!closed.contains(grid[x][y]) && !grid[x][y].isWall())
        {
            grid[x][y].setBorder(Color.rgb(255, 242, 82));
            gnext = grid[current.getX()][current.getY()].getG() + ((diag) ? Math.sqrt(2) : 1);
            hnext = euclideanDistance(grid[x][y]);
            fnext = gnext + hnext;
            if (grid[x][y].getF() == Double.MAX_VALUE || grid[x][y].getF() > fnext)
            {
                open.add(grid[x][y]);
                grid[x][y].setF(fnext);
                grid[x][y].setG(gnext);
                grid[x][y].setH(hnext);
                grid[x][y].xParent = current.getX();
                grid[x][y].yParent = current.getY();
            }
        }
    }

    /**
     * Heuristic function which calculates the euclidean distance from a given
     * node to the target.
     */
    private double euclideanDistance(Node n)
    {
        return Math.sqrt(Math.pow(n.getX() - xTarget, 2) + Math.pow(n.getY() - yTarget, 2));
    }

    /**
     * Once algorithm is ran and the shortest path has been found we trace back
     * all parent nodes until we detect the starting point. Start tracing
     * back from the target node.
     */
    private void displayPath()
    {
        Node tmp = grid[xTarget][yTarget];
        tmp = grid[tmp.xParent][tmp.yParent];
        while (!(tmp.getX() == xStart && tmp.getY() == yStart))
        {
            tmp.setBorder(Color.CRIMSON);
            tmp = grid[tmp.xParent][tmp.yParent];
        }
    }

    /**
     * Returns the index of the smallest F value in the open ArrayList.
     */
    private int smallestFIndex ()
    {
        int fIndex = 0;
        double f = Double.MAX_VALUE;
        for (int i = 0; i < open.size(); i++)
        {
            if (open.get(i).getF() < f)
            {
                f = open.get(i).getF();
                fIndex = i;
            }
        }
        return fIndex;
    }

    /**
     * Returns true if the current node is the target node
     * @param x : the x value on a 2D plane
     * @param y : the y value on a 2D plane
     */
    private boolean isDest(int x, int y)
    {
        if (x == xTarget && y == yTarget)
        {
            return true;
        }
        return false;
    }

    /**
     * Returns true if the row and column values are within the range of the
     * matrix else returns false.
     * @param row : the x value on a 2D plane
     * @param col : the y value on a 2D plane
     */
    private boolean isValid(int row, int col)
    {
        return (row >= 0) && (row < Constant.X_NODES.value) &&
                (col >= 0) && (col < Constant.Y_NODES.value);
    }

    /**
     * Clears all previously set walls, and sets random walls throughout the
     * grid as long as they are not the start or target node. 20% of the matrix
     * will be walls.
     */
    public void randomizedGrid()
    {
        Random rng = new Random();
        // clears previous walls
        for (int i = 0; i < Constant.X_NODES.value; i++)
        {
            for (int j = 0; j < Constant.Y_NODES.value; j++)
            {
                if (!(i == xStart && j == yStart) && !(i == xTarget && j == yTarget))
                {
                    grid[i][j].isWall = false;
                    grid[i][j].setBorder(Color.rgb(88, 89, 90));
                }
            }
        }
        // sets new walls
        for (int i = 0; i < Constant.X_NODES.value; i++)
        {
            for (int j = 0; j < Constant.Y_NODES.value; j++)
            {
                if (!(i == xStart && j == yStart) && !(i == xTarget && j == yTarget))
                {
                    grid[i][j].isWall = (rng.nextInt(4) == 1) ? true : false;
                    if (grid[i][j].isWall)
                    {
                        grid[i][j].setBorder(Color.rgb(37, 37, 37));
                    }
                }
            }
        }
    }

    /**
     * Creates a node object for every element in the 2D matrix and adds this
     * new node to a set consisting of the children of a Pane object (root).
     * @param root : Pane object
     */
    void populateGrid(Pane root)
    {
        for (int i = 0; i < Constant.X_NODES.value; i++)
        {
            for (int j = 0; j < Constant.Y_NODES.value; j++)
            {
                grid[i][j] = new Node(i, j);
                root.getChildren().add(grid[i][j]);
            }
        }
        // sets the color of the target node
        grid[xTarget][yTarget].setBorder(Color.rgb(0, 0, 255));
        // sets the color of the starting node
        grid[xStart][yStart].setBorder(Color.rgb(0, 255, 0));
    }
}