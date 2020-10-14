import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

// This program redacts proper nouns from a given text.
// In War and Peace it encounters some issues when finding the start of sentences due to the writing style.
// Over a few chapters the program usually performs excellently, but less so over the entire book.
public class main {

    // This function will return true if both strings are equal.
    protected static boolean equals (String string1, String string2) {
        if (string1.length() != string2.length()){
            return false;
        }
        // For each character in the string...
        for(int i = 0; i < string1.length(); i++) {
            // Check to see if they are equal.
            if (string1.charAt(i) != string2.charAt(i)) {
                return false;
            }
        }
        // If every character is equal then return true.
        return true;
    }

    // This function returns a string containing the contents of the file.
    public static String importText(String path) {
        Scanner scanner = null;
        // Try creating a new scanner on the file.
        try {
            scanner = new Scanner(new File(path));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // Read the contents of the file.
        String content = scanner.useDelimiter("\\A").next();
        scanner.close();
        return content;
    }

    // This function returns an ArrayList containing every word in the file.
    public static ArrayList<String> importRedact (String path) {
        // Read the file into a string.
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String content = scanner.useDelimiter("\\A").next();
        scanner.close();
        // Remove all new lines in the string.
        content = content.replaceAll("\n", " ");
        // Create an array by splitting the string by spaces.
        String[] redactList = content.split(" ");
        // For every element in the array, remove any spaces.
        for (int i=0; i<redactList.length-1; i++) {
           redactList[i] = redactList[i].replaceAll(" ", "");
        }
        // Return the array as an ArrayList.
        return new ArrayList(Arrays.asList(redactList));
    }

    // This function returns true if a character is a capital letter.
    public static boolean isCapital(char c) {
        return ((int) c > 64) && ((int) c < 91);
    }

    // This function returns true if a character is a sentence terminator.
    public static boolean isSentenceEnd(char c) {
        return c == '.' || c == '?' || c == '!' || c == '“';
    }

    // This function will return an ArrayList containing all the proper nouns needed to be redacted.
    public static ArrayList<String> findProperNouns(String text) {
        ArrayList<String> properNouns = new ArrayList<String>();
        String word = "";
        // This will represent whether the word is at the start of a sentence.
        boolean isFirst = true;
        // For every character in the text.
        for (int i = 0; i< text.length(); i++) {
            // If the character is a sentence terminator...
            if (isSentenceEnd(text.charAt(i))) {
                // Find the character at the start of the next sentence.
                while (!isAlpha(text.charAt(i))) {
                    if (i>=text.length()-1) break;
                    i++;
                }
                isFirst = true;
            }
            // If the character is a capital letter...
            if (isCapital(text.charAt(i))) {
                // Build the word that follows it.
                while (isAlpha(text.charAt(i))) {
                    word += text.charAt(i);
                    if (i>=text.length()-1) break;
                    i++;
                }
                i--;
                // If this word isn't at the start of a sentence it is a proper noun.
                if (!equals(word, "I") && !isFirst && word.length()>2 && !equals(word, "CHAPTER") && !equals(word, "BOOK")) {
                    addToList(properNouns, word);
                }
                word = "";
            }
            isFirst = false;
        }
        return properNouns;
    }

    // This function replaces every character in a string with '*'.
    public static String redactWord(String word) {
        String newWord = "";
        // For every charcter in the original word, add a '*' to the new word.
        for(int i=0; i<word.length(); i++) {
            newWord = newWord + '*';
        }
        return newWord;
    }

    // This function returns true if the character is a letter.
    public static boolean isAlpha(char c) {
        return (c < 123 && c > 96) || (c < 91 && c > 64);
    }

    //This function returns true if a given string is in a given list.
    public static boolean findInList(ArrayList<String> list, String stringToFind) {
        for (int i=0; i<list.size(); i++)  {
            if (equals(list.get(i), stringToFind)) {
                return true;
            }
        }
        return false;
    }

    // This procedure adds a string to a list.
    public static void addToList(ArrayList<String> list, String stringToAdd) {
        // First checks if string is already in the list.
        if (!findInList(list, stringToAdd)) {
            list.add(stringToAdd);
        }
    }

    // This function redacts every element in a given list from a given string.
    public static String redact(String text, ArrayList<String> redactList){
        String word = "";
        StringBuilder newText = new StringBuilder();
        // For every character in the string...
        for (int i = 0; i<text.length()-2; i++) {
            // If the character is a letter, build the word that follows it.
            while (isAlpha(text.charAt(i)) && i<text.length()-1) {
                word = word + text.charAt(i);
                i++;
            }
            // If this word is in redact list...
            if (findInList(redactList, word)) {
                // Add the redacted word to the text.
                newText.append(redactWord(word));
                word = "";
            }
            word = word + text.charAt(i);
            newText.append(word);
            word = "";
        }
        return newText.toString();
    }

    // This function merges two lists together.
    public static ArrayList<String> mergeLists(ArrayList<String> oldList, ArrayList<String> newList) {
        ArrayList<String> mergedList = new ArrayList<String>();
        // For every element in first list, add to new list.
        for (int i=0; i<newList.size(); i++) {
            addToList(mergedList, newList.get(i));
        }
        // For every element in second list, add to new list.
        for (int i=0; i<oldList.size(); i++) {
            addToList(mergedList, oldList.get(i));
        }
        return mergedList;
    }

    // This function writes to text to a file.
    public static void writeToFile (String path, String text) {
        try {
            FileWriter outFile = new FileWriter(path);
            outFile.write(text);
            outFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        String textPath = "";
        String redactPath = "";
        String outPath = "";

        // Import the text.
        String text = importText(textPath);
        // Import the original redact list.
        ArrayList<String> redactList = importRedact(redactPath);
        // Find all the properNouns in the text.
        ArrayList<String> properNouns = findProperNouns(text);
        // Merge the original redact list will the new one.
        redactList = mergeLists(properNouns, redactList);
        // Redact everything in the list from the text.
        String finalText = redact(text, redactList);

        System.out.println(finalText);
        // Write the new text to a file.
        writeToFile(outPath, finalText);

    }
}
