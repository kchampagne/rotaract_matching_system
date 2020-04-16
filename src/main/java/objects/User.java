package objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import database.Const;

import java.util.HashMap;

@JsonIgnoreProperties(value={
        "email",
        "timestamp",
        "surveyAnswers",
        "rankingAnswers",
        "ratingAnswers"
})
public class User {
    protected String id;
    protected UserType type;
    protected String clubName;
    protected String email;
    protected String name;
    protected String timestamp;
    private HashMap<String, Object> surveyAnswers;
    private HashMap<String, Answer> rankingAnswers;
    private HashMap<String, Answer> ratingAnswers;

    public User (final UserType type) {
        this.type = type;
    }

    public User (final UserType type, final HashMap<String, Object> node) {
        this.type = type;

        if (node.containsKey(Const.ID)) {
            this.id = node.get(Const.ID).toString();
        } else {
            throw new IllegalArgumentException("ERROR :: No id is present in node for a " + this.type);
        }
        if (node.containsKey(Const.NAME)) {
            this.name = node.get(Const.NAME).toString();
        } else {
            throw new IllegalArgumentException("ERROR :: No name is present in node with id of " +
                    this.id + " for a " + this.type);
        }
        if (node.containsKey(Const.CLUB_NAME)) {
            this.clubName = node.get(Const.CLUB_NAME).toString();
        } else {
            throw new IllegalArgumentException("ERROR :: No club name is present in node with id of " +
                    this.id + " for " + this.name + " a " + this.type);
        }
    }

    public enum UserType {
        Rotarian,
        Rotaractor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, Object> getSurveyAnswers() {
        return surveyAnswers;
    }

    public void setSurveyAnswers(HashMap<String, Object> surveyAnswers) {
        this.surveyAnswers = surveyAnswers;
    }

    public HashMap<String, Answer> getRankingAnswers() {
        return rankingAnswers;
    }

    public void setRankingAnswers(HashMap<String, Answer> rankingAnswers) {
        this.rankingAnswers = rankingAnswers;
    }

    public HashMap<String, Answer> getRatingAnswers() {
        return ratingAnswers;
    }

    public void setRatingAnswers(HashMap<String, Answer> ratingAnswers) {
        this.ratingAnswers = ratingAnswers;
    }
}
