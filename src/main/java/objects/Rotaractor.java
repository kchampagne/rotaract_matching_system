package objects;

import java.util.HashMap;

public class Rotaractor extends Participant {
    public Rotaractor () {
        super(ParticipantType.Rotaractor);
    }

    public Rotaractor (final HashMap<String, Object> node) {
        super(ParticipantType.Rotaractor, node);
    }
}
