package api.controller;

import database.DbFunctions;
import objects.Rotaractor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rotaractor")
public class RotaractorController {

    private final DbFunctions dbFunctions = new DbFunctions();

    @GetMapping("")
    public ResponseEntity<List<Rotaractor>> getRotaractors(@RequestParam(name="matched", defaultValue="false") final String matchedStr) {
        final boolean matched = Boolean.parseBoolean(matchedStr);

        List<Rotaractor> rotaractors = dbFunctions.readRotaractors(matched);

        if (rotaractors.size() < 1) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(rotaractors);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rotaractor> getRotaractor(@PathVariable final String id) {
        Rotaractor rotaractor = dbFunctions.readRotaractor(id);

        if (rotaractor != null && rotaractor.getId().equals(id)) {
            return ResponseEntity.ok(rotaractor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
