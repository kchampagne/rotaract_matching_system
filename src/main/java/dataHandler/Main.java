package dataHandler;

import database.Const;
import database.DbFunctions;
import objects.User;
import dataHandler.utils.SurveyRegistry;

import java.util.List;

public class Main {
    public static String json = Const.projectDir + "\\src\\main\\resources\\rotarian_survey1-19-2020.json";
    public static String csv = Const.projectDir + "\\src\\main\\resources\\rotarian_survey1-19-2020.csv";

    public static void main(String[] args) {
        initRotarians(json, csv);
    }

    public static void initRotarians(String json, String csv) {
        DbFunctions db = new DbFunctions();

        SurveyRegistry.getInstance().registerFromJson(json);
        List<User> users = DataParser.parse(csv, User.UserType.Rotarian);
        db.createRotarians(users);
    }
}
