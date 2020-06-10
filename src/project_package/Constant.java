package project_package;

/**
 * This enum consists exclusively of the following constants used throughout the
 * package: project_package. pane width, pane height, size of every node,
 * number of nodes in the x-direction and finally number of nodes in the
 * y-direction.
 */

enum Constant
{
    PANE_WIDTH(900),
    PANE_HEIGHT(600),
    NODE_SIZE(20),
    X_NODES(PANE_WIDTH.value / NODE_SIZE.value),
    Y_NODES(PANE_HEIGHT.value / NODE_SIZE.value);

    final int value;

    Constant(int value)
    {
        this.value = value;
    }
}
