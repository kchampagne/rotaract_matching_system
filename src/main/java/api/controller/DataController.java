package api.controller;

import dataHandler.Main;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping(value = "/data")
public class DataController {

    @PostMapping("/initDefaults/")
    public Response initDefaults(){
        Main.initRotarians(Main.json, Main.csv);
        return Response.ok().build();
    }
}
