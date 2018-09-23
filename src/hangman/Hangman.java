package hangman;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class Hangman {

	// The csv file with all words
	private static String fileName = "../Stanislava_Tercheva_HangmanGame/src/MOCK_DATA.csv";

	// Different ArrayLists with words to pick form each category
	private static ArrayList<String> footballWords = new ArrayList<String>();
	private static ArrayList<String> booksWords = new ArrayList<String>();
	private static ArrayList<String> programmingWords = new ArrayList<String>();

	private static Random rnd = new Random();
	private static String gameWord; // the word to be guessed
	private static char hidingSymbol = '_';
	private static char[] currentGuessedWord; //Hold char indexes for guessed letters of gameWord
	private static int score = 0;

	/*
	 * Reads through csv file with all words and based on different categories
	 * adds them to different ArrayList(which stores words only from one
	 * category). Depending on user's choice returns one random word from the
	 * listed categories.
	 */
	private static String getWord(String userInput) throws IOException {

		Reader reader = Files.newBufferedReader(Paths.get(fileName));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);

		for (CSVRecord csvRecord : csvParser) {

			// Accessing Values by Column Index
			String football = csvRecord.get(0);
			String books = csvRecord.get(1);
			String programming = csvRecord.get(2);

			switch (userInput) {
			case "football teams":
				if (football != null && !football.isEmpty()) {

					footballWords.add(football.toLowerCase());
				}
				int indexFootball = rnd.nextInt(footballWords.size());
				gameWord = footballWords.get(indexFootball);
				break;

			case "books":
				if (books != null && !books.isEmpty()) {
					booksWords.add(books.toLowerCase());
				}
				int indexBooks = rnd.nextInt(booksWords.size());
				gameWord = booksWords.get(indexBooks);
				break;

			case "programming principles":
				if (programming != null && !programming.isEmpty()) {
					programmingWords.add(programming.toLowerCase());
				}
				int indexProgramming = rnd.nextInt(programmingWords.size());
				gameWord = programmingWords.get(indexProgramming);
			default:
				
				break;

			}

		}

		csvParser.close();

		return gameWord;
	}

	// checks if currentGuessdWord is completely guessed
	private static boolean isGuessed() {

		boolean isGuessed = false;

		for (int i = 0; i <= currentGuessedWord.length - 1; i++) {

			if (hidingSymbol == currentGuessedWord[i]) {

				isGuessed = false;

				break;
			} else {

				isGuessed = true;
			}
		}
		return isGuessed;
	}

	/*
	 * Checks user's entered letter or phrase. If the char/String is in the
	 * gameWord, the hidingSymbol at that index in currentGuessedWord array is
	 * replaced with that char.
	 */
	private static int checkEnteredWord(String inputToCheck) {
		int trials = 0; // When the current char is not inside the gameWord the value of trials will be raised.
		boolean currentCharIsInWord = false; // Will be true if the use's letter is in the word.
		boolean currentCharIsNotInWord = false; //Will be false when user's letter is not in the word.

		// check if the input is one symbol
		if (inputToCheck.length() == 1) {

			for (int i = 0; i < currentGuessedWord.length; i++) {

				if (inputToCheck.charAt(0) == gameWord.charAt(i)) {
					currentCharIsInWord = true;
					currentGuessedWord[i] = inputToCheck.charAt(0);

				} else if (inputToCheck.charAt(0) != gameWord.charAt(i)) {
					currentCharIsNotInWord = false;
				}
			}
			if (currentCharIsInWord == false && currentCharIsNotInWord == false) {
				trials += 1;
			}
		}

		// check if the input is more than one symbol
		else if (inputToCheck.length() > 1) {

			for (int i = 0; i < inputToCheck.length(); i++) {

				for (int j = 0; j < currentGuessedWord.length; j++) {

					if (inputToCheck.charAt(i) == gameWord.charAt(j)) {

						currentCharIsInWord = true;
						currentGuessedWord[j] = inputToCheck.charAt(i);
					}

					else if (inputToCheck.charAt(i) != gameWord.charAt(j)) {
						currentCharIsNotInWord = false;
					}
				}
			}
			if (currentCharIsInWord == false && currentCharIsNotInWord == false) {
				trials += 1;
			}
		}

		return trials;
	}
	
	

	private static void play(String category) throws IOException {

		Scanner scanner = new Scanner(System.in);
		int currentAttemptsLeft = 10;
		String gameWordToBeGuessed = getWord(category); //Random word to be guessed from a category
		currentGuessedWord = new char[gameWordToBeGuessed.length()]; //Assigning the length of the gameWordToBeGuessed to the length of the "hidden" word array
		boolean victory = false; //Check if the user has won.

		score++; //Increment amount of total games played.
		
		//Add hiding symbol at the index of the game word into "hidden" word array
		for (int i = 0; i < gameWordToBeGuessed.length(); i++) {

			char whiteSpaceInWord = gameWordToBeGuessed.charAt(i);

			if (whiteSpaceInWord == ' ') {
				currentGuessedWord[i] = whiteSpaceInWord;
			} else {
				currentGuessedWord[i] = hidingSymbol;
			}
		}

		System.out.println();


		while (currentAttemptsLeft > 0) {

			System.out.println("Attempts left " + currentAttemptsLeft);

			System.out.print("Current word/phrase: ");
			
			//print the indexes of currently decoded word
			for (int y = 0; y < currentGuessedWord.length; y++) {
				System.out.print(currentGuessedWord[y] + " ");
			}
			System.out.println();

			/* NOTE: For debugging process, show the word that was chosen to be guessed.
	              Remember to comment this when the game starts. */
			System.out.println(gameWordToBeGuessed);

			System.out.println("Please enter a letter or phrase");
			System.out.print(">");
			String userInput = scanner.nextLine().toLowerCase().trim();

			/*If the user guessed the word correctly, the user wins.
			Otherwise, check what the user has entered and subtract the remaining attempts. */
			if (isGuessed() == true) {
				victory = true;
				break;
			} else {
				if (gameWordToBeGuessed.equals(userInput)) {
					victory = true;
					break;
				} else {
					currentAttemptsLeft -= checkEnteredWord(userInput);
				}

			}

		}

		if (victory == true) {

			System.out.println("Congratulations you have revealed the word/phrase: " + gameWordToBeGuessed);
			System.out.println("Current score: " + score);
			showOptions(); //If the user successfully guesses the word, play the game again.

		} else {
			System.out.println("Unfortunately you did not revealed the word/phrase, which was: " + gameWordToBeGuessed);

		}

		scanner.close();

		return;
	}

	
	/*
	 * Print menu to the user and assign chosen category as a parameter of play method.
	 */
	public static void showOptions() {
		System.out.println("Please choose a category:\nFootball teams \nBooks \nProgramming principles");
		Scanner sc = new Scanner(System.in);
		String userChosenCategory = sc.nextLine().toLowerCase().trim();
		try {
			play(userChosenCategory);

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			sc.close();
		}
	}
	
}
