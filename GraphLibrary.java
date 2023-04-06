import java.io.*;
import java.util.*;

/**
 * library class with necessary static methods to run bfs, get the paths,
 * find which actors are connected, and calculate average separation
 * @author nikhilpande
 * @author nathanmcallister
 */
public class GraphLibrary {

    /**
     * method to perform breadth-first search from a certain actor
     * @param g graph of all the actors and their links
     * @param source root actor
     * @return tree-style graph that holds information on paths to the root
     * @param <V> vertex type = String
     * @param <E> edge label type = set of strings
     */
    public static <V,E> Graph<V,E> bfs(Graph<V,E> g, V source){
        Queue<V> pathQueue = new LinkedList<V>(); // queue for bfs
        Graph<V, E> backTrack = new AdjacencyMapGraph<V, E>(); // initialize backtrack tree
        HashSet<V> visited = new HashSet<V>(); // set of actors we've already seen to avoid repetition

        pathQueue.add(source);
        backTrack.insertVertex(source);
        visited.add(source);

        while (!pathQueue.isEmpty()) {
            V x = pathQueue.remove();

            for (V nb : g.outNeighbors(x)){ // check all neighbors
                if (!visited.contains(nb)){ // if not yet seen, add vertex from parent to child
                    pathQueue.add(nb);
                    visited.add(nb);
                    backTrack.insertVertex(nb);
                    backTrack.insertDirected(x, nb, g.getLabel(x, nb));
                }
            }
        }
        return backTrack;
    }

    /**
     * method to find the path from one actor to the root
     * @param tree bfs backtrack tree
     * @param v actor from whom the path is starting
     * @return list of actors that define the path
     */
    public static <V,E> List<V> getPath(Graph<V,E> tree, V v) {
        ArrayList<V> path = new ArrayList<V>();
        if (!tree.hasVertex(v) || tree.numVertices() == 0) { // boundary case: tree empty or vertex non-existent
            System.out.println("Tree does not have vertex " + v);
        }
        else {
            V current = v;
            path.add(0, current);

            while (tree.inDegree(current) != 0) {

                for (V prev : tree.inNeighbors(current)) { // move to neighbor
                    current = prev;
                }
                path.add(0, current); // add new current vertex to path

            }
        }
        return path;
    }

    /**
     * method to determine which vertices are not in the bfs
     * @param graph universe graph
     * @param subgraph bfs graph
     * @return set of all vertices not included
     */
    public static <V,E> Set<V> missingVertices(Graph<V,E> graph, Graph<V,E> subgraph) {
        Set<V> missingVertices = new HashSet<>();
        for (V v : graph.vertices()) {
            if (!subgraph.hasVertex(v)) { // for each actor, if bfs doesn't have it, put in missing set
                missingVertices.add(v);
            }
        }
        return missingVertices;
    }

    /**
     * method to determine what the average separation is for each actor in a bfs
     * @param tree bfs tree
     * @param root root actor
     * @return number (double) that represents the average
     */
    public static <V,E> double averageSeparation(Graph<V,E> tree, V root) {
        int totalDist = 0;
        totalDist = asHelp(tree, root, 1); // recurse with helper function, accumulating steps for each new actor

        return (double) totalDist/tree.numVertices();
    }

    private static <V,E> int asHelp(Graph<V,E> tree, V root, double steps) {
        int total = 0;
        for (V vertex : tree.outNeighbors(root)) { // move to neighbors
            total += steps;
            total += asHelp(tree, vertex, steps + 1); // neighbors call this recursively and add path+1 to total
        }
        return total;
    }

    // testing these functions on the test files
    public static void main(String[] args) throws IOException {
        Map<Integer, String> actorMap = LoadGraph.readIDFile("inputs/actorsTest.txt");
        Map<Integer, String> movieMap = LoadGraph.readIDFile("inputs/moviesTest.txt");
        Map<String, ArrayList<String>> MovieActorsMap = LoadGraph.readActMovFile(actorMap, movieMap,"inputs/movie-actorsTest.txt");

        Graph<String, HashSet<String>> game = LoadGraph.createGraph(actorMap, movieMap, MovieActorsMap);
        Graph<String, HashSet<String>> back = bfs(game, "Alice");
        System.out.println(averageSeparation(back, "Alice"));
        System.out.println("\n");
        System.out.println(game);
        System.out.println("\n");
        System.out.println(back);
        System.out.println("\n");
        System.out.println(getPath(back, "Bob"));
        System.out.println("\n");
        System.out.println(missingVertices(game, back));
    }
}
