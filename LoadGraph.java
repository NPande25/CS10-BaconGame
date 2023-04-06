import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * class with the necessary functions to read and parse the files and create
 * maps and graphs out of the data
 * @author nikhilpande
 * @author nathanmcallister
 */
public class LoadGraph {

    /**
     * method reads file and creats a map with its data
     * @param filename
     * @return map that matches id numbers to a corresponding actor/movie
     */
    public static Map<Integer, String> readIDFile(String filename) {
        BufferedReader input = null;
        Map<Integer, String> ids = new HashMap<Integer, String>();

        try {
            input = new BufferedReader(new FileReader(filename));
            String line;

            while ((line = input.readLine()) != null) {
                String[] lineList = line.split("\\|");
                int id = Integer.parseInt(lineList[0]); // change type to integer
                ids.put(id, lineList[1]); // insert into map, key = id and value = actor/movie name
            }

        }
        catch (IOException e) { // if the file is not found
            System.out.println("File " + filename + " not found");
        }
        finally {
            try {
                input.close();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return ids;
    }

    /**
     *
     * @param actors map of ids to actor names
     * @param movies map of ids to movie names
     * @param movActFile map of movies to corresponding actors in them
     * @return
     * @throws IOException if file not found
     */
    public static Map<String, ArrayList<String>> readActMovFile(Map<Integer, String> actors, Map<Integer, String> movies, String movActFile) throws IOException {
        BufferedReader input = null;
        Map<String, ArrayList<String>> movieactor = new HashMap<String, ArrayList<String>>();

        try { // try reading the file
            input = new BufferedReader(new FileReader(movActFile));
            String line;

            while ((line = input.readLine()) != null) {
                String[] lineList = line.split("\\|");
                int movieid = Integer.parseInt(lineList[0]);
                String movie = movies.get(movieid);
                int actorid = Integer.parseInt(lineList[1]);
                String actor = actors.get(actorid);

                if (!movieactor.containsKey(movie)) { // if the movie isn't yet in map, set it as key, create new map, and add actor
                    movieactor.put(movie, new ArrayList<String>());
                    movieactor.get(movie).add(actor);
                } else { // if the movie is already in map, add this actor to its set of actors
                    movieactor.get(movie).add(actor);
                }
            }
        }
        catch (IOException e) {
            System.out.println("File " + movActFile + " not found.");
        }
        finally {
            try {
                input.close();
            }
            catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        return movieactor;
    }

    /**
     *
     * @param actor ids to actors map
     * @param movie ids to movies map
     * @param movieactors movies to actors map
     * @return a graph that connects all the actors
     * @throws IOException
     */
    public static Graph<String, HashSet<String>> createGraph(Map<Integer, String> actor, Map<Integer, String> movie, Map<String, ArrayList<String>> movieactors) {
        Graph<String, HashSet<String>> game = new AdjacencyMapGraph<String, HashSet<String>>();

        // loop through actors and insert vertices
        for (int id : actor.keySet()) {
            game.insertVertex(actor.get(id));
        }

        // loop through all movies and actor lists
        for (String mov : movieactors.keySet()) {
            for (int i = 0; i < movieactors.get(mov).size(); i++) {
                for (int j = i + 1; j < movieactors.get(mov).size(); j++) { // this loop structure cuts the time in half
                    if (!game.hasEdge(movieactors.get(mov).get(i), movieactors.get(mov).get(j))) {
                        // add edge from actor to each other actor in the set
                        game.insertUndirected(movieactors.get(mov).get(i), movieactors.get(mov).get(j), new HashSet<String>());
                        game.getLabel(movieactors.get(mov).get(i), movieactors.get(mov).get(j)).add(mov);
                    }
                    else { // if they already have a link, add this new movie to their set
                        game.getLabel(movieactors.get(mov).get(i), movieactors.get(mov).get(j)).add(mov);
                    }
                }
            }
        }

        return game;
    }

    public static void main(String[] args) throws IOException {
        // testing
        Map<Integer, String> actorMap = readIDFile("inputs/actorsTest.txt");
        Map<Integer, String> movieMap = readIDFile("inputs/moviesTest.txt");
        Map<String, ArrayList<String>> MovieActorsMap = readActMovFile(actorMap,movieMap, "inputs/movie-actorsTest.txt");

        Graph<String, HashSet<String>> game = createGraph(actorMap, movieMap, MovieActorsMap);
        System.out.println(game);

    }
}
