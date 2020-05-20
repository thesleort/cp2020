import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
This is the exam for DM563 - Concurrent Programming, Spring 2020.

Your task is to implement the following methods of class Exam:
- shortestWord;
- longestWord;
- wordStartingWith;
- findWord.

These methods search text files for particular words.
You must use a BreakIterator to identify words in a text file,
which you can obtain by calling BreakIterator.getWordInstance().
The usage of BreakIterator is explained in the video lecture 02-BreakIterator.

The implementations of these methods must exploit concurrency to achieve improved performance.

The only code that you can change is the implementation of these methods.
In particular, you cannot change the signatures (return type, name, parameters) of any method, and you cannot edit method main.
The current code of these methods throws an exception: remove that line before proceeding on to the implementation.

You can find a general explanation in the video lecture 10-Exam, and at the following webpage.

https://github.com/fmontesi/cp2020/tree/master/exam

The webpage also contains more rules about the exam.

Note for people taking the re-exam of the 5 ECTS version of this course: you do not have to implement methods "longestWord" and "findWord".
*/
public class Exam {

	// Do not change this method
	public static void main(final String[] args) {
		checkArguments(args.length > 0, "You must choose a command: help, shortestWord, longestWord, wordStartingWith, or findWord.");
		switch (args[0]) {
			case "help":
				System.out.println("Available commands: help, shortestWord, longestWord, wordStartingWith, or findWord.\nFor example, try:\n\tjava Exam shortestWord data");
				break;
			case "shortestWord":
				checkArguments(args.length == 2, "Usage: java Exam.java shortestWord <directory>");
				final String shortestWord = shortestWord(Paths.get(args[1]));
				System.out.println("The shortest word found is " + shortestWord);
				break;
			case "longestWord":
				checkArguments(args.length == 2, "Usage: java Exam.java longestWord <directory>");
				final String longestWord = longestWord(Paths.get(args[1]));
				System.out.println("The longest word found is " + longestWord);
				break;
			case "wordStartingWith":
				checkArguments(args.length == 4, "Usage: java Exam.java wordStartingWith <directory> <prefix> <true|false>");
				final Optional<LocatedWord> locatedWordOptional = wordStartingWith(Paths.get(args[1]), args[2], Boolean.parseBoolean(args[3]));
				locatedWordOptional.ifPresentOrElse(
					locatedWord -> System.out.println("Found " + locatedWord.word + " in " + locatedWord.filepath ),
					() -> System.out.println("No match found")
				);
				break;
			case "findWord":
				checkArguments(args.length == 4, "Usage: java Exam.java findWord <directory> <word> <limit>");
				final int limit = Integer.parseInt(args[3]);
				final List<WordLocation> locations = findWord(Paths.get(args[1]), args[2], limit);
				if (locations.size() > limit) {
					throw new InternalException("Returned list size exceeds limit");
				}
				System.out.println("Found " + locations.size() + " matches");
				locations.forEach(location -> System.out.println(location.filepath + ":" + location.line));
				break;
			default:
				System.out.println("Unrecognised command: " + args[0] + ". Try java Exam.java help.");
				break;
		}
	}
	
	// Do not change this method
	private static void checkArguments(final Boolean check, final String message)
	{
		if (!check) {
			throw new IllegalArgumentException(message);
		}
	}

	/** Returns the shortest word present in the text files contained in a directory.
	 * 
	 * This method recursively visits a directory to find all the text files
	 * contained in it and its subdirectories (and the subdirectories of these
	 * subdirectories, etc.).
	 *
	 * You must consider only files ending with a ".txt" suffix. You are
	 * guaranteed that they will be text files.
	 * 
	 * The method should return the shortest word found among all text files.
	 * If multiple words are identified as shortest, the method should return
	 * the one that precedes the other shortest words lexicographically.
	 * To compare strings lexicographically, you can use String::compareTo.
	 * See also https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#compareTo(java.lang.String)
	 * 
	 * @param dir the directory to search
	 * @return the shortest word found among all text files inside of dir
	 */
	private static String shortestWord(final Path dir) {
		throw new UnsupportedOperationException(); // Remove this once you implement the method
	}

	/** Returns the longest word present in the text files contained in a directory.
	 * 
	 * This method recursively visits a directory to find all the text files
	 * contained in it and its subdirectories (and the subdirectories of these
	 * subdirectories, etc.).
	 *
	 * You must consider only files ending with a ".txt" suffix. You are
	 * guaranteed that they will be text files.
	 * 
	 * The method should return the longest word found among all text files.
	 * If multiple words are identified as longest, the method should return
	 * the one that precedes the other longest words lexicographically.
	 * To compare strings lexicographically, you can use String::compareTo.
	 * See also https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/String.html#compareTo(java.lang.String)
	 * 
	 * @param dir the directory to search
	 * @return the longest word found among all text files inside of dir
	 */
	private static String longestWord(final Path dir) {
		throw new UnsupportedOperationException(); // Remove this once you implement the method
	}

	/** Returns an Optional<LocatedWord> (see below) about a word starting with the given prefix found in the files of the given directory.
	 * 
	 * This method recursively visits a directory to find text files
	 * contained in it and its subdirectories (and the subdirectories of these
	 * subdirectories, etc.).
	 *
	 * You must consider only files ending with a ".txt" suffix. You are
	 * guaranteed that they will be text files.
	 * 
	 * The method should return an (optional) LocatedWord object (defined by
	 * the class at the end of this file) containing:
	 *   - the word found that starts with the given prefix;
	 *   - the path to the file containing the word.
	 * 
	 * If the caseSensitive parameter is true, then the found word must start
	 * exactly with the given prefix. Otherwise, if caseSensitive is false,
	 * then the found word can start with the given prefix without considering
	 * differences between uppercase and lowercase letters.
	 * 
	 * If a word satisfying the description above can be found, then the method
	 * should return an Optional containing the desired LocatedWord.
	 * Otherwise, if such a word cannot be found, the method should
	 * return Optional.empty().
	 * 
	 * This method should return *as soon as possible*: as soon as a satisfactory
	 * word is found, the method should return a result without waiting for the 
	 * processing of remaining files and/or other data.
	 * 
	 * @param dir the directory to search
	 * @param prefix the prefix the word searched for should start with
	 * @param caseSensitive whether the search should be case sensitive
	 * @return an optional LocatedWord about a word starting with the given prefix
	 */
	private static Optional<LocatedWord> wordStartingWith(final Path dir, final String prefix, final boolean caseSensitive) {
		throw new UnsupportedOperationException(); // Remove this once you implement the method
	}

	/** Returns a list of locations at which the given word has been found.
	 * 
	 * This method recursively visits a directory to find text files
	 * contained in it and its subdirectories (and the subdirectories of these
	 * subdirectories, etc.).
	 *
	 * You must consider only files ending with a ".txt" suffix. You are
	 * guaranteed that they will be text files.
	 * 
	 * The method should return a list of WordLocation objects (defined by
	 * the class at the end of this file) containing:
	 *   - the line number at which the word has been found;
	 *   - the path to the file containing the word.
	 *
	 * The search is case sensitive, in the sense that the word found must match
	 * exactly the word given as parameter, also considering casing.
	 * For example, the word "Hello" would be judged as different from the word "hello",
	 * and the word "heY" would be different than the word "hEy".
	 * 
	 * The size of the returned list must not exceed the given limit.
	 * Therefore, this method should return *as soon as possible*: if the list
	 * reaches the given limit at any point during the computation, no more
	 * elements should be added to the list and remaining files and/or other lines
	 * should not be analysed.
	 * 
	 * @param dir the directory to search
	 * @param word the word to search for
	 * @param limit the size limit for the returned list
	 * @return a list of locations where the given word has been found
	 */
	private static List<WordLocation> findWord(final Path dir, final String target, final int limit) {

		List<WordLocation> results = new ArrayList<WordLocation>();
		ExecutorService executor = Executors.newWorkStealingPool();
		ExecutorCompletionService<WordLocation> completionService =
		new ExecutorCompletionService<>( executor );

		try (Stream<Path> walk = Files.walk(dir).filter(s -> { 
			return s.toString().endsWith(".txt"); 
		})) {
			walk.forEach(file -> {
				// completionService.
				// completionService.submit(() -> OpenFile(file, results, target, limit));
				// if (results.size() <= limit) {
				// 	OpenFile(file, results, target, limit);
				// }
			});



			while (!executor.isTerminated()) {
				synchronized(results) {
					if (results.size() >= limit) {
						executor.shutdownNow();
					} 
				}
			}

		} catch (final IOException e) {
			e.printStackTrace();
		}

		return results;
	}

	private static void OpenFile(final Path file, List<WordLocation> words, final String target, final int limit) {
		List<String> lines = new ArrayList<>();
		try (Stream<String> linesStream = Files.lines(file)) {			
			lines = linesStream.collect(Collectors.toList());
			
			int i = 0;
			for (String line: lines) {
				LineSearch(line, i, file, target, words, limit);
				i++;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

	}

	private static void LineSearch(final String line, final int lineNumber, final Path path, final String target, List<WordLocation> words, final int limit) {
		extractWords(line).forEach(word -> {
			if (word.equals(target)) {
				synchronized(words) {
					if (words.size() < limit) {
						System.out.println("SIZE: = " + words.size());
						words.add(new WordLocation(path, lineNumber + 1));
					}
				}
			}
		});
	}

	public static Stream< String > extractWords( String s )
	{
		List< String > words = new ArrayList<>();
		
		BreakIterator it = BreakIterator.getWordInstance();
		it.setText( s );
		
		int start = it.first();
		int end = it.next();
		while( end != BreakIterator.DONE ) {
			String word = s.substring( start, end );
			if ( Character.isLetterOrDigit( word.charAt( 0 ) ) ) {
				words.add( word );
			}
			start = end;
			end = it.next();
		}		
		return words.stream();       
	}

	// Do not change this class
	private static class LocatedWord {
		private final String word; // the word
		private final Path filepath; // the file where the word has been found

		private LocatedWord(final String word, final Path filepath) {
			this.word = word;
			this.filepath = filepath;
		}
	}

	// Do not change this class
	private static class WordLocation {
		private final Path filepath; // the file where the word has been found
		private final int line; // the line number at which the word has been found

		private WordLocation(final Path filepath, final int line) {
			this.filepath = filepath;
			this.line = line;
		}
	}
	
	// Do not change this class
	private static class InternalException extends RuntimeException {
		private InternalException(final String message) {
			super(message);
		}
	}
}
