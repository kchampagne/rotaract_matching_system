package api.controller;

import database.DbFunctions;
import objects.Rotarian;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/match")
public class MatchController {

    private DbFunctions dbFunctions = new DbFunctions();

    @GetMapping("/rotaractor/{id}")
    public ResponseEntity<List<Rotarian>> getPossibleMatches(@PathVariable final String id) {
        List<Rotarian> rotarians = dbFunctions.readPossibleRotarianMatchesForRotaractor(id);

        if (rotarians.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rotarians);
    }

    @PostMapping("/{rotaractorId}/{rotarianId}")
    public ResponseEntity createMatch(@PathVariable final String rotaractorId,
                                      @PathVariable final String rotarianId) {
        dbFunctions.makeMatch(rotarianId, rotaractorId);
        return ResponseEntity.ok().build();
    }
}
