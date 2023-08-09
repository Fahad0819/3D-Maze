package Test;

import java.util.InputMismatchException;
import java.util.Scanner;

import Solver.PathSolver;

public class Menu {
	Scanner in;// will take the user input
	int switcher = -1;

	public Menu() {
	
		in = new Scanner(System.in);
		int switcher = -1; // dictates the switch statement

		do {
			try {
				startMenu(); // generates the start-up menu
				switcher = in.nextInt();
				switch (switcher) {

				default: { // if value is not valid, wait for a valid answer
					System.out.println("\nType a valid option, please:\n");
					break;
				}
				case 1: {
					solveMenu();
					break;
				}

				case 2:
					System.exit(0);
				}

			} catch (InputMismatchException ime) {
				System.out.println("\nYou must be a samus main...smh thats not valid sir\n");
				in.next();
			}
		} while (switcher != 2); // may fix later
		System.exit(0);
	}

	public void startMenu() {

		System.out.println("1.Solve a maze optimally"); // solves for the shortest path
		System.out.println("2.Quit"); // terminates the program

	}

	/**
	 * This method prompts a user to input the width, height, and depth of the 3D
	 * maze you want to solve.
	 * 
	 */
	public void solveMenu() {
		int width; // width of the maze
		int height; // height of the maze
		int depth; // depth of the maze

		System.out.println("What is the width of your maze:");
		width = in.nextInt();
		System.out.println("What is the height of your maze:");
		height = in.nextInt();
		System.out.println("What is the depth of your maze:");
		depth = in.nextInt();
		new PathSolver(width, height, depth);
	}

	public static void main(String[] args) {
		new Menu();
	}

}
