package api.controller;

import database.DbFunctions;
import objects.Rotarian;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@RequestMapping(value = "/rotarian")
public class RotarianController {

    private final DbFunctions dbFunctions = new DbFunctions();

    @GetMapping("")
    public Response getRotarians(@RequestParam(name="matched", defaultValue="false") final String matchedStr) {
        final boolean matched = Boolean.parseBoolean(matchedStr);

        List<Rotarian> rotarians = dbFunctions.readRotarians(matched);

        if (rotarians.size() < 1) {
            return Response.noContent().build();
        } else {
            return Response.ok(rotarians).build();
        }
    }

    @GetMapping("/{id}")
    public Response getRotarian(@PathVariable final String id) {
        Rotarian rotarian = dbFunctions.readRotarian(id);

        if (rotarian != null && rotarian.getId().equals(id)) {
            return Response.ok(rotarian).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
