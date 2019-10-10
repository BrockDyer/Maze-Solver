package backend.mazefiles;

import backend.maze.Maze;
import backend.util.errors.InvalidMazeException;
import backend.util.errors.NoSolutionException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Read a maze from a png image.
 *
 * @author Brock Dyer.
 */
public class MazePNG {

    /**
     * Check if the current pixel is a branch.
     * This is true if:
     * - has path in 2 non-opposite orthogonal directions.
     *
     * @param x     the x-coord.
     * @param y     the y-coord.
     * @param image the image of the maze.
     * @return true if it is a branch.
     */
    private static boolean isBranch(int x, int y, BufferedImage image) {

        int uy = y - 1;
        int dy = y + 1;
        int lx = x - 1;
        int rx = x + 1;

        Color up = new Color(image.getRGB(x, uy));
        Color down = new Color(image.getRGB(x, dy));
        Color left = new Color(image.getRGB(lx, y));
        Color right = new Color(image.getRGB(rx, y));

        return (up.equals(Color.WHITE) || down.equals(Color.WHITE)) &&
                (left.equals(Color.WHITE) || right.equals(Color.WHITE));
    }

    /**
     * A vertex at the given point must be a neighbor. Check if the map has that vertex.
     *
     * @param p        the point of the vertex we are querying.
     * @param vertex   the point of the vertex we are adding a neighbor to.
     * @param vertices the map of points to vertices.
     * @param maze     the maze being constructed.
     * @return true if a neighbor was added.
     */
    private static boolean checkAndAddNeigbor(Point p, Point vertex, List<Point> vertices, Maze maze) {
        if (vertices.contains(p)) {
            // System.out.println("Connecting vertices");
            maze.connectVertices(vertex, vertices.get(vertices.indexOf(p)));
            return true;
        }
        return false;
    }

    /**
     * Search a map of vertices for neighbors of a given vertex.
     *
     * @param vertices the list of vertex points.
     * @param vertex   the point of the vertex.
     * @param image    the image of the maze.
     * @param maze     the maze being constructed.
     * @return the number of neighbors added. This should be in range [0, 4]
     */
    private static int connectNeighbors(List<Point> vertices,
                                        Point vertex, BufferedImage image, Maze maze) {

        int neighbors = 0;
        int x = vertex.x;
        int y = vertex.y;

        boolean up = true;
        boolean down = true;
        boolean right = true;
        boolean left = true;

        int i = 1;
        while (up || left || down || right) {

            if (up && y - i >= 0 && (new Color(image.getRGB(x, y - i)).equals(Color.WHITE))) {

                Point py = new Point(x, y - i);
                if (checkAndAddNeigbor(py, vertex, vertices, maze)) {
                    neighbors += 1;
                    up = false;
                }
            } else {
                up = false;
            }

            if (down && y + i < image.getHeight() && (new Color(image.getRGB(x, y + i)).equals(Color.WHITE))) {

                Point py = new Point(x, y + i);
                if (checkAndAddNeigbor(py, vertex, vertices, maze)) {
                    neighbors += 1;
                    down = false;
                }
            } else {
                down = false;
            }

            if (left && x - i >= 0 && (new Color(image.getRGB(x - i, y)).equals(Color.WHITE))) {

                Point px = new Point(x - i, y);
                if (checkAndAddNeigbor(px, vertex, vertices, maze)) {
                    neighbors += 1;
                    left = false;
                }
            } else {
                left = false;
            }

            if (right && x + i < image.getWidth() && (new Color(image.getRGB(x + i, y)).equals(Color.WHITE))) {

                Point px = new Point(x + i, y);
                if (checkAndAddNeigbor(px, vertex, vertices, maze)) {
                    neighbors += 1;
                    right = false;
                }
            } else {
                right = false;
            }


            i++;
        }

        return neighbors;
    }

    /**
     * Create a new Maze
     *
     * @param x        the x coord of the start
     * @param y        the y coord of the start
     * @param vertices the list of vertices
     * @return the constructed maze.
     */
    private static Maze initMaze(int x, int y, List<Point> vertices) {
        Point start = new Point(x, y);
        vertices.add(start);
        return new Maze(start);
    }

    /**
     * Read from a buffered image and construct the maze.
     *
     * @return the constructed maze, null if something went wrong.
     * @throws InvalidMazeException if the maze is invalid.
     */
    public static Maze createMazeFromPNG(BufferedImage image) throws InvalidMazeException {

        int height = image.getHeight();
        int width = image.getWidth();

        Point end = null;
        List<Point> vertices = new ArrayList<>();
        Maze maze = null;

        for (int x = 0; x < width; x++) {
            Color pxt = new Color(image.getRGB(x, 0));
            Color pxb = new Color(image.getRGB(x, height - 1));

            if (pxt.equals(Color.WHITE)) {
                if (maze == null) {
                    maze = initMaze(x, 0, vertices);
                } else {
                    end = new Point(x, 0);
                    break;
                }
            }

            if (pxb.equals(Color.WHITE)) {
                if (maze == null) {
                    maze = initMaze(x, height - 1, vertices);
                } else {
                    end = new Point(x, height - 1);
                    break;
                }
            }
        }

        for (int y = 1; y < height - 1; y++) {
            Color pyl = new Color(image.getRGB(0, y));
            Color pyr = new Color(image.getRGB(width - 1, y));

            if (maze != null && end != null) {
                break;
            }

            if (pyl.equals(Color.WHITE)) {
                if (maze == null) {
                    maze = initMaze(0, y, vertices);
                } else {
                    end = new Point(0, y);
                    break;
                }
            }

            if (pyr.equals(Color.WHITE)) {
                if (maze == null) {
                    maze = initMaze(width - 1, y, vertices);
                } else {
                    end = new Point(width - 1, y);
                    break;
                }
            }
        }

        if (maze == null || end == null) {
            System.err.println("Invalid maze! Exiting.");
            throw new InvalidMazeException("Maze missing start and/or end.");
        }

        // System.out.println(end);
        vertices.add(end);
        maze.addVertex(end);
        maze.setExit(end);

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {

                Color pixelColor = new Color(image.getRGB(x, y));
                if (pixelColor.equals(Color.WHITE)) {
                    // Process path

                    // Guaranteed to be within maze edge.
                    if (isBranch(x, y, image)) {
                        // System.out.println("Found a vertex");
                        Point branch = new Point(x, y);
                        // System.out.println(branch.getValue());
                        maze.addVertex(branch);
                        // connectNeighbors(vertices, branch, image, maze);
                        vertices.add(branch);
                    }
                } else {
                    if (!pixelColor.equals(Color.BLACK)) {
                        System.err.println("Bad pixel color! This is an error!");
                        return null;
                    }
                }

            }
        }

        for (Point vertP : vertices) {
            connectNeighbors(vertices, vertP, image, maze);
        }

        return maze;
    }

    public static void main(String[] args) {

        try {
            BufferedImage testImage = ImageIO.read(
                    new File("src/com/github/brockdyer/mazefiles/testpng/test1.png"));
            Maze test = createMazeFromPNG(testImage);
            if (test != null) {
                //test.printStart();
                File solution = new File("src/com/github/brockdyer/mazefiles/testpng/solution.png");

                long sTime = System.currentTimeMillis();
                BufferedImage sol = test.solveBFS(testImage);
                if(sol == null){
                    throw new NoSolutionException("Maze has no solution.");
                }
                sTime = System.currentTimeMillis() - sTime;

                ImageIO.write(sol, "png", solution);
                System.out.printf("Solved maze using breadth first search in %f seconds.", ((double)sTime / 1000));
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch(InvalidMazeException ime){
            System.err.println(ime.getMessage());
        } catch (NoSolutionException nse){
            System.out.println(nse.getMessage());
        }

    }

}
