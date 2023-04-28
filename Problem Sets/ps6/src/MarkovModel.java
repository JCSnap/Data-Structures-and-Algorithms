import java.util.HashMap;
import java.util.Random;
/**
 * This is the main class for your Markov Model.
 *
 * Assume that the text will contain ASCII characters in the range [1,255].
 * ASCII character 0 (the NULL character) will be treated as a non-character.
 *
 * Any such NULL characters in the original text should be ignored.
 */
public class MarkovModel {
	private int order;
	private HashMap<String, Pair> frequencies;
	// Use this to generate random numbers as needed
	private Random generator = new Random();

	// This is a special symbol to indicate no character
	public static final char NOCHARACTER = (char) 0;

	/**
	 * Constructor for MarkovModel class.
	 *
	 * @param order the number of characters to identify for the Markov Model sequence
	 * @param seed the seed used by the random number generator
	 */
	public MarkovModel(int order, long seed) {
		this.order = order;
		HashMap<String, Pair> frequencies = new HashMap<>();
		this.frequencies = frequencies;

		// Initialize the random number generator
		generator.setSeed(seed);
	}

	public class Pair {
		/* first tracks the total count that kgram is repeated in the text
		 * second tracks the frequency of chars that comes after kgram
		 */
		private int first;
		private int[] second;

		public Pair(int first, int[] second) {
			this.first = first;
			this.second = second;
		}

		public void addTotal() {
			this.first++;
		}

		public void addCount(int nextChar) {
			int[] temp = this.second;
			temp[nextChar] = temp[nextChar] + 1;
		}

		public int getTotal() {
			return this.first;
		}

		public int getCount(int index) {
			return this.second[index];
		}
	}

	/**
	 * Builds the Markov Model based on the specified text string.
	 */
	public void initializeText(String text) {
		this.frequencies = new HashMap<>();
		for (int i = 0; i < text.length() - order; i++) {
			/* substring has length order, we iterate through every possible substring
			 * end is to represent the end of substring (exclusive)
			 * nextCharPos represents the position of the next char, the one where we need to
			 * keep track of the frequency, it is one position more than end, in this case since
			 * end is exclusive and nextCharPos is inclusive, they have the same value
			 */
			int end = i + order;
			int nextCharPos = end;
			String curSubstring = text.substring(i, end);
			if (this.frequencies.containsKey(curSubstring)) {
				Pair curSubstringInfo = this.frequencies.get(curSubstring);
				int nextChar = (int) text.charAt(nextCharPos);
				/* we add to the frequency of that specific char for the specific kgram
				 * we add to the total
				 */
				curSubstringInfo.addCount(nextChar);
				curSubstringInfo.addTotal();
			} else {
				/* We initialize a new pair if we have not
				 */
				int[] count = new int[255];
				int nextChar = (int) text.charAt(nextCharPos);
				count[nextChar] = count[nextChar] + 1;
				Pair temp = new Pair(1, count);
				frequencies.put(curSubstring, temp);
			}
		}
	}

	/**
	 * Returns the number of times the specified kgram appeared in the text.
	 */
	public int getFrequency(String kgram) {
		if (this.frequencies.containsKey(kgram)) {
			Pair kgramInfo = frequencies.get(kgram);
			return kgramInfo.getTotal();
		} else {
			return 0;
		}
	}

	/**
	 * Returns the number of times the character c appears immediately after the specified kgram.
	 */
	public int getFrequency(String kgram, char c) {
		if (this.frequencies.containsKey(kgram)) {
			Pair kgramInfo = this.frequencies.get(kgram);
			int charIndex = (int) c;
			return kgramInfo.getCount(charIndex);
		} else {
			return 0;
		}
	}

	/**
	 * Generates the next character from the Markov Model.
	 * Return NOCHARACTER if the kgram is not in the table, or if there is no
	 * valid character following the kgram.
	 */
	public char nextCharacter(String kgram) {
		if (this.frequencies.containsKey(kgram)) {
			Pair kgramInfo = this.frequencies.get(kgram);
			int total = kgramInfo.getTotal();
			/* charArray is used to represent the preceeding characters and their corresponding frequencies
			 * for example, if b has 25%, c has 50%, d has 25%, the array would be
			 * [b, c, c, d] (in ASCII integer). This is for random selection later on
			 * charCount represents how many positions in this array we have filled. Since total represents
			 * the frequency the kgram has repeated in the text, when we are done charCount will equal total
			 */
			int[] charArray = new int[total];
			int charCount = 0;
			for (int i = 0; i < 255; i++) {
				/* curCount (or CurTotal) is total of the the current char in the array
				 */
				int curCount = kgramInfo.getCount(i);
				if (charCount == total) {
					break;
				}
				if (curCount != 0) {
					while (curCount > 0) {
						/* eg. if total is 5, we add 5 instances of the current char into the array
						 */
						charArray[charCount] = i;
						curCount--;
						charCount++;
					}
				}
			}
			int randomInt = generator.nextInt(total);
			return (char) charArray[randomInt];
		} else {
			return NOCHARACTER;
		}
	}
}
