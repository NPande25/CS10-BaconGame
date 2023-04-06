import java.util.*;

public class BaconGame {
    static Graph<String, HashSet<String>> universe; // graph connecting all the actors
    String currCenter; // current center of the universe, root of bfs tree
    Graph<String, HashSet<String>> currBFS; // current bfs backtrack tree, root is current universe center


    public BaconGame(String centerOfUniverse) {
        try {
            // initial center of universe is passed in when BaconGame is instantiated
            this.currCenter = centerOfUniverse;

            // read actor & movie files, assembly MovieActorsMap using methods in LoadGraph
            Map<Integer, String> actorMap = LoadGraph.readIDFile("inputs/actors.txt");
            Map<Integer, String> movieMap = LoadGraph.readIDFile("inputs/movies.txt");
            Map<String, ArrayList<String>> MovieActorsMap = LoadGraph.readActMovFile(actorMap,movieMap, "inputs/movie-actors.txt");
            universe = LoadGraph.createGraph(actorMap, movieMap, MovieActorsMap);
            currBFS = GraphLibrary.bfs(universe, currCenter);
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     * reads initial character input from user: c, d, i, p, s, u, q
     * collects appropriate additional inputs for given initial character input
     * calls appropriate method to carry out task
     */
    public void handleKeyPress() {
        System.out.print(currCenter + " game: choose a, c, b, d, i, p, s, u, q: ");

        // scanner collects all input in the handleKeyPress method
        Scanner opIn = new Scanner(System.in);
        String input = opIn.nextLine();

        // covers all cases for input: a, c, b, d, i, p, s, u, q
        switch (input) {
            // no additional input need, call asep method
            case "a" -> asep();

            // collects a number and passes it to the listSep method
            case "c" -> {
                System.out.print("input integer: ");
                String num = opIn.next();
                listSep(num);
            }
            // no additional input needed, call bestCenter method
            case "b" -> bestCenter();

            // collects low and high bounds, passes them to degreeList function
            case "d" -> {
                System.out.print("input lower limit: ");
                String dlow = opIn.next();
                System.out.print("input upper limit: ");
                String dhigh = opIn.next();

                degreeList(dlow, dhigh);
            }
            // no additional input needed, call infiniteSep method
            case "i" -> infiniteSep();

            // collects name of actor to find path from, passes it to findPath method
            case "p" -> {
                System.out.print("input name: ");
                String pname = opIn.nextLine();
                findPath(pname);
            }
            // collect upper and lower bounds of separation, passes them to fullListSep method
            case "s" -> {
                System.out.print("input lower limit: ");
                String slow = opIn.nextLine();
                System.out.print("input upper limit: ");
                String shigh = opIn.nextLine();

                fullListSep(slow, shigh);
            }
            // collects name of actor to be made new center of universe, passes it to center method
            case "u" -> {
                System.out.print("input name: ");
                String uname = opIn.nextLine();
                center(uname);
            }
            // no additional input needed, send to quit method
            case "q" -> quit();
            // catch all: if input doesn't match on of the cases above, have user try again
            default -> {
                System.out.println("invalid input, try again");
                handleKeyPress();
            }
        }
        opIn.close(); // done reading input
    }

    /**
     * start menu
     *
     * lists options with descriptions, calls handleKeyPress method to receive user input
     */
    public void start() {
        System.out.println("a: print the average separation for the current universe from the current center");
        System.out.println("c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation");
        System.out.println("b: print the best center of the universe, determined by smallest average separation");
        System.out.println("d <low> <high>: list actors sorted by degree, with degree between low and high");
        System.out.println("i: list actors with infinite separation from the current center");
        System.out.println("p <name>: find path from <name> to current center of the universe");
        System.out.println("s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high");
        System.out.println("u <name>: make <name> the center of the universe");
        System.out.println("q: quit game");
        System.out.println(currCenter + " is now the center of the acting universe, connected to " + (currBFS.numVertices() - 1) + "/" + (universe.numVertices()-1) + " actors with average separation " + GraphLibrary.averageSeparation(currBFS, currCenter) + "\n");

        handleKeyPress(); // ready to receive user input
    }

    /**
     * a: print the average separation for the current universe from the current center
     */
    public void asep(){
        // averageSeparation method from GraphLibrary handles the printing
        System.out.println("Average separation between " + currCenter + " and the rest of the universe: " + GraphLibrary.averageSeparation(currBFS, currCenter));
        System.out.println("\n");
        handleKeyPress(); // ready for more input
    }

    /**
     * c: list top (positive number) or bottom (negative) <k> centers of the universe, sorted by average separation
     *
     * takes int input k from handleKeyPress method
     * outputs list of actors as specified
     * uses a map to store actors and their average separations so that when sorting the list, it can simply
     * draw from the map (an O(1) operation). Trades memory for much better runtime efficiency.
     *
     * @param num positive or negative number specifying how many actors to list and their average separation
     */
    public void listSep(String num) {
        try{
            List<String> out = new ArrayList<String>(); // final list with top or bottom k actors
            Map<String, Double> sepList = new HashMap<String, Double>();

            int k = Integer.parseInt(num);

            // check if k is greater than the number of actors available
            if (Math.abs(k) > currBFS.numVertices()) {
                System.out.println("Please choose a lower number of actors.");
            }
            else {
                System.out.println("Printing the " + Math.abs(k) + " best (if positive input) or worst (if negative input) actors, sorted by average separation:");

                // create map with keys as actors and values as average separations
                for (String actor : currBFS.vertices()) {
                    sepList.put(actor, GraphLibrary.averageSeparation(GraphLibrary.bfs(universe, actor), actor));
                }

                // list of keys
                List<String> list = new ArrayList<String>(sepList.keySet()); // put all the actors in the list, soon to be sorted

                if (k >= 0) {
                    // multiply average sep by a large factor so they are comparable when cast as ints. Otherwise,
                    // the differences will be negligible
                    Comparator<String> c = Comparator.comparingInt((String a) -> (int) (sepList.get(a) * 1000000));
                    list.sort(c);

                    for (int i = 0; i < k; i++) {
                        out.add(list.get(i) + " " + sepList.get(list.get(i)));
                    }
                    System.out.println(out);

                } else {
                    Comparator<String> c = Comparator.comparingInt((String a) -> (int) (sepList.get(a) * 1000000));
                    list.sort(c);

                    for (int i = list.size() - 1; i > list.size() - 1 + k; i--) {
                        out.add(list.get(i) + " " + sepList.get(list.get(i)));
                    }
                    System.out.println(out);
                }
            }
            System.out.println("\n");
            handleKeyPress(); // ready to receive more input
        }
        catch(Exception e) {
            System.out.println("Invalid input");
            handleKeyPress();
        }
    }

    /**
     * b: print the best center of the universe, determined by smallest average separation
     *
     * calls the listSep function with input 1
     */
    public void bestCenter() {
        listSep("1"); // call listSep with input 1
        System.out.println("\n");
        handleKeyPress(); // ready to receive more input
    }

    /**
     * d: list actors sorted by degree, with degree between low and high
     *
     * takes in low and high bounds
     * outputs actors with degrees between (inclusive) low and high
     *
     * @param low lower bound
     * @param high upper bound
     */
    public void degreeList(String low, String high) {
        try{
            List<String> out = new ArrayList<String>(); // list to add to and print

            // parse strings into ints
            int l = Integer.parseInt(low);
            int h = Integer.parseInt(high);

            // check that l is not greater than h
            if (l >= h) {
                System.out.println("Invalid bounds");
            }
            else {
                // for all actors, if their degree is between low and high, add them to out list
                for (String vertex : universe.vertices()) {
                    if (universe.outDegree(vertex) <= h && universe.inDegree(vertex) >= l) {
                        out.add(vertex);
                    }
                }
                // printing
                System.out.println("Displaying actors between degrees " + l + " and " + h + ":");
                System.out.println(out);
            }
            handleKeyPress(); // ready for more input
        }
        catch(Exception e){
            System.out.println("Invalid input");
            handleKeyPress();
        }
    }

    /**
     * i: list actors with infinite separation from the current center
     *
     * lists actors with no path to the current center
     */
    public void infiniteSep() {
        // call GraphLibrary method missingVertices on all the actors and the current BFS
        Set<String> missing = GraphLibrary.missingVertices(universe, currBFS);
        // print the set
        System.out.println(missing);
        // print additional info
        System.out.println("Number of people not connected to " + currCenter + ": " + missing.size());
        System.out.println("\n");
        handleKeyPress(); // ready for more input
    }

    /**
     * p: find path from <actor> to current center of the universe
     *
     * @param actor actor to find path from
     */
    public void findPath(String actor) {
        // check if actor exists
        if (!universe.hasVertex(actor)) {
            System.out.println(actor + " is not a valid actor.");
        }
        // check if actor is connected to the current center
        else if (!currBFS.hasVertex(actor)) {
            System.out.println(actor + " is not connected to " + currCenter);
        }
        // else, get the path and print it
        else {
            List<String> path = GraphLibrary.getPath(currBFS, actor);
            Collections.reverse(path); // getPath gets the path to the actor, we want the reverse path
            // print
            System.out.println(actor + "'s " + currCenter + " number is " + (path.size() - 1));
            System.out.println("Here is the path:");
            System.out.println(path);
            // print each connection and the movie(s) they were in together
            for (int i = 1; i < path.size(); i++) {
                System.out.println(path.get(i-1) + " appeared in " + currBFS.getLabel(path.get(i), path.get(i-1)) + " with " + path.get(i));
            }
        }
        System.out.println("\n");
        handleKeyPress(); // ready for more input
    }

    /**
     * s: list actors sorted by non-infinite separation from the current center, with separation between low and high (inclusive)
     *
     * @param low lower bound
     * @param high upper bound
     */
    public void fullListSep(String low, String high) {
        try{
            List<String> list = new ArrayList<String>();

            // parse strings into ints
            int l = Integer.parseInt(low);
            int h = Integer.parseInt(high);

            // check that the lower bound is not greater than the upper bound
            if (l >= h) {
                System.out.println("Invalid bounds.");
            }
            // else, for each actor in the current BFS, see if their separation is within the bounds
            else {
                for (String actor : currBFS.vertices()) {
                    int num = GraphLibrary.getPath(currBFS, actor).size() - 1;
                    if (num >= l && num <= h)
                        list.add(actor + ": " + num);
                }
                // print list of actors
                System.out.println("Displaying actors with " + currCenter + " number between " + l + " and " + h + ":");
                System.out.println(list);
            }
            System.out.println("\n");
            handleKeyPress(); // ready for more input
        }
        catch(Exception e) {
            System.out.println("Invalid input");
            handleKeyPress();
        }
    }

    /**
     * u: make <actor> the center of the universe
     * @param actor actor to be made the center
     */
    public void center(String actor) {
        // check that the actor exists
        if (universe.hasVertex(actor)) {
            currCenter = actor;
            currBFS = GraphLibrary.bfs(universe, actor);
            System.out.println(actor + " is now the center of the acting universe, connected to " + (currBFS.numVertices() - 1) + "/9235 actors with average separation " + GraphLibrary.averageSeparation(currBFS, actor) + "\n");
        }
        // else, print error message
        else {
            System.out.println(actor + " is not a valid actor");
        }
        System.out.println("\n");
        handleKeyPress(); // ready for more input
    }

    /**
     * q: quit game
     */
    public void quit() {
        System.out.println("Game over!");
        System.exit(0); // quit
    }

    // runs the game!
    public static void main(String[] args) {
        BaconGame game = new BaconGame("Kevin Bacon"); // pass in Kevin Bacon as the initial center
        game.start(); // call start to start the game!
    }
}

