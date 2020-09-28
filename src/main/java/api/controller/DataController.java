package api.controller;

import dataHandler.DataParser;
import dataHandler.Main;
import dataHandler.utils.SurveyRegistry;
import database.Const;
import database.DbFunctions;
import objects.Participant;
import objects.Participant.ParticipantType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@RestController
@RequestMapping(value = "/data")
public class DataController {

    @PostMapping("/initDefaults/")
    public ResponseEntity initDefaults(){
        Main.main(new String[] {});
        return ResponseEntity.ok().build();
    }

    @PostMapping("csv")
    public ResponseEntity ingestCsv(@RequestParam(name = "type", required = true) final String typeStr,
                                    @RequestParam("file") MultipartFile file) {
        ParticipantType type = ParticipantType.valueOf(typeStr);
        DbFunctions db = new DbFunctions();

        if (type.name().equals(ParticipantType.Rotaractor.name())) {

            SurveyRegistry.getRotaractInstance().registerFromJson(Const.projectDir + "\\src\\main\\resources\\rotaract_survey4-19-2020.json");
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                List<Participant> participants = DataParser.parse(reader, Participant.ParticipantType.Rotaractor);
                db.createRotaractors(participants);
            } catch (Exception ex) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            SurveyRegistry.getRotaryInstance().registerFromJson(Const.projectDir + "\\src\\main\\resources\\rotarian_survey1-19-2020.json");
            try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                List<Participant> participants = DataParser.parse(reader, ParticipantType.Rotarian);
                db.createRotarians(participants);
            } catch (Exception ex) {
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.ok().build();
    }
}
