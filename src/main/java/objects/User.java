package objects;

import java.util.HashMap;

public class User {
    UserType type;
    String clubName;
    String email;
    String name;
    String timestamp;
    HashMap<String, Object> surveyAnswers;
    HashMap<String, Answer> rankingAnswers;
    HashMap<String, Answer> ratingAnswers;


    public enum UserType {
        Rotarian,
        Rotaractor
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
