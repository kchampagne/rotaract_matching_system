package objects;

import java.util.HashMap;

public class Rotarian extends User {
    public Rotarian () {
        super(UserType.Rotarian);
    }

    public Rotarian (final HashMap<String, Object> node) {
        super(UserType.Rotarian, node);
    }
}
