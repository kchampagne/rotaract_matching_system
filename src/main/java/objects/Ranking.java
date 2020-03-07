package objects;

import java.util.List;

public class Ranking extends Survey {
    private List<String> options;

    public Ranking (String name, SurveyType surveyType, List<String> options) {
        super(name, surveyType);

        this.options = options;
    }

    public List<String> getOptions() {
        return options;
    }
}
