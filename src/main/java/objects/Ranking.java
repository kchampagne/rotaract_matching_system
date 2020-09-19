package objects;

import java.util.List;

public class Ranking extends WeightedQuestion {
    private List<String> options;

    public Ranking (final String name, final SurveyType surveyType, final double weight, final List<String> options) {
        super(name, surveyType, weight);

        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }
}
