package objects;

import java.util.HashMap;

public class Rotaractor extends User {
    public Rotaractor () {
        super(UserType.Rotaractor);
    }

    public Rotaractor (final HashMap<String, Object> node) {
        super(UserType.Rotaractor, node);
    }
}
