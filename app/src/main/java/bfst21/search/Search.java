package bfst21.search;

import bfst21.search.RadixNode;
import bfst21.search.RadixTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Search {

    public static void main(String[] args) {
        RadixTree tree = new RadixTree();
        tree.insert("Mårdalsvej 12A", 1);
        tree.insert("Udemarken 5", 1);
        tree.insert("Klitvej 20", 1);
        tree.insert("Elmevej 8", 1);
        tree.insert("Stampen Vej 6 7", 1);
        tree.insert("Vej 14 Nr 33", 1);

        RadixTree tree1 = new RadixTree();
        tree1.insert("Ulstrup", 1);
        tree1.insert("V. Velling", 1);
        tree1.insert("Øster Velling", 1);
        tree1.insert("Jystrup", 1);
        tree1.insert("Ringsted", 1);
        tree1.insert("Roskilde", 1);
        tree1.insert("Ringkøbing", 1);
        tree1.insert("Nørlev", 1);
        tree1.insert("Nørlev strand", 1);
        tree1.insert("Løkken", 1);
        tree1.insert("Lønstrup", 1);

        List<String> randomAddresses = new ArrayList<>();
        randomAddresses.add("Haveforeningen Af 10. Maj 1918 44 20.th., 8260 Viby J");
        randomAddresses.add("Einarsvej 2800 Lyngby");
        randomAddresses.add("Christian 4 Vej, Kolding");
        randomAddresses.add("Hf. 4. Juli, Bagsværd");
        randomAddresses.add("Løjt Feriecenter 2, Aabenraa");
        randomAddresses.add("Skovbrynet 9850");
        randomAddresses.add("4. Maj Stræde, Marstal");
        randomAddresses.add("Rørthvej 34 A, 8300");


        List<String> roadnameAndNumber = new ArrayList<>();
        roadnameAndNumber.add("Mårdalsvej 12A");
        roadnameAndNumber.add("Udemarken 5");
        roadnameAndNumber.add("Klitvej 20");
        roadnameAndNumber.add("Elmevej 8");
        roadnameAndNumber.add("Stampen Vej 6 7");
        roadnameAndNumber.add("Vej 14 Nr 33");

        List<String> roadname = new ArrayList<>();
        roadname.add("Feddet");
        roadname.add("Haveforeningen Af 1934");
        roadname.add("Hf. Arbejderboligernes Kolonihaver");
        roadname.add("Kaj 22");
        roadname.add("Løjt Feriecenter 2");
        roadname.add("6. Julivej");
        roadname.add("4. Maj Stræde");
        roadname.add("Christian 10 Gade");
        roadname.add("Vej 15 Nr");
        roadname.add("Korsnæbvej");


        List<String> city = new ArrayList<>();
        city.add("Ulstrup");
        city.add("V. Velling");
        city.add("Øster Velling");
        city.add("Jystrup");
        city.add("Ringsted");
        city.add("Nørlev");
        city.add("Løkken");
        city.add("Lønstrup");


        List<String> postalcodeAndCity = new ArrayList<>();
        postalcodeAndCity.add("9800 Hjørring");
        postalcodeAndCity.add("4560 Vig");
        postalcodeAndCity.add("4540 Fårevejle");
        postalcodeAndCity.add("8240 Risskov");

        List<List<String>> addresses = new ArrayList<>();
        addresses.add(randomAddresses);
        addresses.add(roadnameAndNumber);
        addresses.add(roadname);
        addresses.add(city);
        addresses.add(postalcodeAndCity);


        Scanner sc = new Scanner(System.in);
        System.out.println("Enter your input: ");
        String input = sc.nextLine();

        //Regular expressions
        List<String> regexList = new ArrayList<>();
        regexList.add("(?<postcode>\\d{4})(?:\\s*)?(?<city>.*)"); //[Postcode] + [City]
        regexList.add(".*"); // [City]
        regexList.add("(?<street>.*)(?:[,\\.]*\\s+)(?<house>\\d+[a-z|A-Z]*)\n"); //[Street] + [House]
        regexList.add("(?<street>[^\\n,]*?)\\s+(?<house>\\d+[A-zÆØÅæøå]*)(?:(?:,)?\\s+(?:(?<floor>[A-z0-9ÆØÅæøå]+(?:\\.)?))?(?:\\s*)?(?:(?<side>[A-z0-9ÆØÅæøå\\. ]*?))?(?:,)?(?:\\s*)?(?<postcode>\\d{4})(?:\\s*)?(?<city>.*))?");

        //Creating a pattern objects
        List<Pattern> patterns = new ArrayList<>();
        for (int i = 0; i < regexList.size(); i++) {
            Pattern temp = Pattern.compile(regexList.get(i));
            patterns.add(temp);
        }

        //Creating an List object
        for (int i = 0; i < patterns.size(); i++) {
            Matcher matcher = patterns.get(i).matcher(input);
            if (matcher.find()) {
                System.out.println("For regex " + i + " " + input + " is valid");
                if (i == 0) {
                    System.out.println("searching in postalcode and city");
                    if (addresses.get(4).contains(input)) {
                        System.out.println("Match found: " + matcher.group("postcode") + " " + matcher.group("city"));
                    }
                } else if (i == 1) {
                    System.out.println("Searching in city");
                    ArrayList<RadixNode> suggestions = tree1.getSuggestions(input);
                    System.out.println("Suggestions:");
                    for (RadixNode r : suggestions) {
                        System.out.println(r.getContent());
                    }
                    if (addresses.get(3).contains(input)) {
                        System.out.println("Lookup:" + tree1.lookupNode(input).getFullName());
                        System.out.println("Match found");
                    }
                } else if (i == 2) {
                    System.out.println("Searching in road and number");
                    ArrayList<RadixNode> suggestions = tree.getSuggestions(input);
                    for (RadixNode r : suggestions) {
                        System.out.println(r.getContent());
                    }
                    if (addresses.get(1).contains(input)) {
                        System.out.println("Lookup:" + tree.lookupNode(input).getFullName());
                        System.out.println("Match found");
                    }
                } else if (i == 3) {
                    System.out.println("Searching in random addresses");
                    if (addresses.get(0).contains(input)) System.out.println("Match found");
                }
            } else {
                System.out.println("For regex " + i + " " + input + " is not valid");
            }
        }
    }

    /*public static void main(String[] args) throws Exception {
        Path path = Paths.get("file:///Users/jacobm/Desktop/testdict.txt");
        Spelling spel = new Spelling(path);
        spel.correct("haj");
      /*Set<String> dictionary = new HashSet<>();
      dictionary.add("Hi");
      dictionary.add("Mårdalsvej 12A");
      dictionary.add("Udemarken 5");
      dictionary.add("Klitvej 20");
      dictionary.add("Elmevej");
      dictionary.add("Stampen Vej 6 7");
      dictionary.add("Vej 14 Nr 33");
      java.util.Scanner write = new Scanner(System.in);
      System.out.println("Type a sentence and I will check your spelling/correct words :)");
      String sentence = write.nextLine();
      String[] splitSentence = sentence.split(" ");
      for (String word : splitSentence)
         if (dictionary.contains(word))
            System.out.println(word + " : correct");
         else
            System.out.println(word + " : incorrect");*/
    //}
}