package dataHandler.utils;

import objects.Question;
import objects.Ranking;
import objects.Question.SurveyType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SurveyRegistry {
    private static SurveyRegistry rotary;
    private static SurveyRegistry rotaract;
    private List<Question> registry = new ArrayList<>();
    private List<String> matchingQuestions = new ArrayList<>();
    private HashMap<String, Ranking> rankings = new HashMap<>();

    private String questionStr = "question";
    private String questionTypeStr = "type";
    private String surveyStr = "survey";
    private String optionsStr = "options";
    private String weightStr = "weight";

    public static SurveyRegistry getRotaryInstance() {
        if (rotary == null) {
            rotary = new SurveyRegistry();
        }
        return rotary;
    }

    public static SurveyRegistry getRotaractInstance() {
        if (rotaract == null) {
            rotaract = new SurveyRegistry();
        }
        return rotaract;
    }

    public void registerFromJson(final String path) {
        JSONParser jsonParser = new JSONParser();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            JSONObject obj = (JSONObject) jsonParser.parse(br);
            JSONArray survey = (JSONArray) obj.get(surveyStr);

            for (int i = 0; i < survey.size(); i++) {
                JSONObject surveyObj = (JSONObject) survey.get(i);
                switch (SurveyType.valueOf(surveyObj.get(questionTypeStr).toString().toUpperCase())) {
                    case RANKING:
                        Ranking rankingItem = new Ranking(surveyObj.get(questionStr).toString().trim(),
                                SurveyType.valueOf(surveyObj.get(questionTypeStr).toString()),
                                Double.parseDouble(surveyObj.get(weightStr).toString()),
                                new ArrayList<String>((JSONArray) surveyObj.get(optionsStr)));
                        this.rankings.put(rankingItem.getName(), rankingItem);
                        this.registry.add(rankingItem);
                        break;
                    case LONGANSWER:
                    case SHORTANSWER:
                        matchingQuestions.add(surveyObj.get(questionStr).toString().trim());
                    default:
                        Question questionItem = new Question(surveyObj.get(questionStr).toString().trim(),
                                SurveyType.valueOf(surveyObj.get(questionTypeStr).toString()));
                        this.registry.add(questionItem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public List<Question> getRegistry() {
        return Collections.unmodifiableList(registry);
    }

    public List<String> getMatchingQuestions() {
        return matchingQuestions;
    }

    public List<String> getRankingOptions(final String rankingName) {
        if (rankings.containsKey(rankingName)) {
            return rankings.get(rankingName).getOptions();
        }
        throw new IllegalArgumentException("This RANKING does not have options.");
    }
}
