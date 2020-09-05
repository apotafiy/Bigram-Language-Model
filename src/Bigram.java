import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

// Artem Potafiy

/**
 * A simple bigram model for language processing.
 *
 */
public class Bigram implements ActionListener {
	Set<String> totalSet; // total set of all bigrams
	TreeMap<String, LinkedList<String>> totalMap; // contains every word(key) and every different word that comes after
													// it(Value-ArrayList)
	private int alternator = 0;

	private JFrame frame;
	private JPanel panel;
	private JLabel label;
	private JTextField desiredWordSeed; // for choosing starting word
	private JTextField numberOfWords;
	private JButton button;

	public Bigram() throws FileNotFoundException {

		Scanner s1 = new Scanner(new File("mmarch.txt"));
		String text = "";
		while (s1.hasNextLine()) {
			String string = s1.nextLine().toLowerCase();
			text = text.concat(string + "\n");
		}
		s1.close();

		this.frame = new JFrame();
		this.panel = new JPanel();

		this.button = new JButton("Create Sample Bigram!");
		button.addActionListener(this);

		this.desiredWordSeed = new JTextField("Replace this text with ONE word and click the button when ready");
		this.numberOfWords = new JTextField("Replace this text with number of words you want output");
		panel.add(desiredWordSeed);
		panel.add(numberOfWords);

		// this.label = new JLabel("You may click the button when the program is no
		// longer processing. Processing... ");
		this.label = new JLabel();
		label.setFont(label.getFont().deriveFont(15.0f));
		panel.add(label);

		panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
		panel.setLayout(new GridLayout(0, 1));
		panel.add(button);

		frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Bigram Language Model");
		frame.pack();
		frame.setVisible(true);

		Scanner s = new Scanner(text);
		this.totalSet = new HashSet<>();
		this.totalMap = new TreeMap<>();
		String prevWord = "";
		String nextWord = "";

		while (s.hasNext()) {
			nextWord = s.next();
			if (prevWord.equals("")) {
				prevWord = nextWord;
				nextWord = s.next();
				totalSet.add(prevWord + " " + nextWord);
				totalMap.put(prevWord, new LinkedList<>());
			} else {
				totalSet.add(prevWord + " " + nextWord);
			}

			if (!totalMap.containsKey(prevWord)) { // if word never seen before
				totalMap.put(prevWord, new LinkedList<>()); // adds new key-value pair
				totalMap.get(prevWord).add(nextWord);
			} else {
				totalMap.get(prevWord).add(nextWord); // if word is seen before, add nextWord to this keys ArrayList

			}
			prevWord = nextWord;

		}
		s.close();

		// label.setText("You may click the button when the program is no longer
		// processing. Successfuly Processed!");
		// button.setText("Create Sample Bigram!");

	}

	/**
	 * Sequence generation method will be given a start word and a count indicating
	 * the number of total words to generate (including the start word). It will
	 * generate the "most likely" or "most common" sequence based on bigram counts.
	 * It will return an array of Strings with the words generated in order. It
	 * always starts by generating the start word. As you generate each word, the
	 * next word generated should be the one that appears most often in the input
	 * (constructor) text after the previous word generated. If you reach a dead end
	 * (either the previous word was never seen or there are no words ever seen
	 * after that word), end generation early and return a shorter array. If there
	 * is more than one "most common" word seen in the input text, pick the
	 * smallest/first one according to the String.compareTo method.
	 * 
	 * @param word
	 * @param count
	 * @return
	 */
	public String[] generate(String word, int count) {
		if (!totalMap.containsKey(word) || totalMap.get(word) == null || count == 1) {
			return new String[] { word };
		}

		LinkedList<String> generatedString = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			if (generatedString.isEmpty()) {
				generatedString.add(word);
			} else {
				String newestWord = generatedString.getLast();

				if (totalMap.get(newestWord) != null) { // if the newest word has an ArrayList

					if (alternator == 0 || alternator == 1) { // switches between most common and second most common
						generatedString.add(mostCommon(newestWord));
						//alternator++;
						alternator = (int)(Math.random()*3);

					} else {
						//alternator = 0;
						generatedString.add(randomWord(newestWord));
						alternator = (int)(Math.random()*3);

					}
				} else {
					break;
				}
			}
		}
		String[] ending = new String[generatedString.size()];
		int i = 0;
		for (String stuff : generatedString) {
			ending[i] = stuff;
			i++;
		}

		return ending;
	}

	public String randomWord(String word) {

		String otherWord = totalMap.get(word).get((int) (Math.random() * (totalMap.get(word).size())));
		return otherWord;
	}

	/**
	 * Uses Map to count how often a word appears in ArrayList Then find entry with
	 * maximum value
	 * 
	 * @param word
	 * @return most common word
	 */
	public String mostCommon(String word) { // can be called in generate()
		// create hashmap with words as keys.
		// for each loop and increments when looking at element
		TreeMap<String, Integer> tempMap = new TreeMap<>();
		for (String words : totalMap.get(word)) { // iterates through ArrayList and counts how often a word appears
			if (!tempMap.containsKey(words)) {
				tempMap.put(words, 1);
			} else {
				int temp = tempMap.get(words).intValue();
				temp++; // incrementing count
				tempMap.put(words, temp);
			}
		}

		// at this point, tempMap KEYS contain every word that is in totalMap.get(word)
		// and
		// the VALUES are how often the word occurs

		String bestWord = "";
		Map.Entry<String, Integer> maxEntry = null; // Map.Entry allows for easier iteration
		for (Map.Entry<String, Integer> t : tempMap.entrySet()) {
			if (maxEntry == null || t.getValue().compareTo(maxEntry.getValue()) > 0) { // tempMap is already ordered
				maxEntry = t;
				bestWord = maxEntry.getKey();
			}
		}

		// create a list from the tempMap values and then remove any duplicates

		if (alternator == 0) { // meant to choose second best word. idk if it actually works
			tempMap.remove(bestWord);
			if (!tempMap.isEmpty()) {
				// String bestWord = "";
				Map.Entry<String, Integer> maxEntry2 = null; // Map.Entry allows for easier iteration
				for (Map.Entry<String, Integer> t : tempMap.entrySet()) {
					if (maxEntry2 == null || t.getValue().compareTo(maxEntry2.getValue()) > 0) { // tempMap is already
																									// ordered
						maxEntry2 = t;
						bestWord = maxEntry.getKey();
					}
				}
			}

		}

		return bestWord;

	}

	public static void main(String[] args) throws FileNotFoundException {

		new Bigram();
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		String input = desiredWordSeed.getText();

		int num = 0;
		try {
			num = Integer.parseInt(numberOfWords.getText());

			String[] generated = generate(input, num);
			String stringy = "";
			if (generated.length == 1) {
				label.setText("ERROR IN INPUT FIELD");
				desiredWordSeed.setText("ATTENTION: Enter different word");
			} else {
				for (String word : generated) {
					stringy = stringy + " " + word;
				}

				label.setText(stringy);
			}

		} catch (

		NumberFormatException e) {
			label.setText("ERROR IN INPUT FIELD");

			numberOfWords.setText("WARNING: You must enter an integer value in this input field");
		}

	}

}
