package database;

import objects.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.HashMap;
import java.util.List;

public class DbFunctions {
    private static DatabaseUtils database;
    private static Node root;
    private static String dbPath = "C:\\Users\\the88\\Workspace\\rotaract_matching_system\\database\\graph.db";

    public DbFunctions() {
        database = DatabaseUtils.getInstance(dbPath);
        root = database.initRoot();
    }

    public DbFunctions(String path) {
        database = DatabaseUtils.getInstance(path);
        root = database.initRoot();
    }

    public void addRotarians(List<User> rotarians) {
        for (User rotarian: rotarians) {
            addRotarian((Rotarian) rotarian);
        }
        System.out.println("Added Rotarians");
    }

    public void addRotarian(Rotarian rotarian) {
        Node node = database.createNode(Const.ROTARIAN, rotarian.getSurveyAnswers());

        database.addLabels(node, new Label[] {
                Const.ROTARIAN,
                Label.label(rotarian.getName().toUpperCase())
        });

        handleRelationships(node, rotarian);

        database.createRelationship(root, node, Const.RELATE_ROOT_ROTARIAN);
        
        matchRotarian(node);
    }

    private void matchRotarian(Node node) {
    }

    public void addRotaractors(List<User> rotaractors) {
        for (User rotaractor: rotaractors) {
            addRotaractor((Rotaractor) rotaractor);
        }
        System.out.println("Added Rotaractors");
    }

    public void addRotaractor(Rotaractor rotaractor) {
        Node node = database.createNode(Const.ROTARACTOR, rotaractor.getSurveyAnswers());

        database.addLabels(node, new Label[] {
                Const.ROTARACTOR,
                Label.label(rotaractor.getName().toUpperCase())
        });

        handleRelationships(node, rotaractor);

        database.createRelationship(root, node, Const.RELATE_ROOT_ROTARACTOR);
    }

    public void handleRelationships(Node node, User user) {
        HashMap<String, Answer> rankingAnswers = user.getRankingAnswers();
        for (String key: rankingAnswers.keySet()) {
            createRelationshipToRanking(key, node, (int) rankingAnswers.get(key).getValue());
        }

        HashMap<String, Answer> ratingAnswers = user.getRatingAnswers();
        for (String key: ratingAnswers.keySet()) {
            createRelationshipToRanking(key, node, (int) ratingAnswers.get(key).getValue());
        }
    }

    public void createRelationshipToRanking(String rankingName, Node node, int rank) {
        if (database.findNode(Const.RANKING, rankingName)) {
            Node ranking = database.readNode(Const.RANKING, rankingName);

            createRelationshipForRanking(ranking, node, rank);
        }
    }

    public void createRelationshipToRating(String ratingName, Node node, int rank) {
        if (database.findNode(Const.RATING, ratingName)) {
            Node ranking = database.readNode(Const.RATING, ratingName);

            createRelationshipForRanking(ranking, node, rank);
        }
    }

    public void createRelationshipForRanking(Node survey, Node node, int rank) {
        switch (rank) {
            case 1:
                database.createRelationship(survey, node, Const.RANK_ONE);
                break;
            case 2:
                database.createRelationship(survey, node, Const.RANK_TWO);
                break;
            case 3:
                database.createRelationship(survey, node, Const.RANK_THREE);
                break;
            case 4:
                database.createRelationship(survey, node, Const.RANK_FOUR);
                break;
            case 5:
                database.createRelationship(survey, node, Const.RANK_FIVE);
                break;
            default:
                throw new IllegalArgumentException("ERROR :: There is no RelationshipType for rank:" + rank);
        }
    }

    public void findOrAddNode(Label label, HashMap<String, Object> map) {
        if (database.findNode(label, map.get(Const.ID).toString())) {
            Node node = database.createNode(label, map);

            if (label.name().equals(Survey.SurveyType.RANKING.name())) {
                database.createRelationship(root, node, Const.RELATE_ROOT_RANKING);
            } else if (label.name().equals(Survey.SurveyType.RATING.name())) {
                database.createRelationship(root, node, Const.RELATE_ROOT_RATING);
            }
        }
    }
}
