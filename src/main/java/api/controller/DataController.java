package api.controller;

import dataHandler.Main;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data")
public class DataController {

    @PostMapping("/initDefaults/")
    public ResponseEntity initDefaults(){
        Main.main(new String[] {});
        return ResponseEntity.ok().build();
    }
}
