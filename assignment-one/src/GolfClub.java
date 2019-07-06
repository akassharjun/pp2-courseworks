import java.util.*;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.toMap;
import java.util.AbstractMap.SimpleEntry;
/**
 * Golf Club
 * The golf club program implements an application that
 * allows a golf club to manage the golfers and their
 * scores.
 *
 * @author Akassharjun Shanmugarajah
 */

public class GolfClub {
	private static Map<String, Integer> scores = new HashMap<>();
	//	private static Map<String, Integer> tempScores = new HashMap<>();
	// STRING will be the name of the ACTION
	// SimpleEntry will store the entry of the score/ or the necessary data.
	private static Stack<SimpleEntry<String, Map<String, Integer>>> previousActions = new Stack<>();
	private static Stack<SimpleEntry<String, Map<String, Integer>>> undoneActions = new Stack<>();

	private static Map<String, Integer> deletedScores = new HashMap<>();
	private static Scanner scanner = new Scanner(System.in);
	private static final String DIVIDER = "+----------------------------+-----------+%n";
	// The entry point for the program.

	public static void main(String[] args) {
		int option;
		do {
			// printing the menu
			System.out.println("Welcome to Springfield Golf Club.");
			System.out.println("Select an option");
			System.out.println("\t1) Enter Scores");
			System.out.println("\t2) Find Golfer");
			System.out.println("\t3) Display Scoreboard");
			System.out.println("\t4) Undo Action");
			System.out.println("\t5) Redo Action");
			System.out.println("\t6) Exit Program");
			System.out.print("> ");

			// if the user hasn't entered a valid integer, it will store 0 to option
			// making it ask for an option again.
			option = scanner.hasNextInt() ? scanner.nextInt() : 0;
			scanner.nextLine();

			switch (option) {
				case 1:
					enterScores();
					break;
				case 2:
					findGolfer();
					break;
				case 3:
					displayScoreboard();
					break;
				case 4:
					undoActions();
					break;
				case 5:
					redoActions();
					break;
				case 6:
					System.out.println("Quitting program.");
					break;
				default:
					System.out.println("Invalid Option! Please try again!");
			}
			System.out.println();
		} while (option != 6);
	}

	/**
	 * This method is used to display the score menu
	 */
	private static void scoreMenu() {
		System.out.println("Scores Menu");
		System.out.println("\t1) Add Scores");
		System.out.println("\t2) Edit Score");
		System.out.println("\t3) Delete Score");
		System.out.println("\t4) Restore Score");
	}

	/**
	 * OPTION 1 (Scores Menu)
	 * This method is used to add a score
	 * for a golfer.
	 */
	private static void addScore() {
		System.out.format(DIVIDER);
		int numberOfGolfers = getIntegerInput("Enter the number of golfers");
		// to store values temporarily
		Map<String, Integer>  tempScores = new HashMap<>();
		for (int count = 1; count < numberOfGolfers + 1; count++) {
			System.out.printf("GOLFER %d\n", count);
			// prompting and getting the name
			System.out.print("Enter name : ");
			String name = scanner.nextLine().toUpperCase();

			int numberOfStrokes = getIntegerInput("Enter the number of strokes : ");
			// repeating until user enters an integer that satisfies the given condition.
			while (numberOfStrokes <= 18 || numberOfStrokes >= 108) {
				System.out.println("Please enter a value between 18 - 108");
				numberOfStrokes = getIntegerInput("Enter the number of strokes : ");
			}

			// checking if the golfer already exists
			if (scores.containsKey(name)) {
				System.out.print("This golfer already exists, would you like to overwrite the score? (Y for Yes) : ");
				String response = scanner.next().toUpperCase();
				// if the user responds Y, then the score will be overwritten.
				if (response.equals("Y")) {
					scores.replace(name, numberOfStrokes);
					System.out.println("Overwrote the score successfully.");
				} else {
					System.out.println("Did not overwrite the score.");
				}
				scanner.nextLine();
			} else {
				// storing the values in the hash map if the user doesn't exist in the hash map.
				scores.put(name, numberOfStrokes);
				// storing the values, along with this action name in another hash map in case the user wants to undoActions this action.
				tempScores.put(name, numberOfStrokes);
			}

			System.out.format(DIVIDER);
		}
		// for handling undoActions/redoActions
		String actionName = previousActions.size() + 1 + ".ADD";
		previousActions.push(new SimpleEntry<>(actionName, tempScores));
	}

	/**
	 * OPTION 2 (Scores Menu)
	 * This method is used to edit a golfer's score
	 */
	private static void editScore() {
		if (scores.isEmpty()) {
			System.out.println("There are no golfers to be edited");
			return;
		}
		displayTable(scores);
		// prompting and getting the name
		System.out.print("Enter the name of the golfer you would like to edit : ");
		String golferName = scanner.nextLine().toUpperCase();

		if (scores.containsKey(golferName)) {
			int numberOfStrokes = getIntegerInput("Enter the new number of strokes : ");
			// repeating until user enters an integer that satisfies the given condition.
			while (numberOfStrokes <= 18 || numberOfStrokes >= 108) {
				System.out.println("Please enter a value between 18 - 108");
				numberOfStrokes = getIntegerInput("Enter the new number of strokes : ");
			}
			int previous = scores.get(golferName);
			scores.replace(golferName, numberOfStrokes);
			System.out.println("Successfully edited the golfer's score");
			// for handling undoActions/redoActions
			String actionName = previousActions.size() + 1 + ".EDIT";
			Map<String, Integer> temp = new HashMap<>();
			temp.put(golferName, previous);
			previousActions.push(new SimpleEntry<>(actionName, temp));
		} else {
			System.out.println("This golfer does not exist.");
		}
	}

	/**
	 * OPTION 3 (Scores Menu)
	 * This method is used to delete a golfer
	 * from the scores map.
	 */
	private static void deleteScore() {
		if (scores.isEmpty()) {
			System.out.println("There are no golfers to be restored");
			return;
		}
		displayTable(scores);
		// prompting and getting the name
		System.out.print("Enter the name of the golfer you would like to delete : ");
		String golferName = scanner.nextLine().toUpperCase();

		if (scores.containsKey(golferName)) {
			// for handling undoActions/redoActions
			Map<String, Integer> temporaryMap = new HashMap<>();
			temporaryMap.put(golferName, scores.get(golferName));
			String actionName = previousActions.size() + 1 + ".DELETE";

			previousActions.push(new SimpleEntry<>(actionName, temporaryMap));
			// deleting
			deletedScores.put(golferName, scores.get(golferName));
			scores.remove(golferName);
			System.out.println("Successfully deleted the golfer.");
		} else {
			System.out.println("This golfer does not exist.");
		}
	}

	/**
	 * OPTION 4 (Scores Menu)
	 * This method is used to restore a golfer
	 * who was deleted.
	 */
	private static void restoreScore() {
		if (deletedScores.isEmpty()) {
			System.out.println("There are no golfers to be restored");
			return;
		}
		displayTable(deletedScores);
		// prompting and getting the name
		System.out.print("Enter the name of the golfer you would like to restore : ");
		String golferName = scanner.nextLine().toUpperCase();

		if (deletedScores.containsKey(golferName)) {
			// handling undoActions/redoActions
			String actionName = previousActions.size() + 1 + ".RESTORE";
			Map<String, Integer> temp = new HashMap<>();
			temp.put(golferName, deletedScores.get(golferName));
			previousActions.push(new SimpleEntry<>(actionName, temp));

			// restoring
			scores.put(golferName, deletedScores.get(golferName));
			deletedScores.remove(golferName);
			System.out.println("Successfully restored the golfer.");
		} else {
			System.out.println("This golfer does not exist.");
		}
	}


	/**
	 * OPTION 1 (Main Menu)
	 * This method is used to handle the addition, & deletion of scores
	 */
	private static void enterScores() {
		scoreMenu();
		int option = getIntegerInput("Select an option");

		while (option < 1 || option > 4) {
			System.out.println("Please enter a valid option");
			scoreMenu();
			option = getIntegerInput("Select an option");
		}

		switch (option) {
			case 1:
				addScore();
				break;
			case 2:
				editScore();
				break;
			case 3:
				deleteScore();
				break;
			case 4:
				restoreScore();
				break;
			default:
				break;
		}
	}

	/**
	 * OPTION 2 (Main Menu)
	 * This method is used to find if a golfer exists
	 * in the tournament and to display their score
	 */
	private static void findGolfer() {
		System.out.print("\nEnter the golfer's name : ");
		String golferName = scanner.nextLine().toUpperCase();

		if (scores.containsKey(golferName)) {
			System.out.println(String.format("\n%s's score is %d.", capitalizeString(golferName), scores.get(golferName)));
		} else {
			System.out.println("This golfer does not exist.");
		}
	}

	/**
	 * OPTION 3 (Main Menu)
	 * This method is used to display the scores
	 * of the golfers, in ascending order.
	 */
	private static void displayScoreboard() {
		if (scores.isEmpty()) {
			System.out.println("You haven't added any golfers yet!");
			// return will stop the function here
			return;
		}

		//https://www.java67.com/2017/07/how-to-sort-map-by-values-in-java-8.html
		Map<String, Integer> sortedScores = scores.entrySet()
				.stream()
				.sorted(comparingByValue())
				.collect(toMap(Map.Entry::getKey,
						Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		displayTable(sortedScores);
	}

	/**
	 * OPTION 4 (Main Menu)
	 * This method is used to undoActions the previous actions.
	 */
	private static void undoActions() {
		if (previousActions.isEmpty()) {
			System.out.println("There is nothing to be undone!");
			return;
		}

		SimpleEntry<String, Map<String, Integer>> action = previousActions.elementAt(previousActions.size() - 1);
		// Getting the action name and removing the number from it.
		String actionName = action.getKey().split("\\.")[1];
		// Getting the previous entry data
		Map<String, Integer> actionData = action.getValue();

		switch (actionName) {
			case "ADD":
				// For each entry added in the last add action
				// remove the key from the score map.
				actionData.forEach((name, score) -> scores.remove(name));
				break;
			case "EDIT":
				// The action data will hold the previous score
				// This will replace the new score with the old score.
				actionData.forEach((name, oldScore) -> {
					int score = scores.get(name);
					scores.replace(name, oldScore);
					actionData.replace(name, score);
				});
				break;
			case "DELETE":
				// this action data will hold the previously deleted golfers
				// and restore them
				actionData.forEach((name, score) -> {
					scores.put(name, score);
					deletedScores.remove(name);
				});
				break;
			case "RESTORE":
				// this action data will hold the previously restored golfers
				// and delete them
				actionData.forEach((name, score) -> {
					scores.remove(name);
					deletedScores.put(name, score);
				});
				break;
			default:
				break;
		}
		undoneActions.push(new SimpleEntry<>(action.getKey(), actionData));
		previousActions.pop();
	}

	/**
	 * OPTION 5 (Main Menu)
	 * This method is used to redoActions the undone actions.
	 */
	private static void redoActions() {
		if (undoneActions.isEmpty()) {
			System.out.println("There is nothing to be redone!");
			return;
		}
		SimpleEntry<String, Map<String, Integer>> action = undoneActions.elementAt(undoneActions.size() - 1);
		// Getting the action name and removing the number from it.
		String actionName = action.getKey().split("\\.")[1];
		// Getting the previous entry data
		Map<String, Integer> actionData = action.getValue();

		switch (actionName) {
			case "ADD":
				// For each entry removed in the last add action
				// add the key from the score map.
				actionData.forEach((name, score) -> scores.put(name, score));
				break;
			case "EDIT":
				// this action data will contain the old name and the newly
				// overwritten name
				actionData.forEach((name, newScore) -> {
					int oldScore = scores.get(name);
					scores.replace(name, newScore);
					actionData.replace(name, oldScore);
				});
				break;
			case "DELETE":
				// this action data will hold the previously deleted golfers
				// and restore them
				actionData.forEach((name, score) -> {
					scores.remove(name);
					deletedScores.put(name, score);
				});
				break;
			case "RESTORE":
				// this action data will hold the previously restored golfers
				// and delete them
				actionData.forEach((name, score) -> {
					scores.put(name, score);
					deletedScores.remove(name);
				});
				break;
			default:
				break;
		}
		undoneActions.pop();
		previousActions.push(new SimpleEntry<>(action.getKey(), actionData));
	}

	/**
	 * This method is used to display a map
	 * in a neat tabular format.
	 *
	 * @param map The Map that is to be printed out in a neat table format.
	 */
	private static void displayTable(Map<String, Integer> map) {
		// variables for the table
		String leftAlignFormatTitle = "| %-26s |  %-8s |%n";
		String leftAlignFormat = "| %-26s |  %-8d |%n";
		// printing the table
		System.out.format(DIVIDER);
		System.out.format(leftAlignFormatTitle, "Name", "Score");
		System.out.format(DIVIDER);
		map.forEach((name, score) -> System.out.format(leftAlignFormat, capitalizeString(name), score));
		System.out.format(DIVIDER);
	}

	/**
	 * This method is used to get a valid integer input
	 * from the user.
	 *
	 * @param prompt To prompt the user for an input
	 * @return int This returns a valid integer as the user's input.
	 */
	private static int getIntegerInput(String prompt) {
		System.out.format("%s : ", prompt);

		// repeats until the user enters a valid integer
		while (!scanner.hasNextInt()) {
			System.out.println("Please enter a valid integer.\n");
			scanner.next();
			System.out.format("%s : ", prompt);
		}

		int value = scanner.nextInt();
		// moving to the next line, because scanner.next() will not move to the next line.
		scanner.nextLine();
		return value;
	}

	/**
	 * This method is used to capitalise a string.
	 *
	 * @param string the string to be capitalized
	 * @return the capitalized string
	 */
	private static String capitalizeString(String string) {
		String[] names = string.split(" ");
		StringBuilder fullName = new StringBuilder();

		for (int i = 0; i < names.length; i++) {
			fullName.append(names[i], 0, 1).append(names[i].substring(1).toLowerCase());
			if (i != names.length - 1) {
				fullName.append(" ");
			}
		}

		return fullName.toString();
	}

}