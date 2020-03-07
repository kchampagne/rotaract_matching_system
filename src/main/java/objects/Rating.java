package objects;

public class Rating extends Survey {
    private int scaleMin = 1;
    private int scaleMax = 5;

    public Rating (String name, SurveyType surveyType) {
        super(name, surveyType);
    }

    public int getScaleMin() {
        return scaleMin;
    }

    public void setScaleMin(int scaleMin) {
        this.scaleMin = scaleMin;
    }

    public int getScaleMax() {
        return scaleMax;
    }

    public void setScaleMax(int scaleMax) {
        this.scaleMax = scaleMax;
    }
}
