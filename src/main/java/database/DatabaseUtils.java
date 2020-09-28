package database;

import com.google.common.collect.LinkedListMultimap;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.*;

import java.io.File;
import java.util.*;

import static org.neo4j.graphdb.Direction.INCOMING;
import static org.neo4j.graphdb.Direction.OUTGOING;


public class DatabaseUtils {
    private static DatabaseUtils databaseUtils;
    private Node root;
    private Node rotaractor_match;
    private Node rotarian_match;
    private GraphDatabaseService graphDb;

    public static DatabaseUtils getInstance(String dbPath) {
        if (databaseUtils == null) {
            databaseUtils = new DatabaseUtils(dbPath);
        }
        return databaseUtils;
    }

    private DatabaseUtils(String dbPath) {
        this.connectDatabase(dbPath);
    }

    private void connectDatabase(String dbPath) {
        try {
            graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(new File(dbPath));
            registerShutdownHook(graphDb);
        } catch(Exception e){
            System.out.println("ERROR :: " + e.getMessage());
        }
    }

    private static void registerShutdownHook( final GraphDatabaseService graphDb ) {
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }

    public Node init() {
        try (Transaction tx = graphDb.beginTx()) {
            if (graphDb.findNodes(Const.ROOT_LABEL).hasNext()) {
                root = graphDb.findNodes(Const.ROOT_LABEL).next();
            } else {
                root = graphDb.createNode(Const.ROOT_LABEL);
                root.setProperty(Const.ID, "root");
                System.out.println("New root created.");
            }
            if (graphDb.findNodes(Const.ROTARACTOR_MATCH).hasNext()) {
                rotaractor_match = graphDb.findNodes(Const.ROTARACTOR_MATCH).next();
            } else {
                rotaractor_match = graphDb.createNode(Const.ROTARACTOR_MATCH);
                rotaractor_match.setProperty(Const.ID, "rotaractor_match");
                createRelationship(root, rotaractor_match, Const.ROOT_MATCH_VALUES);
                System.out.println("New rotaractor match map node.");
            }
            if (graphDb.findNodes(Const.ROTARIAN_MATCH).hasNext()) {
                rotarian_match = graphDb.findNodes(Const.ROTARIAN_MATCH).next();
            } else {
                rotarian_match = graphDb.createNode(Const.ROTARIAN_MATCH);
                rotarian_match.setProperty(Const.ID, "rotarian_match");
                createRelationship(root, rotarian_match, Const.ROOT_MATCH_VALUES);
                System.out.println("New rotarian match map node");
            }
            tx.success();
            return root;
        } catch (Exception e) {
            System.out.println("Unable to create 'root' node for the graph database. Msg : " + e.getMessage());
        }

        return null;
    }

    public Node init(String uuid) {
        try (Transaction tx = graphDb.beginTx()) {
            if(graphDb.findNodes(Const.ROOT_LABEL).hasNext()){
                root = graphDb.findNodes(Const.ROOT_LABEL).next();
            }
            else {
                root = this.graphDb.createNode(Const.ROOT_LABEL);
                root.setProperty(Const.ID, uuid);
                root.setProperty(Const.NAME, "root");
                System.out.println("New root created.");
            }
            tx.success();
            return root;
        } catch (Exception e) {
            System.out.println("Unable to create 'root' node for the graph database. Msg : " + e.getMessage());
        }

        return null;
    }

    public boolean findNode(Label label, String id) {
        try ( Transaction tx = graphDb.beginTx() ) {
            if(graphDb.findNode(label, Const.ID, id) == null) {
                tx.success();
                tx.close();
                return false;
            }
            tx.success();
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR :: " + e.getMessage());
        }

        return true;
    }

    public Node createNode(Label label, HashMap<String, Object> props) {
        try ( Transaction tx = graphDb.beginTx() ) {
            //Check for duplicates first
            if(graphDb.findNode(label, Const.ID, props.get(Const.ID)) == null){ //If no copies in DB we create new node
                Node node = graphDb.createNode(label);
                for (String key: props.keySet()) {
                    node.setProperty(key, props.get(key));
                }
                if(!node.hasProperty(Const.ID)){ //If no id was passed create ID
                    node.setProperty(Const.ID, UUID.randomUUID().toString());
                }
                tx.success();
                tx.close();
                return  node;
            } else {
                tx.failure();
                throw new IllegalArgumentException("ERROR :: A node already exists in the database for id: " + props.get(Const.ID));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR :: " + e.getMessage());
            return null;
        }
    }

    public Node createParticipant(Label label, HashMap<String, Object> props) {
        try ( Transaction tx = graphDb.beginTx() ) {
            //Check for duplicates first
            if(graphDb.findNode(label, Const.NAME, props.get(Const.NAME)) == null){ //If no copies in DB we create new node
                Node node = createNode(label, props);
                if (node != null) {
                    tx.success();
                    tx.close();
                    return node;
                }
            } else {
                Node node = graphDb.findNode(label, Const.NAME, props.get(Const.NAME));
                updateNode(node, props);
                System.out.println("Updated: " + props.get(Const.ID));
                tx.success();
                tx.close();
                return node;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR :: " + e.getMessage());
        }
        return null;
    }

    public void addRotaractorMatchValue(String nodeId, double value) {
        try ( Transaction tx = graphDb.beginTx() ) {
            tx.acquireWriteLock(rotaractor_match);
            rotaractor_match.setProperty(nodeId, value);
            tx.success();
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR :: " + e.getMessage());
        }
    }

    public HashMap<Double, String> readRotaractorMatchValues() {
        HashMap<Double, String> values = new HashMap<>();
        try ( Transaction tx = graphDb.beginTx() ) {
            tx.acquireReadLock(rotaractor_match);
            Map<String, Object> map = rotaractor_match.getAllProperties();
            map.remove(Const.ID);
            for (String key: map.keySet()) {
                values.put((Double) map.get(key), key);
            }
            tx.success();
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR :: " + e.getMessage());
        }

        return values;
    }

    public void addRotarianMatchValue(String nodeId, double value) {
        try ( Transaction tx = graphDb.beginTx() ) {
            tx.acquireWriteLock(rotarian_match);
            rotarian_match.setProperty(nodeId, value);
            tx.success();
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR :: " + e.getMessage());
        }
    }

    public HashMap<Double, String> readRotarianMatchValues() {
        HashMap<Double, String> values = new HashMap<>();
        try ( Transaction tx = graphDb.beginTx() ) {
            tx.acquireReadLock(rotarian_match);
            Map<String, Object> map = rotarian_match.getAllProperties();
            map.remove(Const.ID);
            for (String key: map.keySet()) {
                values.put((Double) map.get(key), key);
            }
            tx.success();
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR :: " + e.getMessage());
        }

        return values;
    }

    public Node addLabels(Node node, Label[] labels) {
        if (labels == null) {
            return node;
        }

        try ( Transaction tx = graphDb.beginTx() ) {
            tx.acquireWriteLock(node);
            for (int i = 0; i < labels.length; i++) {
                node.addLabel(labels[i]);
            }
            tx.success();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return node;
    }

    public Node readNode(Label label, String id){
        try ( Transaction tx = graphDb.beginTx() ) {
            Node node = graphDb.findNode(label, Const.ID, id);
            tx.acquireReadLock(node);
            if (node != null) {
                tx.success();
                tx.close();
                return  node;
            } else {
                tx.failure();
                throw new NotFoundException("ERROR :: There is no node with id '" + id + "' in the database.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public List<Node> readNodes(Label label) {
        List<Node> nodes = new ArrayList<>();

        try( Transaction tx = graphDb.beginTx()) {
            tx.acquireReadLock(root);
            ResourceIterator<Node> nodeIterator = graphDb.findNodes(label);
            while (nodeIterator.hasNext()) {
                try {
                    Node node = nodeIterator.next();
                    nodes.add(node);
//                    System.out.println("Found " + label.name() + " node with id = " + node.getProperty(Const.ID));
                } catch (Exception e){
                    System.out.println("ERROR :: " + e.getMessage());
                }
            }
            tx.success();
        }
        catch (Exception e)
        {
            System.out.println("An error occurred while getting all " + label + "nodes. Msg :" + e.getMessage());
        }

        return nodes;
    }

    public List<Node> readMatchedNodes(Label primary, boolean matched) {
        List<Node> nodes = new ArrayList<>();

        try( Transaction tx = graphDb.beginTx()) {
            tx.acquireReadLock(root);
            ResourceIterator<Node> nodeIterator = graphDb.findNodes(primary);
            while (nodeIterator.hasNext()) {
                try {
                    Node node = nodeIterator.next();
                    if (node.hasLabel(Const.MATCHED)) {
                        if (matched) {
                            nodes.add(node);
                        }
                    } else {
                        if (!matched) {
                            nodes.add(node);
                        }
                    }
                } catch (Exception e){
                    System.out.println("ERROR :: " + e.getMessage());
                }
            }

            tx.success();
        } catch (Exception e) {
            if (matched) {
                System.out.println("An error occurred while getting all matched " + primary  + "nodes. Msg :" + e.getMessage());
            } else {
                System.out.println("An error occurred while getting all unmatched " + primary  + "nodes. Msg :" + e.getMessage());
            }
        }

        return nodes;
    }

    public void updateNode(Node node, HashMap<String, Object> props){
        try ( Transaction tx = graphDb.beginTx() ) {
            tx.acquireWriteLock(node);
            if(node != null) {
                for (String prop : props.keySet()) {
                    node.setProperty(prop, props.get(prop));
                }
            }
            tx.success();
        } catch(Exception e) {
            System.out.println("Unable to update node. Msg :" + e.getMessage());
        }
    }

    public void deleteNode(Label label, String id) {
        try ( Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.findNode(label, Const.ID, id);
            tx.acquireWriteLock(node);
            if(node != null) {
                if (node.hasRelationship()) {
                    node.getRelationships(OUTGOING).forEach(rel -> {
                        Node otherNode = rel.getOtherNode(node);
                        node.getRelationships(INCOMING).forEach(relation -> relation.delete());
                        rel.delete();
                        if(!otherNode.hasRelationship(INCOMING)){ //So you don't delete another node's child
                            this.deleteNode(otherNode.getLabels().iterator().next(), (String) otherNode.getProperty(Const.ID));
                        }
                    });
                }
                node.delete();
            }
            tx.success();
        } catch (Exception e) {
            System.out.println("Unable to delete node {id :" + id + "}. Msg : " + e.getMessage());
        }
    }

    public void deleteNodes(Label label) {
        try ( Transaction tx = graphDb.beginTx()) {
            ResourceIterator<Node> nodes = (graphDb.findNodes(label));

            while(nodes.hasNext())
            {
                Node current = nodes.next();
                tx.acquireReadLock(current);
                if(current.hasRelationship()){
                    current.getRelationships().forEach(rel -> {
                        if(label != Const.ROOT_LABEL /*|| rel.getType() != Const.RELATE_ROOT_OBSERVATION*/) {
                            rel.delete();
                        }
                    });
                }
//                System.out.println("Deleting node { id: " + current.getProperties(Const.ID) + " , name: " + current.getProperties(Const.NAME ) + "}");
                try {
                    current.delete();
                }
                catch (NotFoundException e){
                    System.out.println(e.getMessage());
                    System.out.println("node " + current.getProperty(Const.ID));
                }
            }
            tx.success();
        } catch(Exception e) {
            System.out.println("Unable to delete all " + label.name() + " nodes. Msg : " +  e.getMessage());
        }
    }

    public void createRelationship(Node startNode, Node endNode, RelationshipType type){
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireWriteLock(startNode);
            tx.acquireWriteLock(endNode);
            Relationship rel = startNode.createRelationshipTo(endNode, type);
            rel.setProperty(Const.ID, UUID.randomUUID().toString());
            tx.success();
        } catch(Exception e) {
            System.out.println("ERROR :: " + e.getMessage());
        }
    }

    public boolean checkOutgoingRelationship(Node node) {
        try(Transaction tx = graphDb.beginTx()) {
            tx.acquireReadLock(node);
            return node.hasRelationship(OUTGOING);
        }
    }

    public boolean checkIncomingRelationship(Node node) {
        try(Transaction tx = graphDb.beginTx()) {
            tx.acquireReadLock(node);
            return node.hasRelationship(INCOMING);
        }
    }

    public Relationship readRelationship(Node startNode, Node endNode, RelationshipType type){
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireWriteLock(startNode);
            tx.acquireWriteLock(endNode);
            HashSet<Relationship> rels = new HashSet<>();
            startNode.getRelationships(type).forEach(rel -> {
                if(rel.getEndNode() == endNode)
                    rels.add(rel);
            });
            if(rels.isEmpty())
                throw new NotFoundException();
            tx.success();
            tx.close();
            return rels.iterator().next();
        } catch(Exception e) {
            System.out.println("ERROR :: " + e.getMessage());
            return null;
        }
    }

    public List<Relationship> readRelationships(Node node){
        List<Relationship> rels = new ArrayList<>();

        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(node);
            node.getRelationships().iterator().forEachRemaining(rel ->{
                try {
//                    System.out.println("Found relationship " + rel.getType().name() + " between " + rel.getStartNode().getLabels().toString()
//                            + " node {id: " + (rel.getStartNode().getProperty(Const.ID)) + " } and " + rel.getEndNode().getLabels().toString()
//                            + " node {id: " + (rel.getEndNode().getProperty(Const.ID)) + " }.");
                    rels.add(rel);
                }
                catch(Exception e){
                    System.out.println("Error. Msg : " + e.getMessage());
                }
                tx.success();
            });
        } catch(Exception e) {
            System.out.println("Unable to get relaionships. Msg:" + e.getMessage());
        }

        return rels;
    }

    public List<Node> readNodesFromRelationshipsOfType(Node node, RelationshipType type){
        List<Node> nodes = new ArrayList<>();

        try (Transaction tx = graphDb.beginTx()) {
            tx.acquireReadLock(node);
            node.getRelationships().iterator().forEachRemaining(rel ->{
                if (rel.isType(type)) {
                    nodes.add(rel.getOtherNode(node));
                }
            });
            tx.success();
        } catch (Exception e) {
            System.out.println("Unable to get relaionships. Msg:" + e.getMessage());
        }

        return nodes;
    }

    // TODO
    public List<Relationship> readAllRelationships(){
        List<Relationship> rels = new ArrayList<>();

        try(Transaction tx = graphDb.beginTx())
        {
            graphDb.getAllRelationships().iterator().forEachRemaining(rel -> {
                try {
//                    System.out.println("Found relationship " + rel.getType().name() + " between " + rel.getStartNode().getLabels().toString()
//                            + " node {id: " + (rel.getStartNode().getProperty(Const.ID)) + " } and " + rel.getEndNode().getLabels().toString()
//                            + " node {id: " + (rel.getEndNode().getProperty(Const.ID)) + " }.");
                    rels.add(rel);
                }
                catch(Exception e){
                    System.out.println("Error. Msg : " + e.getMessage());
                }
            });
            tx.success();
        } catch(Exception e) {
            System.out.println("Unable to get relaionships. Msg:" + e.getMessage());
        }

        return rels;
    }

    // TODO
    public List<Relationship> readAllRelationships(RelationshipType type){
        List<Relationship> rels = new ArrayList<>();

        try(Transaction tx = graphDb.beginTx())
        {
            graphDb.getAllRelationships().iterator().forEachRemaining(rel -> {
                if(rel.getType() == type)
                    rels.add(rel);
            });
            tx.success();
        } catch(Exception e) {
            System.out.println("Unable to get relaionships. Msg:" + e.getMessage());
        }

        return rels;
    }

    // TODO check
    public void deleteRelationship(Relationship rel){
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireWriteLock(rel);
            rel.delete();
            tx.success();
        } catch (Exception e) {
            System.out.println("ERROR :: " + e.getMessage());
        }
    }

    public HashMap<String, Object> readNodeProperties(Label label, String id){
        HashMap<String, Object> nodeMap = new HashMap<>();
        Node node = readNode(label, id);

        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(node);
            nodeMap = (HashMap<String, Object>) node.getAllProperties();
            tx.success();
        } catch (Exception e) {
            System.out.println("Unable to map node properties. Msg : " + e.getMessage());
        }

        return nodeMap;
    }

    public HashMap<String, Object> readNodeProperties(Node node){
        HashMap<String, Object> nodeMap = new HashMap<>();

        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(node);
            nodeMap = (HashMap<String, Object>) node.getAllProperties();
            tx.success();
        } catch (Exception e) {
            System.out.println("Unable to map node properties. Msg : " + e.getMessage());
        }

        return nodeMap;
    }

    public HashMap<String, HashMap<String, Object>> readNodeListProperties(List<Node> nodes){
        HashMap<String, HashMap<String, Object>> nodesMap = new HashMap<>();

        try(Transaction tx = graphDb.beginTx()){
            nodes.iterator().forEachRemaining(node -> {
                try {
                    tx.acquireReadLock(node);
                    nodesMap.put(node.getProperty(Const.ID).toString(), (HashMap<String, Object>) node.getAllProperties());
                } catch (Exception e){
                    System.out.println("Unable to map node properties. Msg : " + e.getMessage());
                }
            });
            tx.success();
        } catch (Exception e) {
            System.out.println("ERROR :: " + e.getMessage());
        }

        return nodesMap;
    }

//    public HashMap<String, Object> readRelationshipProperties(Relationship rel){
//        HashMap<String, Object> relMap = new HashMap<>();
//
//        try(Transaction tx = graphDb.beginTx()){
//            relMap.put(Const.START_NODE, rel.getStartNode().getProperty(Const.ID));
//            relMap.put(Const.END_NODE, rel.getEndNode().getProperty(Const.ID));
//            relMap.put(Const.RELATIONSHIP_TYPE, rel.getType().name());
//            relMap.put(Const.ID, rel.getProperty(Const.ID));
//            tx.success();
//        } catch (Exception e) {
//            System.out.println("Unable to map relationship properties. Msg : " + e.getMessage());
//        }
//
//        return relMap;
//    }
//
//    public HashMap<String, HashMap<String, Object>> readRelationshipListProperties(List<Relationship> rels){
//        HashMap<String, HashMap<String, Object>> relsMap = new HashMap<>();
//
//        try(Transaction tx = graphDb.beginTx()){
//            rels.iterator().forEachRemaining(rel ->{
//                try{
//                    HashMap<String, Object> map = new HashMap<>();
//                    map.put(Const.START_NODE, rel.getStartNode().getProperty(Const.ID));
//                    map.put(Const.END_NODE, rel.getEndNode().getProperty(Const.ID));
//                    map.put(Const.RELATIONSHIP_TYPE, rel.getType().name());
//                    map.put(Const.ID, rel.getProperty(Const.ID));
//                    relsMap.put(Const.START_NODE, map);
//                } catch (Exception e){
//                    System.out.println("Unable to map relationship properties. Msg : " + e.getMessage());
//                }
//            });
//            tx.success();
//        } catch (Exception e) {
//            System.out.println("ERROR :: " +e.getMessage());
//        }
//
//        return relsMap;
//    }

    // TODO
    public List<Label> getLabels() {
        List<Label> labels = new ArrayList<>();
        try(Transaction tx = graphDb.beginTx()) {
            ResourceIterable<Label> labelsIterator = graphDb.getAllLabels();
            for (Label label : labelsIterator) {
                labels.add(label);
            }
            tx.success();
        } catch (Exception e){
            System.out.println("ERROR :: " + e.getMessage());
        }
        return labels;
    }

    // TODO assumptions made here of only one node
    public Label getNodeLabel(Node node){
        Label label;
        try(Transaction tx = graphDb.beginTx()){
            //There should only be one label per node
            label = node.getLabels().iterator().next();
            tx.success();
        }
        return label;
    }

    public String getNodeID(Node node){
        String ID;
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(node);
            ID = node.getProperty(Const.ID).toString();
            tx.success();
        }
        return ID;
    }

    // TODO
    public Node getNodeByID(Object value){
        ResourceIterator<Node> graphNodesIterator = getAllNodesIterator();
        Node currentNode;
        try(Transaction tx = graphDb.beginTx()){
            while(graphNodesIterator.hasNext()) {
                currentNode = graphNodesIterator.next();
                String currentKey = currentNode.getProperty(Const.ID).toString();
                if (currentKey.equals(value)) {
                    return currentNode;
                }
            }
            tx.success();
        }
        return null;
    }

    public void putNodeInGraph(Label label, HashMap<String, Object> properties){
        try(Transaction tx = graphDb.beginTx()){
            Node newNode = graphDb.createNode();
            for(String key: properties.keySet()){
                newNode.setProperty(key, properties.get(key));
            }
            newNode.addLabel(label);
            tx.success();
        }
    }

    public ResourceIterator<Node> getAllNodesIterator(){
        ResourceIterator<Node> allIterableNodes;
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(root);
            ResourceIterable<Node> iterable = graphDb.getAllNodes();
            allIterableNodes = iterable.iterator();
            tx.success();
        }
        return allIterableNodes;
    }

    public ArrayList<String> getAllIDs(){
        ResourceIterator<Node> nodesItr = getAllNodesIterator();
        String currentId;
        ArrayList<String> allIds = new ArrayList<>();
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(root);
            while(nodesItr.hasNext()){
                Node node = nodesItr.next();
                currentId = node.getProperty(Const.ID).toString();
                allIds.add(currentId);
            }
            tx.success();
        }
        return allIds;
    }

    public Iterator<Relationship> getRelationshipIterator(Node node){
        Iterator<Relationship> relsIterator;
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(node);
            Iterable<Relationship> rels = node.getRelationships();
            relsIterator = rels.iterator();
            tx.success();
        }
        return relsIterator;
    }

    public Iterator<String> getPropertyKeysIterator(Node node){
        Iterator<String> keysIterator;
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(node);
            Iterable<String> keys = node.getPropertyKeys();
            keysIterator = keys.iterator();
            tx.success();
        }
        return keysIterator;
    }

    public String getPropertyAsString(Node node, String key){
        String property;
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(node);
            property = node.getProperty(key).toString();
            tx.success();
        }
        return property;
    }

    public void setProperty(Node node, String key, String property){
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireWriteLock(node);
            node.setProperty(key, property);
            tx.success();
        }
    }

    public RelationshipType getRelationshipType(Relationship relationship){
        RelationshipType relType;
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(relationship);
            //TODO: incorporate with constants
            relType = relationship.getType();
            tx.success();
        }
        return relType;
    }

    public Node[] getRelationshipNodes(Relationship relationship){
        Node[] relationshipNodes;
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireWriteLock(relationship);
            relationshipNodes = relationship.getNodes();
            tx.success();
        }
        return relationshipNodes;
    }

    //From https://stackoverflow.com/questions/27233978/java-neo4j-check-if-a-relationship-exist
    public Relationship getRelationshipBetween(Node startNode, String endNodeId){
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireReadLock(startNode);
            for (Relationship rel : startNode.getRelationships()){ // n1.getRelationships(type,direction)
                tx.acquireReadLock(rel);
                String otherNodeId = getNodeID(rel.getOtherNode(startNode));
                if (otherNodeId.equals(endNodeId)) return rel;
            }
            tx.success();
            return null;
        }
    }

    public void createRelationshipBetween(Node node1, Node node2, RelationshipType relType){
        try(Transaction tx = graphDb.beginTx()){
            tx.acquireWriteLock(node1);
            tx.acquireWriteLock(node2);
            Relationship rel = node1.createRelationshipTo(node2, relType);
            //rel.setProperty("T", "test");
            tx.success();
        }
    }

    public void dispose(){
        graphDb.shutdown();
    }
}
