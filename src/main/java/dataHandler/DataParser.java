package dataHandler;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import database.Const;
import objects.Participant;
import objects.Question;
import objects.Participant.ParticipantType;
import dataHandler.utils.SurveyRegistry;
import objects.WeightedQuestion;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataParser {
    private static String cvsSplitBy = "\",\"";

    static List<Participant> parse(final String path, final ParticipantType type) {
        List<Participant> participants = new ArrayList<>();
        List<Question> registry = getRegistryInstance(type).getRegistry();
        String[] line;

        Class<?> clazz = getConstructor(type);

        try {
            CSVReader reader = new CSVReader(new FileReader(path));

            // skip line one with question names
            reader.readNext();

            while ((line = reader.readNext()) != null) {
                double matchValue = 0;
                int iter = 0;
                HashMap<String, Object> answers = new HashMap<>();
                HashMap<String, Double> weightedAnswers = new HashMap<>();
                Participant participant = (Participant) clazz.newInstance();

                ListIterator<Question> list = registry.listIterator();
                WeightedQuestion weightedQuestion;
                for (int i = 0; i < registry.size(); i++) {
                    Question question = list.next();
                    switch (question.getSurveyType()) {
                        case TIMESTAMP:
                            participant.setTimestamp(handleTimestamp(line[iter]));
                            iter++;
                            break;
                        case NAME:
                            participant.setName(line[iter]);
                            iter++;
                            break;
                        case CLUB:
                            participant.setClubName(line[iter]);
                            iter++;
                            break;
                        case RANKING:
                            weightedQuestion = (WeightedQuestion) question;
                            List<String> options = getRegistryInstance(type).getRankingOptions(weightedQuestion.getName());
                            Iterator<String> listIter = options.listIterator();
                            while (listIter.hasNext()) {
                                final double value = Double.parseDouble(line[iter]);
                                final double weightedValue = (weightedQuestion.getWeight() + (0.01*iter)) * value;
                                matchValue += weightedValue;
                                weightedAnswers.put(listIter.next(), value);
                                iter++;
                            }
                            break;
                        case RATING:
                            weightedQuestion = (WeightedQuestion) question;
                            final double value = Double.parseDouble(line[iter]);
                            final double weightedValue = weightedQuestion.getWeight() * value;
                            matchValue += weightedValue;
                            weightedAnswers.put(question.getName(), value);
                            iter++;
                            break;
                        default:
                            answers.put(question.getName(), line[iter]);
                            iter++;
                    }
                }

                participant.setId(String.format("%s_%s_%s", participant.getTimestamp(),
                        participant.getName().replace(" ", "_"),
                        participant.getClubName().replace(" ", "_")));
                participant.setMatchValue(matchValue);
                participant.setWeightedAnswers(weightedAnswers);
                participant.setSurveyAnswers(answers);

                participants.add(participant);
            }
        } catch (IOException | IllegalAccessException | InstantiationException | CsvValidationException e) {
            e.printStackTrace();
        }

        return participants;
    }

    private static SurveyRegistry getRegistryInstance(final Participant.ParticipantType type) {
        if (type.name().equals(ParticipantType.Rotarian.name())) {
            return SurveyRegistry.getRotaryInstance();
        } else {
            return SurveyRegistry.getRotaractInstance();
        }
    }

    private static Class<?> getConstructor(final ParticipantType type) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName("objects." + type.toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return clazz;
    }

    private static String handleTimestamp(final String input) {
        String timestamp = "";

        String[] timePieces = input.split(" ");

        if (timePieces[1].length() < 8) {
            timePieces[1] = String.format("0%s", timePieces[1]);
        }

        DateFormat df = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss aa", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone(timePieces[3]));
        SimpleDateFormat outputFormat = new SimpleDateFormat(
                "YYYY-MM-dd'T'HH:mm:ss.SS'Z'", Locale.US);
        outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = df.parse(String.format("%s %s %s", timePieces[0], timePieces[1], timePieces[2]));
            timestamp = outputFormat.format(date);
        } catch(ParseException pe){
            pe.printStackTrace();
        }

        return timestamp;
    }
}
