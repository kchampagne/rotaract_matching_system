package dataHandler;

import database.DbFunctions;
import objects.User;
import dataHandler.utils.SurveyRegistry;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DbFunctions db = new DbFunctions();

        SurveyRegistry.getInstance().registerFromJson("C:\\Users\\the88\\Workspace\\rotaract_matching_system\\src\\main\\resources\\rotarian_survey1-19-2020.json");
        List<User> users = DataParser.parse("C:\\Users\\the88\\Workspace\\rotaract_matching_system\\src\\main\\resources\\rotarian_survey1-19-2020.csv",
                User.UserType.Rotarian);
        db.createRotarians(users);
    }
}
