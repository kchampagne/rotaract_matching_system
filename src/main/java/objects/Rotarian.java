package objects;

import java.util.HashMap;

public class Rotarian extends Participant {
    public Rotarian () {
        super(ParticipantType.Rotarian);
    }

    public Rotarian (final HashMap<String, Object> node) {
        super(ParticipantType.Rotarian, node);
    }
}
