package database;

import objects.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DbFunctions {
    private static DatabaseUtils database;
    private static Node root;
    private static String dbPath = Const.projectDir + "\\database\\graph.db";

    public DbFunctions() {
        database = DatabaseUtils.getInstance(dbPath);
        root = database.initRoot();
    }

    public DbFunctions(String path) {
        database = DatabaseUtils.getInstance(path);
        root = database.initRoot();
    }

    public Rotarian readRotarian(final String id) {
        return new Rotarian(database.readNodeProperties(Const.ROTARIAN, id));
    }

    public List<Rotarian> readRotarians() {
        final List<Rotarian> rotarians = new ArrayList<>();
        final List<Node> nodes = database.readNodes(Const.ROTARIAN);

        for (Node node: nodes) {
            rotarians.add(new Rotarian(database.readNodeProperties(node)));
        }
        
        return rotarians;
    }

    public void createRotarians(List<User> rotarians) {
        for (User rotarian: rotarians) {
            createRotarian((Rotarian) rotarian);
        }
        System.out.println("Added Rotarians");
    }

    public void createRotarian(Rotarian rotarian) {
        Node node = database.createUser(Const.ROTARIAN, rotarian.getSurveyAnswers());

        database.addLabels(node, new Label[] {
                Const.ROTARIAN,
                Label.label(rotarian.getName().toUpperCase())
        });

        handleRelationships(node, rotarian);

        database.createRelationship(root, node, Const.RELATE_ROOT_ROTARIAN);
        
        matchRotarian(node);
    }

    private void matchRotarian(Node node) {
        // TODO write matching code
    }

    public Rotaractor readRotaractor(final String id) {
        return new Rotaractor(database.readNodeProperties(Const.ROTARACTOR, id));
    }

    public List<Rotaractor> readRotaractors() {
        final List<Rotaractor> rotaractors = new ArrayList<>();
        final List<Node> nodes = database.readNodes(Const.ROTARACTOR);

        for (Node node: nodes) {
            rotaractors.add(new Rotaractor(database.readNodeProperties(node)));
        }

        return rotaractors;
    }

    public List<Rotarian> readPossibleRotarianMatchesForRotaractor(final String id) {
        List<Rotarian> rotarians = new ArrayList<>();

        Node node = database.readNode(Const.ROTARACTOR, id);
        if (node == null) {
            throw new IllegalArgumentException("ERROR :: There is no node for Rotaractor with id of " + id);
        }

        List<Node> nodes = database.readNodesFromRelationshipsOfType(node, Const.POSSIBLE_MATCH);

        for (Node rotarian : nodes) {
            rotarians.add(new Rotarian(database.readNodeProperties(rotarian)));
        }

        return rotarians;
    }

    public void createRotaractors(List<User> rotaractors) {
        for (User rotaractor: rotaractors) {
            createRotaractor((Rotaractor) rotaractor);
        }
        System.out.println("Added Rotaractors");
    }

    public void createRotaractor(Rotaractor rotaractor) {
        Node node = database.createUser(Const.ROTARACTOR, rotaractor.getSurveyAnswers());

        database.addLabels(node, new Label[] {
                Const.ROTARACTOR,
                Label.label(rotaractor.getName().toUpperCase())
        });

        handleRelationships(node, rotaractor);

        database.createRelationship(root, node, Const.RELATE_ROOT_ROTARACTOR);
    }

    private void matchRotaractor(Node node) {
        // TODO write matching code
    }

    public void makeMatch(final String rotarianId, final String rotaractorId) {
        Node rotarian = database.readNode(Const.ROTARIAN, rotarianId);
        if (rotarian == null) {
            throw new IllegalArgumentException("ERROR :: There is no node for Rotarian with id of " + rotarianId);
        }
        Node rotaractor = database.readNode(Const.ROTARACTOR, rotaractorId);
        if (rotaractor == null) {
            throw new IllegalArgumentException("ERROR :: There is no node for Rotaractor with id of " + rotaractorId);
        }

        database.createRelationship(rotarian,
                rotaractor,
                Const.MATCH);
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
