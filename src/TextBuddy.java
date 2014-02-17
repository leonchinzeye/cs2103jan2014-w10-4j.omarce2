import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class is used to store and retrieve lines of text in a file. It allows
 * for the user to delete specific lines, display all contents of the text file
 * or to clear the text file entirely. All the lines will be numbered accordingly
 * based on when it was added into the file.
 * 
 * @author Omar Khalid
 *
 */

public class TextBuddy {
	
	private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. %s is ready for use";
	private static final String COMMAND_PROMPT = "Command: ";
	private static final String MESSAGE_ERROR_UNRECOGNISABLE_COMMAND = "Command not recognised!"
			+ " Please re-enter command.";
	private static final String MESSAGE_ERROR_NOT_NUMBER = "Please enter a number.";
	private static final String MESSAGE_ERROR_OPENING_FILE = "Unable to open file %s";
	private static final String MESSAGE_ERROR_READING_FILE = "Error reading file %s";
	private static final String MESSAGE_ERROR_WRITING_TO_FILE = "Error writing to file %s";
	private static final String MESSAGE_INVALID_ARGUMENT = "Invalid Argument!";
	private static final String MESSAGE_ADDED = "Added to %s: \"%s\"";
	private static final String MESSAGE_CLEARED = "All content deleted from %s";
	private static final String MESSAGE_DELETED = "Deleted from %s: \"%s\"";
	private static final String MESSAGE_EMPTY_FILE = "%s is empty.";
	private static final String MESSAGE_NOTHING_TO_DELETE = "There is nothing to delete!";
	private static final String MESSAGE_SORTED = "%s is sorted.";
	private static final String MESSAGE_FAIL_SEARCH = "Can't find \"%s\" in file.";
	private static final String MESSAGE_SUCCESS_SEARCH = "Found \"%s\" in the lines:\n";
	
	private static final int MIN_ARG_LENGTH = 1;
	private static final int SPLIT_INTO_TWO = 2;
	private static final int ACTUAL_STRING_CONTENT = 1;
	private static final int ACTUAL_COMMAND = 0;
	private static final int CMD_ARG = 1;
	private static final int MISSING_ARG = 1;
	
	//These are the possible command types
	enum COMMAND_TYPE {
		ADD, DISPLAY, DELETE, CLEAR, SORT, SEARCH, EXIT, INVALID
	};
	
	//This will store all the lines of text the user enters, allowing for easy retrieval and deletion
	private static ArrayList<String> entries = new ArrayList<String>();
	
	private static String fileName;
	private static File textFile = null;
	private static boolean firstTime = true;
	
	private static Scanner reader = new Scanner(System.in);

	public static void main(String[] args) throws IOException {
		checkValidArgument(args);
		openFile(args);
		while (true) {
			if (firstTime) {
				System.out.println(String.format(WELCOME_MESSAGE, fileName));
				firstTime = false;
			}
			System.out.printf(COMMAND_PROMPT);
			String command = reader.nextLine();
			String response = executeCommand(command);
			writeToFile();
			output(response);
		}
	}
	
	private static void output(String text) {
		System.out.println(text);
	}

	private static void checkValidArgument(String[] args) {
		if (args.length != MIN_ARG_LENGTH) {
			System.out.println(MESSAGE_INVALID_ARGUMENT);
			System.exit(0);
		}
	}

	/**
	 * This method takes the user input and decides which command to execute.
	 * 
	 * @param reader
	 * @param cmd_full
	 * @return
	 */
	public static String executeCommand(String cmd_full) {
		String[] cmd = cmd_full.split(" ", SPLIT_INTO_TWO);
		String commandTypeString = cmd[ACTUAL_COMMAND];

		COMMAND_TYPE commandType = determineCommandType(commandTypeString);
			
		switch(commandType) {
			case ADD:
				return executeAddCmd(cmd);
			case DISPLAY:
				return display();
			case CLEAR:
				return clear();
			case DELETE:
				return executeDelCmd(cmd);
			case SORT:
				return sort();
			case SEARCH:
				return executeSearchCmd(cmd);
			case INVALID:
				return String.format(MESSAGE_ERROR_UNRECOGNISABLE_COMMAND + "\n");
			case EXIT:
				writeToFile();
				System.exit(0);
			default:
				throw new Error("Unrecognized command type.");
		}
	}
	
	private static String executeAddCmd(String[] cmd) {
		if (cmd.length == MISSING_ARG) {
			return MESSAGE_INVALID_ARGUMENT;
		}else {
			return add(cmd[CMD_ARG]);
		}
	}
	
	private static String executeDelCmd(String[] cmd) {
		if (cmd.length == MISSING_ARG) {
			return MESSAGE_INVALID_ARGUMENT;
		}else {
			return delete(cmd[CMD_ARG]);
		}
	}
	
	private static String executeSearchCmd(String[] cmd) {
		if (cmd.length == MISSING_ARG) {
			return MESSAGE_INVALID_ARGUMENT;
		}else {
			return search(cmd[CMD_ARG]);
		}
	}

	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null)
			throw new Error("command type string cannot be null!");

		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			 	return COMMAND_TYPE.DELETE;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
		 	return COMMAND_TYPE.CLEAR;
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
		 	return COMMAND_TYPE.SORT;
		} else if (commandTypeString.equalsIgnoreCase("search")) {
		 	return COMMAND_TYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
		 	return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}
	
	private static String add(String cmd) {
		if (cmd.trim().length() <= 0) {
			return MESSAGE_INVALID_ARGUMENT;
		} else {
			entries.add(cmd);
		}
		return String.format(MESSAGE_ADDED, fileName, cmd);
	}

	/**
	 * This method will first check whether the file currently exists.
	 * If it does, it will execute as normal.
	 */
	private static String display() {
		if (entries.isEmpty()) {
			return String.format(MESSAGE_EMPTY_FILE, fileName);
		} else {
			return printDisplay();
		}
	}

	private static String printDisplay() {
		String output = "";
		for (int i = 0; i < entries.size(); i++) {
			if (i > 0) {
				output += "\n";
			}
			output += (i+1) + ". " + entries.get(i);
		}
		return output;
	}

	/**
	 * This method will also first check whether the file exists.
	 * If it does, this method will delete all lines from the ArrayList "entries"
	 * as well as the text file by over-writing it with a whitespace.
	 */
	private static String clear() {
		if (textFile.exists()) {
			entries = new ArrayList<String>();
			return String.format(MESSAGE_CLEARED, fileName);
		} else {
			return String.format(MESSAGE_ERROR_OPENING_FILE, fileName);
		}
	}

	/**
	 * The following first checks whether there is an argument. 
	 * Then it checks whether the argument is a valid digit or not.
	 * Case 1: Anything except a digit is entered.
	 * Case 2: The digit entered does not exist in the text file.
	 * Case 3: The digit entered is less than or equals to zero.
	 * Otherwise, the command will delete the line as requested.
	 * @param lineNo is the argument entered by the user, i.e. the line he wishes to delete.
	 */
	private static String delete(String lineNo) {
		boolean notDigit = false;
		int deleteLineNo = 0;
		String lineToDelete;

		try {
			deleteLineNo = Integer.parseInt(lineNo);
		} catch(Exception ex) {
			notDigit = true;
		}
	
		if (notDigit) {
			return MESSAGE_ERROR_NOT_NUMBER;
		} else if (deleteLineNo > entries.size()) {
			return MESSAGE_NOTHING_TO_DELETE;
		} else if (deleteLineNo <= 0) {
			return MESSAGE_INVALID_ARGUMENT;
		} else {
			lineToDelete = entries.get(deleteLineNo - 1);
			entries.remove(deleteLineNo - 1);
		}
		return String.format(MESSAGE_DELETED, fileName, lineToDelete);
	}
	
	private static String sort() {
		if (entries.isEmpty()) {
			return String.format(MESSAGE_EMPTY_FILE, fileName);
		} else {
			Collections.sort(entries);
			return String.format(MESSAGE_SORTED, fileName);
		}
	}
	
	private static String search(String query) {
		if (entries.isEmpty()) {
			return String.format(MESSAGE_FAIL_SEARCH, fileName);
		} else {
			String foundInLine = searchThruLines(query);
			return foundInLine;
		}
	}

	private static String searchThruLines(String query) {
		boolean found = false;
		String foundInLine = String.format(MESSAGE_SUCCESS_SEARCH, query);		
		for (int i = 0; i < entries.size(); i++) {
			if (entries.get(i).contains(query)) {
				foundInLine += (i + 1) + ". " + entries.get(i) + "\n";
				found = true;
			}
		}
		if (!found) {
			return String.format(MESSAGE_FAIL_SEARCH, fileName);
		}
		return foundInLine;
	}
	
	/**
	 * This method checks if the file of the argument name exists. If it does, it will extract all lines of text
	 * and store them in the ArrayList "entries".
	 * Otherwise, it will create a new file of the same name.
	 * 
	 * @param args is the name of the text file the user wishes to access or create.
	 */
	public static void openFile(String[] args) {
		textFile = new File(args[0]);
		fileName = args[0];
		String lineRead = null;
		if (textFile.exists()) {
			try {
				BufferedReader buffReader = new BufferedReader(new FileReader(textFile));
				while((lineRead = buffReader.readLine()) != null) {
					String[] lineReadArray = lineRead.split(" ", SPLIT_INTO_TWO);
					entries.add(lineReadArray[ACTUAL_STRING_CONTENT]);
				}
				buffReader.close();
			} catch(IOException ex) {
				System.out.println(String.format(MESSAGE_ERROR_READING_FILE, fileName));
			}
		} else {
			textFile = new File(args[0]);
		}
	}
	
	/**
	 * The following method saves all lines from ArrayList "entries"
	 * into the text file.
	 */
	private static void writeToFile() {
		try {
			PrintWriter writer = new PrintWriter(textFile);
			for (int i = 0; i < entries.size(); i++) {
				writer.println((i + 1) + ". " + entries.get(i));
			}
			writer.close();
		} catch (IOException ex) {
			System.out.println(MESSAGE_ERROR_WRITING_TO_FILE);
		}
	}
}