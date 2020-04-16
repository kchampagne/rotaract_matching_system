package api.controller;

import database.DbFunctions;
import objects.Rotarian;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;
import java.util.List;

@RestController
@RequestMapping(value = "/rotarian")
public class RotarianController {

    private final DbFunctions dbFunctions = new DbFunctions();

    @GetMapping("/")
    public Response getRotarians() {
        List<Rotarian> rotarians = dbFunctions.readRotarians();

        if (rotarians.size() < 1) {
            return Response.noContent().build();
        } else {
            return Response.ok(rotarians).build();
        }
    }
}