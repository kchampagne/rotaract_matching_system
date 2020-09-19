package database;

import objects.Question.SurveyType;
import objects.Participant.ParticipantType;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

public class Const {

    /**
     * Misc Utils
     */
    public static final String projectDir = System.getProperty("user.dir");

    /**
     * Node Properties
     */
    public static final String MATCH_VALUE = "matchValue";

    public static final String ID = "id";

    public static final String TYPE = "type";

    public static final String EMAIL = "email";

    public static final String NAME = "name";

    public static final String TIMESTAMP = "timestamp";

    public static final String CLUB_NAME = "clubName";

    public static final String SCALE_MAX = "scaleMax";

    public static final String SCALE_MIN = "scaleMin";

    public static final String SURVEY_ANSWER = "survey_answer";

    public static final String WEIGHTED_ANSWER = "weighted_answer";

    /**
     * Serialize Format
     */
    public static final String NODE_SERIALIZE_FORMAT = "%s_%s";

    /**
     * Database Labels
     */
    public static final Label ROOT_LABEL = Label.label("ROOT");

    public static final Label ROTARACTOR_MATCH = Label.label("ROTARACTOR_MATCH");

    public static final Label ROTARIAN_MATCH = Label.label("ROTARIAN_MATCH");

    public static final Label ROTARIAN = Label.label(ParticipantType.Rotarian.name().toUpperCase());

    public static final Label ROTARACTOR = Label.label(ParticipantType.Rotaractor.name().toUpperCase());

    public static final Label MATCHED =  Label.label("MATCHED");

    public static final Label UNMATCHED =  Label.label("UNMATCHED");

    public static final Label RANKING = Label.label(SurveyType.RANKING.name());

    public static final Label RATING = Label.label(SurveyType.RATING.name());

    /**
     * Database Relationships
     */
    public static final RelationshipType ROOT_MATCH_VALUES = RelationshipType.withName("ROOT_MATCH_VALUES");

    public static final RelationshipType RELATE_ROOT_ROTARIAN =
            RelationshipType.withName(ParticipantType.Rotarian.name().toUpperCase());

    public static final RelationshipType RELATE_ROOT_ROTARACTOR =
            RelationshipType.withName(ParticipantType.Rotaractor.name().toUpperCase());

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
//        for (ParticipantType userType: ParticipantType.values()) {
//            String type = userType.name();
//            labels.put(type, Label.label(type));
//        }
//    }
//
//    public static Map<String, Label> getLabels() {
//        return Collections.unmodifiableMap(labels);
//    }
}
