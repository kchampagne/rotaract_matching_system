package api.controller;

import database.DbFunctions;
import objects.Rotaractor;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@RequestMapping(value = "/rotaractor")
public class RotaractorController {

    private final DbFunctions dbFunctions = new DbFunctions();

    @GetMapping("")
    public Response getRotaractors() {
        List<Rotaractor> rotaractors = dbFunctions.readRotaractors();

        if (rotaractors.size() < 1) {
            return Response.noContent().build();
        } else {
            return Response.ok(rotaractors).build();
        }
    }

    @GetMapping("/{id}")
    public Response getRotaractor(@PathVariable final String id) {
        Rotaractor rotaractor = dbFunctions.readRotaractor(id);

        if (rotaractor != null && rotaractor.getId().equals(id)) {
            return Response.ok(rotaractor).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
