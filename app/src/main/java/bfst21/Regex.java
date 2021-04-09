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
    public Regex(List<Text> regexVisualisers){
        this.regexVisualisers = regexVisualisers;
        //Regular expressions
        regexList = new ArrayList<>();
        regexList.add("(?<postcode>\\d{4})(?:\\s*)?(?<city>.*)");
        regexList.add("(?<street>[^\\n,]*?)\\s+(?<house>\\d+[A-zÆØÅæøå]*)(?:(?:,)?\\s+(?:(?<floor>[A-z0-9ÆØÅæøå]+(?:\\.)?))?(?:\\s*)?(?:(?<side>[A-z0-9ÆØÅæøå\\. ]*?))?(?:,)?(?:\\s*)?(?<postcode>\\d{4})(?:\\s*)?(?<city>.*))?");

        //Creating pattern objects
        patterns = new ArrayList<>();
        for (int i = 0;i < regexList.size(); i++) {
            Pattern temp = Pattern.compile(regexList.get(i));
            patterns.add(temp);
        }
    }

    public void run(String text){
        for (int i = 0; i<patterns.size(); i++) {
            Matcher matcher = patterns.get(i).matcher(text);
            regexVisualisers.get(i).getStyleClass().clear();
            if(matcher.find()) {
                regexVisualisers.get(i).getStyleClass().add("regexMatchGreen");
            } else {
                regexVisualisers.get(i).getStyleClass().add("regexMatchRed");
            }
        }
    }
}
