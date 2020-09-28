package api.controller;

import database.DbFunctions;
import objects.Rotarian;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rotarian")
public class RotarianController {

    private final DbFunctions dbFunctions = new DbFunctions();

    @GetMapping("")
    public ResponseEntity<List<Rotarian>> getRotarians(@RequestParam(name="matched", defaultValue="false") final String matchedStr) {
        final boolean matched = Boolean.parseBoolean(matchedStr);

        List<Rotarian> rotarians = dbFunctions.readRotarians(matched);

        if (rotarians.size() < 1) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(rotarians);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rotarian> getRotarian(@PathVariable final String id) {
        Rotarian rotarian = dbFunctions.readRotarian(id);

        if (rotarian != null && rotarian.getId().equals(id)) {
            return ResponseEntity.ok(rotarian);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
