package dataHandler.utils;

import database.Const;
import database.DbFunctions;
import objects.Ranking;
import objects.Rating;
import objects.Survey;
import objects.Survey.SurveyType;
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
    private ArrayList<Survey> registry = new ArrayList<>();
    private HashMap<String, Ranking> rankings = new HashMap<>();
    private HashMap<String, Rating> ratings = new HashMap<>();

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
            JSONArray survey = (JSONArray) obj.get("survey");

            for (int i = 0; i < survey.size(); i++) {
                JSONObject surveyObj = (JSONObject) survey.get(i);
                switch (SurveyType.valueOf(surveyObj.get("questionType").toString())) {
                    case RATING:
                        Rating ratingItem = new Rating(surveyObj.get("question").toString().trim(),
                                SurveyType.valueOf(surveyObj.get("questionType").toString()));
                        this.ratings.put(ratingItem.getName(), ratingItem);
                        this.registry.add(ratingItem);
                        break;
                    case RANKING:
                        Ranking rankingItem = new Ranking(surveyObj.get("question").toString().trim(),
                                SurveyType.valueOf(surveyObj.get("questionType").toString()),
                                new ArrayList<String>((JSONArray) surveyObj.get("options")));
                        this.rankings.put(rankingItem.getName(), rankingItem);
                        this.registry.add(rankingItem);
                        break;
                    default:
                        Survey surveyItem = new Survey(surveyObj.get("question").toString().trim(),
                                SurveyType.valueOf(surveyObj.get("questionType").toString()));
                        this.registry.add(surveyItem);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        checkForAndAddNodes();
    }

    private void checkForAndAddNodes() {
        DbFunctions dbFunctions = new DbFunctions();

        for (String key: rankings.keySet()) {
            Ranking ranking = rankings.get(key);

            for (String option: ranking.getOptions()) {
                HashMap<String, Object> map = new HashMap<>();

                map.put(Const.ID, option);

                dbFunctions.findOrAddNode(Const.RANKING, map);
            }
        }
        for (String key: ratings.keySet()) {
            HashMap<String, Object> map = new HashMap<>();
            Rating rating = ratings.get(key);

            map.put(Const.ID, rating.getName());
            map.put(Const.SCALE_MAX, rating.getScaleMax());
            map.put(Const.SCALE_MIN, rating.getScaleMin());

            dbFunctions.findOrAddNode(Const.RATING, map);
        }
    }

    public List<Survey> getRegistry() {
        return Collections.unmodifiableList(registry);
    }

    public Map<String, Rating> getRatingsRegistry() {
        return Collections.unmodifiableMap(ratings);
    }

    public Set<String> getRankingNames() { return Collections.unmodifiableSet(rankings.keySet()); }

    public Map<String, Ranking> getRankings() { return Collections.unmodifiableMap(rankings); }

    public List<String> getRankingOptions(final String rankingName) {
        if (rankings.containsKey(rankingName)) {
            return rankings.get(rankingName).getOptions();
        }
        throw new IllegalArgumentException("This RANKING does not have options.");
    }
}
