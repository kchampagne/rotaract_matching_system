package objects;

public class Rating extends WeightedQuestion {
    private int scaleMin = 1;
    private int scaleMax = 5;

    public Rating (final String name, final SurveyType surveyType, final double weight) {
        super(name, surveyType, weight);
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
