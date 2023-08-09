package Solver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class PathSolver {
	int width; // the width of a layer
	int height; // height of a layer
	int depth; // depth of a layer
	int start; // holds the index for the start of the maze
	int end; // holds the index for the end of the maze
	int totalIndex; // holds the total amount of vertices
	char[][] storedMaze; // stores the maze characters
	int[] parentList; // holds the parent of each vertex
	int[] distList; // holds the distance to the start vertex, of each vertex.
	static char[] symbol = { 'x', 'X', 'o', 'O', 's', 'S', 'E', 'e' }; // holds the acceptable symbols of the maze

	Scanner in; // will read user inputs to create the maze
	ArrayList<LinkedList<Integer>> mazeGraph; // the adjacency list of the maze

	/**
	 * This constructor basically solves for the shortest path of the maze. Before
	 * doing so, it initializes certain variables need to complete the task, such as
	 * the width, height, and depth of the maze; totalIndex, parent list, distance
	 * list,etc.
	 * 
	 * @param width
	 * @param height
	 * @param depth
	 */
	public PathSolver(int width, int height, int depth) {
		
		// initializing things
		totalIndex = (height * depth) * width;
		parentList = new int[totalIndex];
		distList = new int[totalIndex];
		this.width = width;
		this.height = height;
		this.depth = depth;
		in = new Scanner(System.in); // used to read user input for the maze
		storedMaze = new char[(height * depth)][width]; // sets the size of the array
		mazeGraph = new ArrayList<>(totalIndex); // gets the total number of items for the ArrayList size

		// fills the two list with appropriate stubs
		for (int i = 0; i < totalIndex; i++) { // sets up the list needed
			mazeGraph.add(new LinkedList<Integer>());
			parentList[i] = -1;
			distList[i] = Integer.MAX_VALUE;
		}
		
		saveMazeData(); // this method stores the user given maze into a 2D array
		createMazeGraph(); // creates a graph representation with the 2D array
		printArray();
		System.out.println("\nThe solution is: " + findShortestPath());
		System.out.println("The length of the shortest path is: " + getShortestPathLength() +"\n");

	}

	/**
	 * 
	 * This method visits all the nodes in the adjacency list, starting at the
	 * starting vertex and visits all the nodes. While doing so, the parent and
	 * distances from the starting node are updated, using 'distList' and
	 * 'parentList'.
	 */
	public String findShortestPath() {
		LinkedList<Integer> unvisited = new LinkedList<>();// this will hold all the unvisited nodes
		
		distList[start] = 0; // the starting node will have a distance to itself of 0.
		parentList[start] = -2; // the starting node will be distinguished by the parent value of -2.
		unvisited.add(start); // add the start node to get the while loop started
		int vertex = -1; // will hold the current vertex

		while (!unvisited.isEmpty()) {
			vertex = unvisited.remove();
			

			for (Integer i : mazeGraph.get(vertex)) {

				if (distList[i] > distList[vertex] + 1) { // checks if the current distance of the adjacent node is less
															// than vertex+1.
					distList[i] = distList[vertex] + 1; // changes the adjacent vertex's distance to the start to the
														// current vertex distance+1.
					parentList[i] = vertex; // changes the parent path of the adjacent vertex to the current vertex
					unvisited.add(i); // add's the adjacent node to the list of unvisited nodes.
				}

			}

		}

		return getPathCoord(); // has the strong of directions for the shortest path
	}

	/**
	 * This method is helper method for 'findShortestPath' used to get the coordinates of the shortest path. i.e. "N",
	 * "S", "E","W", "U","D". and returns it.
	 * 
	 * @return
	 */
	private String getPathCoord() {
		LinkedList<Integer> shortPath = new LinkedList<>(); // holds the vertices of the shortest path
		int current;
		shortPath.add(end);
		current = parentList[end];
		
		if (current == -1) { // if the end path dosen't have a parent, we know there is not solution to the maze.
			return "No Solution";
		}

		do {// adds all the vertices from the ending vertex the starting vertex to the shortPath LinkedList
			shortPath.add(current);
			current = parentList[current];

		} while (current != -2); // once it reaches the starting vertex the loop terminates 

		return getDirections(shortPath); //returns the coordinates of the shortest path

	}

	/**
	 * Helper method for getPathCoord. This method compares adjacent values in the
	 * queue which contains the shortest path, the determines the direction from one
	 * vertex to another. i.e. "N", "S", "W", "E", "U", "D".
	 * 
	 * @param path
	 * @return
	 */
	private String getDirections(LinkedList<Integer> path) {

		int current;// starting vertex being evaluated.
		String result = ""; // in the end this will contain all the solution to be returned.

		while (!path.isEmpty()) { // while the queue is not empty keep comparing values.
			current = path.pollLast(); // get the last element in the list
			if (path.isEmpty())
				return result; // ensures we don't access a null value later
			result = result + compare(current, path.peekLast()) + " "; // add the direction to get to i.e "N" to the result.
		}
		return result; // returns the solution.
	}

	/**
	 * Helper method for getDirection, this method directly compares two vertices
	 * and returns the direction need to get from the 'begin' vertex to the 'end'
	 * vertex.
	 * 
	 * @param begin
	 * @param end
	 * @return
	 */
	private String compare(int begin, int end) {
		int area = width * height; // area of the matrix, is used frequently in this method.

		if (end / (area) == begin / (area)) { // checks to see if the vertices being compared are on the same layer
			if (end - begin == -1) {
				return "W"; // west
			}
			if (end - begin == 1) {
				return "E"; // east
			}
			if (begin + width == end) {
				return "S"; // south
			}
			if (begin - width == end) {
				return "N"; // north
			}

		}

		else {
			if (begin % area == begin % area && (begin % area) % width == (end % area) % width) { // if the vertices are on different layers,
																								//the directions can only be down (D) or Up (U).
				if (begin < end) {
					return "D";
				}
				if (begin > end) {
					return "U";
				}

			}

		}
		return null;
	}

	/**
	 * This method is used for creating the adjacency list of the maze. This
	 * is done by walking through the 2D array and creating in-bound nods towards a
	 * floors, represented by either 'o' or 'O', 's' or 'S', 'e' or 'E'
	 * 
	 */
	public void createMazeGraph() {
		int count = 0; // index for the graph representation
		
		for (int i = 0; i < storedMaze.length; i++) { // I accept upper and lower-case representation, I'm user friendly lol.
			for (int j = 0; j < storedMaze[i].length; j++) {
				if (storedMaze[i][j] == 'o' || storedMaze[i][j] == 'O') { // this value is a floor.
					linkAdj(count); // point all adjacent vertices to this floor 
					++count;

				} else if (storedMaze[i][j] == 's' || storedMaze[i][j] == 'S') { // this value the starting point.
					start = count; // this is the starting vertex
					linkAdj(count); // still counts as a floor, so create in-bound vertices to this vertex
					++count;
				} else if (storedMaze[i][j] == 'e' || storedMaze[i][j] == 'E') { // this value is the end point.
					end = count; // this is the ending vertex
					linkAdj(count); // still counts as a floor, so create in-bound vertices to this vertex
					++count;
				} else {
					++count;
				}
			}
		}

	}

	/**
	 * Helper method for createMaze. This method checks for floors in the 3D maze, once a floor is found this
	 * method sends in-bound edges to that maze from all of its adjacent walls and
	 * floors.
	 * 
	 * @param index
	 */
	private void linkAdj(int index) {
		
		int size = width * height; //basically the area of the each layer
		int layerOfIndex = index / size;
		
		// West in-bound value
		if (index % width != 0) { // checks to see if index is not on the left edge of the array
			// System.out.println("added");
			mazeGraph.get(index - 1).add(index); // creates in bound value from the the left value of the index to index
		}

		// East in-bound value

		if (index % width != width - 1) { // checks to see if index is not on the right edge of the array
			mazeGraph.get(index + 1).add(index);
		}
		// North in-bound value
		if (index - width >= 0 && (index - width) / size == layerOfIndex) { // checks to see if index is not on the very
																			// top edge of the array
			mazeGraph.get(index - width).add(index);
		}

		// South in-bound value
		if (index + width < totalIndex && (index + width) / size == layerOfIndex) {// checks to see if the index is not
																					// at the very bottom edge of the
																					// array
			mazeGraph.get(index + width).add(index);
		}
		// Downwards in-bound value
		if (index + (height * width) < totalIndex) {
			mazeGraph.get(index + (height * width)).add(index);
		}

		// Upward in-bound value
		if (index - (height * width) >= 0) {
			mazeGraph.get(index - (height * width)).add(index);
		}
	}

	/**
	 * This method is used to store the user inputed ASCII maze into a 2D array
	 * named 'storedMaze', which will be used by createMaze.
	 */
	public void saveMazeData() {
		char[] line; // will hold the string of characters provided by the user
		System.out.println("Give me values that are " + width + " char long \n");

		for (int i = 0; i < height * depth; i++) {

			line = getLine().toCharArray(); // gets the input user line
			storedMaze[i] = line; // stores the line into the 2D array

		}

	}
	
	public int getShortestPathLength() {
		if(distList[end] == Integer.MAX_VALUE) return 0;
		
		return distList[end];
	}

	/**
	 * This method retrieves a line from a user input to be stored in the 2D array,
	 * storedMaze.
	 * 
	 * @return line
	 */
	private String getLine() {
		String line = "";
		do {
			line = in.nextLine();
		} while (line.equals("") || line.length() != width); // if the line is blank or the line size is incorrect,
																// ignore the data

		return line;
	} // getLine Provided by Professor Foxwell. "I'm not cheating I swear, lol"
	

	/**
	 * This method is used to display user inputed maze in a pictorial form.
	 */
	public void printArray() {
		int count =0;
		System.out.println("\n Your Maze\n");
		for (int i = 0; i < storedMaze.length; i++) {
			if(count % height ==0 ) System.out.println();
			count++;
			for (int j = 0; j < storedMaze[i].length; j++) {
				System.out.print(storedMaze[i][j] + "\t");
			}
			System.out.println("\n");
		}
	}

	

}
