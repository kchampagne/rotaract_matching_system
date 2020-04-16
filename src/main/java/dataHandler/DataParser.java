package dataHandler;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import database.Const;
import objects.Answer;
import objects.Survey;
import objects.User;
import objects.User.UserType;
import dataHandler.utils.SurveyRegistry;

import java.io.*;
import java.util.*;

public class DataParser {
    private static String cvsSplitBy = "\",\"";

    static List<User> parse(String path, UserType type) {
        List<User> users = new ArrayList<>();
        List<Survey> registry = SurveyRegistry.getInstance().getRegistry();
        String[] line;

        Class<?> clazz = getConstructor(type);

        try {
            CSVReader reader = new CSVReader(new FileReader(path));

            // skip line one with question names
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                int iter = 0;
                HashMap<String, Object> answers = new HashMap<>();
                HashMap<String, Answer> rankingAnswers = new HashMap<>();
                HashMap<String, Answer> ratingAnswers = new HashMap<>();
                User user = (User) clazz.newInstance();

                ListIterator<Survey> list = registry.listIterator();
                for (int i = 0; i < registry.size(); i++) {
                    Survey survey = list.next();
                    switch (survey.getSurveyType()) {
                        case TIMESTAMP:
                            user.setTimestamp(line[iter]);
                            answers.put(Const.TIMESTAMP, line[iter]);
                            iter++;
                            break;
                        case NAME:
                            user.setName(line[iter]);
                            answers.put(Const.NAME, line[iter]);
                            iter++;
                            break;
                        case CLUB:
                            user.setClubName(line[iter]);
                            answers.put(Const.CLUB_NAME, line[iter]);
                            iter++;
                            break;
                        case RANKING:
                            List<String> options = SurveyRegistry.getInstance().getRankingOptions(survey.getName());
                            Iterator<String> listIter = options.listIterator();
                            while (listIter.hasNext()) {
                                rankingAnswers.put(listIter.next(), new Answer<>(Integer.parseInt(line[iter])));
                                iter++;
                            }
                            break;
                        case RATING:
                            ratingAnswers.put(survey.getName(), new Answer<>(Integer.parseInt(line[iter])));
                            iter++;
                            break;
                        default:
                            answers.put(survey.getName(), line[iter]);
                            iter++;
                    }
                }

                answers.put(Const.ID, "" + user.getTimestamp() + "_" + user.getName() + "_" + user.getClubName());

                user.setRatingAnswers(ratingAnswers);
                user.setRankingAnswers(rankingAnswers);
                user.setSurveyAnswers(answers);

                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        return users;
    }

    private static Class<?> getConstructor(UserType type) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("objects." + type.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }
}
