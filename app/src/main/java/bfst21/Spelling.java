package bfst21;
import java.util.*;
import java.util.stream.*;
public class Spelling {
    private static final String ABC = "abcdefghijklmnopqrstuvwxyzæøå";
    private static Map<String, Integer> dictionary = new HashMap<>();
    private static String DICTIONARY_VALUES = ""; //INSERT DICTIONARY VALUES HERE
    public static void main(String[] args) {
    }
    public Spelling(){
        Stream.of(DICTIONARY_VALUES.toLowerCase().split(",")).forEach((word) -> {
            dictionary.compute(word, (k, v) -> v == null ? 1 : v + 1);
        });
    }
    private static Stream<String> getStringStream(String word) {
        List<String> deletes = new ArrayList<>(); //Every combination of removing one character
        for(int i = 0;i<word.length();i++){
            deletes.add(word.substring(0, i) + word.substring(i + 1));
        }
        List<String> replaces = new ArrayList<>(); //Every combination character possible
        for (int i = 0;i<word.length(); i++) {
            for (int c = 0;c<ABC.length();c++) {
                replaces.add(word.substring(0, i) + ABC.charAt(c) + word.substring(i + 1));
            }
        }


        List<String> inserts = new ArrayList<>(); //Every combination of inserting a character between the existing strings
        for (int i = 0;i<word.length()+1;i++) {
            for (int c = 0;c<ABC.length();c++) {
                inserts.add(word.substring(0, i) + ABC.charAt(c) + word.substring(i));
            }
        }
        List<String> transposes = new ArrayList<>(); //Every combination of switching existing characters
        for (int i = 0; i<word.length()-1; i++) {
            transposes.add(word.substring(0, i) + word.substring(i + 1, i + 2) + word.charAt(i) + word.substring(i + 2));
        }
        return Stream.of(deletes.stream(), replaces.stream(), inserts.stream(), transposes.stream()).flatMap((x) -> x);
    }
    private static Stream<String> edits1(final String word) {
        return getStringStream(word);
    }
    private static String correct(String word) {
        Optional<String> e1 = known(edits1(word)).max(Comparator.comparingInt(a -> dictionary.get(a)));
        if (e1.isPresent()) return dictionary.containsKey(word) ? word : e1.get();
        Optional<String> e2 = known(edits1(word).map(Spelling::edits1).flatMap((x) -> x))
                .max(Comparator.comparingInt(a -> dictionary.get(a)));
        return (e2.orElse(word));
    }
    static String[] correction(String sentence){
        String[] words = sentence.split(" ");
        String[] correctedWords = new String[words.length];
        for (int i = 0;i<words.length;i++) {
            correctedWords[i] = correct(words[i]);
        }
        return correctedWords;
    }
    private static Stream<String> known(Stream<String> words) {
        return words.filter((word) -> dictionary.containsKey(word));
    }
}