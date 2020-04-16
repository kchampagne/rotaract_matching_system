package api.controller;

import database.DbFunctions;
import objects.Rotarian;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/match")
public class MatchController {

    private DbFunctions dbFunctions = new DbFunctions();

    @GetMapping("/rotaractor")
    public List<Rotarian> getPossibleMatches(@RequestParam final String id) {
        return dbFunctions.readPossibleRotarianMatchesForRotaractor(id);
    }

    @PostMapping("")
    public ResponseEntity createMatch(@RequestParam final String rotarianId,
                                      @RequestParam final String rotaractorId) {
        dbFunctions.makeMatch(rotarianId, rotaractorId);
        return new ResponseEntity(HttpStatus.OK);
    }
}
