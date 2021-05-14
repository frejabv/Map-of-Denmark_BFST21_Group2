package bfst21;

import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Regex {
    List<String> regexList;
    List<Pattern> patterns;
    List<Text> regexVisualisers;

    public Regex(List<Text> regexVisualisers) {
        this.regexVisualisers = regexVisualisers;
        //Regular expressions
        regexList = new ArrayList<>();

        regexList.add("^(?<street>[^\\n,]*?)\\s+(?<house>\\d+[A-zÆØÅæøå]*)\\s+(?<floor>[A-z0-9ÆØÅæøå]+(?:\\.)?)\\s+(?<side>[A-z0-9ÆØÅæøå\\. ]+)\\s+(?<postcode>\\d{4})(?<more>.*)$");
        regexList.add("^(?<street>[^\\n,]*?)\\s+(?<house>\\d+[A-zÆØÅæøå]*)\\s+(?<floor>[A-z0-9ÆØÅæøå]+(?:\\.)?)\\s+(?<postcode>\\d{4})(?<more>.*)(?<side>)$");
        regexList.add("^(?<street>[^\\n,]*?)\\s+(?<house>\\d+[A-zÆØÅæøå]*)\\s+(?<postcode>\\d{4})(?<more>.*)(?<floor>)(?<side>)$");
        regexList.add("^(?<street>[^\\n,]*?)\\s+(?<house>\\d+[A-zÆØÅæøå]*)\\s+(?<floor>[A-z0-9ÆØÅæøå]+(?:\\.)?)\\s+(?<side>[A-z0-9ÆØÅæøå\\. ]+[^ \\d+])(?<more>.*)(?<postcode>)$");
        regexList.add("^(?<street>[^\\n,]*[^ \\d])\\s+(?<house>\\d+[A-zÆØÅæøå]?)(?<more>.*)(?<floor>)(?<side>)(?<postcode>)$");
        regexList.add("^(?<street>[^\\n,]+?)?(?<more>[ .*]*)(?<house>)(?<floor>)(?<side>)(?<postcode>)$");

        //Creating pattern objects
        patterns = new ArrayList<>();
        for (String s : regexList) {
            Pattern temp = Pattern.compile(s);
            patterns.add(temp);
        }
    }

    public String run(String text) {
        String result = "";
        if (text.length() == 0) {
            return result;
        }
        for (int i = 0; i < patterns.size(); i++) {
            Matcher matcher = patterns.get(i).matcher(text.replace(",", ""));
            regexVisualisers.get(i).getStyleClass().clear();
            if (matcher.find()) {
                regexVisualisers.get(i).getStyleClass().add("regexMatchGreen");
                boolean street = matcher.group("street").length() > 0;
                boolean house = matcher.group("house").length() > 0;
                boolean postcode = matcher.group("postcode").length() > 0;
                boolean more = matcher.group("more").length() > 0;
                if (street) {
                    result += matcher.group("street").toString();
                }
                if (house) {
                    result += " " + matcher.group("house").toString();
                }
                if (postcode) {
                    result += " " + matcher.group("postcode").toString();
                }
                if (more) {
                    result += matcher.group("more").toString();
                }
                return result;
            } else {
                regexVisualisers.get(i).getStyleClass().add("regexMatchRed");
            }
        }
        return result;
    }

}
