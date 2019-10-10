package backend.maze;

import backend.util.graph.Graph;
import backend.util.graph.Vertex;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Represent a maze.
 * Maze is represented as a graph.
 *
 * @author Brock Dyer.
 */
public class Maze {

    /**
     * The start and exit vertices of the maze.
     */
    private Vertex<Point> start, exit;

    private Graph<Point> mgraph;

    /**
     * Construct a new Maze.
     *
     * @param startP the starting coordinate of the maze.
     */
    public Maze(Point startP) {
        this.mgraph = new Graph<>();
        this.start = new Vertex<>(startP);
        this.mgraph.addValue(start.getValue());
    }

    /**
     * Set the value of the exit vertex.
     *
     * @param exit the point of the exit vertex.
     */
    public void setExit(Point exit) {
        this.exit = new Vertex<>(exit);
    }

    /**
     * Add the vertex to the graph.
     *
     * @param point the point of the vertex to add to the graph.
     */
    public void addVertex(Point point) {
        mgraph.addValue(point);
    }

    /**
     * Connect two vertices undirected.
     *
     * @param v1 the point of the first vertex.
     * @param v2 the point of the second vertex.
     */
    public void connectVertices(Point v1, Point v2) {
        mgraph.connectUndirected(v1, v2);
    }

    /**
     * Solve the maze using a breadth first search.
     *
     * @return the solved image. Null if no solution.
     */
    public BufferedImage solveBFS(BufferedImage image) {

        BufferedImage solution = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = solution.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        Color solColor = new Color(50, 150, 255, 150);
        // System.out.println(solColor);

        // System.out.println("S: " + start + " E: " + exit);
        List<Point> path = mgraph.breadthFirstPath(start.getValue(), exit.getValue());
        if(path == null){
            return null;
        }
        for (int i = 1; i < path.size(); i++) {
            Point prev = path.get(i - 1);
            Point curr = path.get(i);
            drawPath(solColor, solution, prev, curr);
        }

        return solution;
    }

    /**
     * Draw the solution path onto the buffered image.
     *
     * @param solColor the color to fill the path with.
     * @param sol      the solution image.
     * @param p1       the point to start drawing from.
     * @param p2       the point to stop drawing at.
     */
    private void drawPath(Color solColor, BufferedImage sol, Point p1, Point p2) {
        int dx = p1.x - p2.x;
        int dy = p1.y - p2.y;

        // System.out.printf("dx: %d, dy: %d\n", dx, dy);

        if (dx < 0) {
            for (int i = 0; p1.x + i <= p2.x; i++) {
                sol.setRGB(p1.x + i, p1.y, solColor.getRGB());
            }
        }

        if (dx > 0) {
            for (int i = 0; p1.x - i >= p2.x; i++) {
                sol.setRGB(p1.x - i, p1.y, solColor.getRGB());
            }
        }

        if (dy < 0) {
            for (int i = 0; p1.y + i <= p2.y; i++) {
                sol.setRGB(p1.x, p1.y + i, solColor.getRGB());
            }
        }

        if (dy > 0) {
            for (int i = 0; p1.y - i >= p2.y; i++) {
                sol.setRGB(p1.x, p1.y - i, solColor.getRGB());
            }
        }
    }

    /**
     * Solve the maze using a depth first search.
     *
     * @return the time in seconds it took to solve.
     */
    public float solveDFS() {

        return 0;
    }

}
