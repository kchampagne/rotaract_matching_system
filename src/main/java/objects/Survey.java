package objects;

public class Survey {
    String name;
    SurveyType surveyType;

    public enum SurveyType {
        CLUB,
        LONGANSWER,
        NAME,
        RANKING,
        RATING,
        SHORTANSWER,
        TIMESTAMP,
    }

    public Survey (String name, SurveyType surveyType) {
        this.name = name;
        this.surveyType = surveyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SurveyType getSurveyType() {
        return surveyType;
    }

    public void setSurveyType(SurveyType surveyType) {
        this.surveyType = surveyType;
    }
}
