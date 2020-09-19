package objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import database.Const;
import scala.collection.immutable.Stream;

import java.util.HashMap;
import java.util.List;

@JsonIgnoreProperties(value={
        "matchValue",
        "email",
        "timestamp",
        "weightedAnswers"
})
public class Participant {
    @JsonProperty(value = "id")
    protected String id;
    @JsonProperty(value = "type")
    protected ParticipantType type;
    @JsonProperty(value = "clubName")
    protected String clubName;
    @JsonProperty(value = "name")
    protected String name;
    @JsonProperty(value = "surveyAnswers")
    private HashMap<String, Object> surveyAnswers;

    protected String email;
    protected String timestamp;
    protected double matchValue = 0;
    private HashMap<String, Double> weightedAnswers;

    public Participant(final ParticipantType type) {
        this.type = type;
    }

    public Participant(final ParticipantType type, final HashMap<String, Object> node) {
        this.type = type;

        if (node.containsKey(Const.ID)) {
            this.id = node.get(Const.ID).toString();
        } else {
            throw new IllegalArgumentException("ERROR :: No id is present in node for a " + this.type);
        }
        if (node.containsKey(Const.TYPE)) {
            this.type = ParticipantType.valueOf(node.get(Const.TYPE).toString());
        } else {
            throw new IllegalArgumentException("ERROR :: No type is present in node for a " + this.type);
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

        this.surveyAnswers = new HashMap<>();
        for (String key: node.keySet()) {
            if (key.startsWith(Const.SURVEY_ANSWER)) {
                this.surveyAnswers.put(key.substring(Const.SURVEY_ANSWER.length()+1), node.get(key));
            }
        }
    }

    public enum ParticipantType {
        Rotarian,
        Rotaractor
    }

    public void setMatchValue(final double weightedValue) {
        matchValue = Math.round(weightedValue * 1000.0) / 1000.0;
    }

    public double getMatchValue() {
        return matchValue;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ParticipantType getType() {
        return type;
    }

    public void setType(ParticipantType type) {
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

    public HashMap<String, Double> getWeightedAnswers() {
        return weightedAnswers;
    }

    public void setWeightedAnswers(HashMap<String, Double> weightedAnswers) {
        this.weightedAnswers = weightedAnswers;
    }

    @JsonIgnore
    public HashMap<String, Object> getInfoMap() {
        HashMap<String, Object> infoMap = new HashMap<>();
        
        infoMap.put(Const.MATCH_VALUE, matchValue);
        infoMap.put(Const.ID, id);
        infoMap.put(Const.TYPE, type.toString());
        infoMap.put(Const.CLUB_NAME, clubName);
        if (email != null) {
            infoMap.put(Const.EMAIL, email);
        }
        infoMap.put(Const.NAME, name);
        infoMap.put(Const.TIMESTAMP, timestamp);
        
        for (String key: surveyAnswers.keySet()) {
            infoMap.put(String.format(Const.NODE_SERIALIZE_FORMAT, Const.SURVEY_ANSWER, key), surveyAnswers.get(key));
        }

        for (String key: weightedAnswers.keySet()) {
            infoMap.put(String.format(Const.NODE_SERIALIZE_FORMAT, Const.WEIGHTED_ANSWER, key), weightedAnswers.get(key));
        }
        
        return infoMap;
    }
}
