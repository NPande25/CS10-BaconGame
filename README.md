# Nikhil Pande
## CS10 23W - Problem Set 4
### Kevin Bacon Game
The Kevin Bacon game, the subject of Problem Set 4, calculates the number of 'degrees of separation' between Kevin Bacon and each actor in the database. For each actor, the algorithm decides a path from the actor to Kevin Bacon, where each actor has been in a movie with the next actor along the path.

In deciding the shortest path from one actor vertex to Kevin Bacon, or whichever actor the user selects as the nexus, the algorithm implements a breadth-first search and outputs the path of vertices. The files also encode a user interface where the user can find a path from one actor to the center, decide which actor will be the center, find the average degree of separation for every actor, and more.
