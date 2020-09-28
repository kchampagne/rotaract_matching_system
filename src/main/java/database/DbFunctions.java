package database;

import com.google.common.collect.LinkedListMultimap;
import objects.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

import java.util.*;

public class DbFunctions {
    private static DatabaseUtils database;
    private static Node root;
    private static String dbPath = Const.projectDir + "\\database\\graph.db";

    public DbFunctions() {
        database = DatabaseUtils.getInstance(dbPath);
        root = database.init();
    }

    public DbFunctions(String path) {
        database = DatabaseUtils.getInstance(path);
        root = database.init();
    }

    public Rotarian readRotarian(final String id) {
        return new Rotarian(database.readNodeProperties(Const.ROTARIAN, id));
    }

    public List<Rotarian> readRotarians(final boolean matched) {
        final List<Rotarian> rotarians = new ArrayList<>();
        final List<Node> nodes = database.readMatchedNodes(Const.ROTARIAN, matched);

        for (Node node: nodes) {
            rotarians.add(new Rotarian(database.readNodeProperties(node)));
        }
        
        return rotarians;
    }

    public void createRotarians(List<Participant> rotarians) {
        for (Participant rotarian: rotarians) {
            createRotarian((Rotarian) rotarian);
        }
        System.out.println("Added Rotarians");
    }

    public void createRotarian(Rotarian rotarian) {
        Node node = database.createParticipant(Const.ROTARIAN, rotarian.getInfoMap());

        database.addLabels(node, new Label[] {
                Const.ROTARIAN,
                Label.label(rotarian.getName().toUpperCase())
        });

        database.createRelationship(root, node, Const.RELATE_ROOT_ROTARIAN);
        
        matchRotarian(node, rotarian.getId(), rotarian.getMatchValue());

        System.out.println("Added Rotarian: {" +
                rotarian.getName() + "} of {" + rotarian.getClubName() +
                "} based on data from {" + rotarian.getTimestamp() + "}");
    }

    private void matchRotarian(Node rotarian, String id, double matchValue) {
        database.addRotarianMatchValue(id, matchValue);
        HashMap<Double, String> values = database.readRotaractorMatchValues();
        List<String> possibleMatches = findPossibleMatches(values, matchValue);

        for (String possibleMatch: possibleMatches) {
            Node rotaractor = database.readNode(Const.ROTARACTOR, possibleMatch);
            database.createRelationship(rotaractor, rotarian, Const.POSSIBLE_MATCH);
        }
    }

    public Rotaractor readRotaractor(final String id) {
        return new Rotaractor(database.readNodeProperties(Const.ROTARACTOR, id));
    }

    public List<Rotaractor> readRotaractors(final boolean matched) {
        final List<Rotaractor> rotaractors = new ArrayList<>();
        final List<Node> nodes = database.readMatchedNodes(Const.ROTARACTOR, matched);

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

    public void createRotaractors(List<Participant> rotaractors) {
        for (Participant rotaractor: rotaractors) {
            createRotaractor((Rotaractor) rotaractor);
        }
        System.out.println("Added Rotaractors");
    }

    public void createRotaractor(Rotaractor rotaractor) {
        Node node = database.createParticipant(Const.ROTARACTOR, rotaractor.getInfoMap());

        database.addLabels(node, new Label[] {
                Const.ROTARACTOR,
                Label.label(rotaractor.getName().toUpperCase())
        });

        database.createRelationship(root, node, Const.RELATE_ROOT_ROTARACTOR);

        matchRotaractor(node, rotaractor.getId(), rotaractor.getMatchValue());

        System.out.println("Added Rotaractor: {" +
                rotaractor.getName() + "} of {" + rotaractor.getClubName() +
                "} based on data from {" + rotaractor.getTimestamp() + "}");
    }

    private void matchRotaractor(Node rotaractor, String id, double matchValue) {
        database.addRotaractorMatchValue(id, matchValue);
        HashMap<Double, String> values = database.readRotarianMatchValues();
        List<String> possibleMatches = findPossibleMatches(values, matchValue);

        for (String possibleMatch: possibleMatches) {
            Node rotarian = database.readNode(Const.ROTARIAN, possibleMatch);
            database.createRelationship(rotaractor, rotarian, Const.POSSIBLE_MATCH);
        }
    }

    public void makeMatch(final String rotarianId, final String rotaractorId) {
        final Label[] labels = new Label[] {Const.MATCHED};
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
        database.addLabels(rotarian, labels);
        database.addLabels(rotaractor, labels);
    }

    private List<String> findPossibleMatches(HashMap<Double, String> values, double matchValue) {
        List<String> possibleMatches = new ArrayList<>();
        Set<Double> keys = values.keySet();
        List<Double> list = new ArrayList(keys);
        List<Double> distances = new ArrayList<>();
        List<Double> distancesClone = new ArrayList<>();

        for (int index = 0; index < list.size(); index++) {
            double distance = getDistanceBetween(matchValue, list.get(index));
            distances.add(distance);
            distancesClone.add(distance);
        }

        Collections.sort(distances, new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return o1.compareTo(o2);
            }
        });

        int K;
        if (list.size() <= 3) {
            K = list.size();
        } else {
            K = 3;
        }

        List<Double> shortestDistances = distances.subList(0 , K);
        for (double element : shortestDistances) {
            Integer indexOnClone = distancesClone.indexOf(element);
            possibleMatches.add(values.get(list.get(indexOnClone)));
        }

        return possibleMatches;
    }

    private double getDistanceBetween(double x1, double x2) {
//        long x1 = Math.round(dataElement1);
//        long x2 = Math.round(dataElement2);
        double term = (x2 - x1) * (x2 - x1);
        double distance = Math.abs(Math.sqrt(term));
        String convertedDistance = Double.toString(distance);
        return Double.parseDouble(convertedDistance);
    }
}
