package objects;

import java.util.HashMap;
import java.util.List;

public class Rotarian extends Participant {
    public Rotarian () {
        super(ParticipantType.Rotarian);
    }

    public Rotarian (final HashMap<String, Object> node, final List<String> possibleMatches, final String match) {
        super(ParticipantType.Rotarian, node, possibleMatches, match);
    }
}
