import java.util.HashSet;

/**
 * @author myname
 */
public class BaconTesting {
    public static void main(String[] args) {
        Graph<String, HashSet<String>> test = new AdjacencyMapGraph<String, HashSet<String>>();

        // hashsets with movies
        HashSet<String> Kb2B = new HashSet<String>();
        HashSet<String> Kb2A = new HashSet<String>();
        HashSet<String> A2C = new HashSet<String>();
        HashSet<String> B2C = new HashSet<String>();
        HashSet<String> B2A = new HashSet<String>();
        HashSet<String> C2D = new HashSet<String>();
        HashSet<String> N2Nf = new HashSet<String>();

        Kb2B.add("A Movie");
        Kb2A.add("A Movie");
        Kb2A.add("E Movie");
        A2C.add("D Movie");
        B2C.add("C Movie");
        B2A.add("A Movie");
        C2D.add("B Movie");
        N2Nf.add("F Movie");

        // hard coding the test graph with vertices and edges
        test.insertVertex("Kevin Bacon");
        test.insertVertex("Charlie");
        test.insertVertex("Alice");
        test.insertVertex("Bob");
        test.insertVertex("Dartmouth (Earl thereof)");
        test.insertVertex("Nobody");
        test.insertVertex("Nobody's Friend");
        test.insertUndirected("Kevin Bacon", "Bob", Kb2B);
        test.insertUndirected("Kevin Bacon", "Alice", Kb2A);
        test.insertUndirected("Alice", "Charlie", A2C);
        test.insertUndirected("Alice", "Bob", B2A);
        test.insertUndirected("Bob", "Charlie", B2C);
        test.insertUndirected("Charlie", "Dartmouth (Earl thereof)", C2D);
        test.insertUndirected("Nobody", "Nobody's Friend", N2Nf);

        // show the graph
        System.out.println("Test graph:");
        System.out.println(test + "\n");

        // show the bfs graph from Kevin Bacon
        Graph<String, HashSet<String>> kbTest = GraphLibrary.bfs(test, "Kevin Bacon");
        System.out.println("BFS graph from Kevin Bacon:");
        System.out.println(kbTest + "\n");

        // shortest path from charlie
        System.out.println("Path to Charlie:");
        System.out.println(GraphLibrary.getPath(kbTest, "Charlie") + "\n");

        // missing vertices
        System.out.println("Missing vertices:");
        System.out.println(GraphLibrary.missingVertices(test, kbTest) + "\n");

        // average separation
        System.out.println("Average separation for Kevin Bacon in this universe:");
        System.out.println(GraphLibrary.averageSeparation(kbTest, "Kevin Bacon"));
    }
}
