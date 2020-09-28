package objects;

import java.util.HashMap;
import java.util.List;

public class Rotaractor extends Participant {
    public Rotaractor () {
        super(ParticipantType.Rotaractor);
    }

    public Rotaractor (final HashMap<String, Object> node, final List<String> possibleMatches, final String match) {
        super(ParticipantType.Rotaractor, node, possibleMatches, match);
    }
}
