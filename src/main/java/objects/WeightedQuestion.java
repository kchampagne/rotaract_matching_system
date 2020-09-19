package objects;

public class WeightedQuestion extends Question {
    protected double weight;

    public WeightedQuestion(final String name, final SurveyType surveyType, final double weight) {
        super(name, surveyType);

        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(final double weight) {
        this.weight = weight;
    }
}
