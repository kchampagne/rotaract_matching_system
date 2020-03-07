package database;

import objects.Survey.SurveyType;
import objects.User.UserType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class Const {
    /**
     * Node Properties
     */
    public static final String ID = "id";

    public static final String NAME = "name";

    public static final String SCALE_MAX = "scaleMax";

    public static final String SCALE_MIN = "scaleMin";

    /**
     * Database Labels
     */
    public static final Label ROOT_LABEL = Label.label("ROOT");

    public static final Label ROTARIAN = Label.label(UserType.Rotarian.name().toUpperCase());

    public static final Label ROTARACTOR = Label.label(UserType.Rotaractor.name().toUpperCase());

    public static final Label MATCHED =  Label.label("MATCHED");

    public static final Label UNMATCHED =  Label.label("UNMATCHED");

    public static final Label RANKING = Label.label(SurveyType.RANKING.name());

    public static final Label RATING = Label.label(SurveyType.RATING.name());

    /**
     * Database Relationships
     */
    public static final RelationshipType RELATE_ROOT_ROTARIAN =
            RelationshipType.withName(UserType.Rotarian.name().toUpperCase());

    public static final RelationshipType RELATE_ROOT_ROTARACTOR =
            RelationshipType.withName(UserType.Rotaractor.name().toUpperCase());

    public static final RelationshipType RELATE_ROOT_RANKING = RelationshipType.withName(SurveyType.RANKING.name());

    public static final RelationshipType RELATE_ROOT_RATING = RelationshipType.withName(SurveyType.RATING.name());

    public static final RelationshipType POSSIBLE_MATCH = RelationshipType.withName("POSSIBLE_MATCH");

    public static final RelationshipType MATCH = RelationshipType.withName("MATCH");

    // Possible ranks for "RANKING" questions
    public static final RelationshipType RANK_ONE = RelationshipType.withName("RANK_ONE");
    public static final RelationshipType RANK_TWO = RelationshipType.withName("RANK_TWO");
    public static final RelationshipType RANK_THREE = RelationshipType.withName("RANK_THREE");
    public static final RelationshipType RANK_FOUR = RelationshipType.withName("RANK_FOUR");
    public static final RelationshipType RANK_FIVE = RelationshipType.withName("RANK_FIVE");

//    private static Map<String, Label> labels = new HashMap<>();
//    {
//        for (UserType userType: UserType.values()) {
//            String type = userType.name();
//            labels.put(type, Label.label(type));
//        }
//    }
//
//    public static Map<String, Label> getLabels() {
//        return Collections.unmodifiableMap(labels);
//    }
}
