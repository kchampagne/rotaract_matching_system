package dataHandler;

import database.Const;
import database.DbFunctions;
import objects.Participant;
import dataHandler.utils.SurveyRegistry;

import java.util.List;

public class Main {
    public static String rotarian_json = Const.projectDir + "\\src\\main\\resources\\rotarian_survey1-19-2020.json";
    public static String rotarian_csv = Const.projectDir + "\\src\\main\\resources\\rotarian_survey1-19-2020.csv";
    public static String rotaract_json = Const.projectDir + "\\src\\main\\resources\\rotaract_survey4-19-2020.json";
    public static String rotaract_csv = Const.projectDir + "\\src\\main\\resources\\rotaract_survey4-19-2020_edited.csv";

    public static void main(String[] args) {
        initRotarians(rotarian_json, rotarian_csv);
        initRotaractors(rotaract_json, rotaract_csv);
    }

    public static void initRotarians(final String json, final String csv) {
        DbFunctions db = new DbFunctions();

        SurveyRegistry.getRotaryInstance().registerFromJson(json);
        List<Participant> participants = DataParser.parseFromPath(csv, Participant.ParticipantType.Rotarian);
        db.createRotarians(participants);
    }

    public static void initRotaractors(final String json, final String csv) {
        DbFunctions db = new DbFunctions();

        SurveyRegistry.getRotaractInstance().registerFromJson(json);
        List<Participant> participants = DataParser.parseFromPath(csv, Participant.ParticipantType.Rotaractor);
        db.createRotaractors(participants);
    }
}
