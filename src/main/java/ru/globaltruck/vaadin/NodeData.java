package ru.globaltruck.vaadin;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.json.XML;

import java.io.FileReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class NodeData {


    public static final List<Node> NODE_LIST = createNodeList();

    @SneakyThrows
    private static List<Node> createNodeList() {
        List<Node> nodeList = new ArrayList<>();

//        Reader xmlSource = new FileReader("src/main/resources/Selta.xml");
        Reader xmlSource = new FileReader("src/main/resources/Atrucks-orders.xml");
        JSONObject object = XML.toJSONObject(xmlSource);

        Map<String, Object> stringObjectMap = object.toMap();

        for (String keyParent : stringObjectMap.keySet()) {
            nodeList.add(new Node(keyParent, null));
            addChildrenRecursion(nodeList, stringObjectMap, keyParent);
        }

        return nodeList;
    }

    private static void addChildrenRecursion(List<Node> nodeList, Map<String, Object> objectMap, String keyParent) {
        int parentIndex = nodeList.size() - 1;
        Object childObject = objectMap.get(keyParent);
        if (childObject instanceof HashMap) {
            Map<String, Object> childMap = (Map<String, Object>) childObject;
            for (String childKey : childMap.keySet()) {
                nodeList.add(new Node(childKey, nodeList.get(parentIndex)));
                addChildrenRecursion(nodeList, childMap, childKey);
            }
        } else if (childObject instanceof ArrayList) {
            List<Map<String, Object>> childList = (List<Map<String, Object>>) childObject;
            Map<String, Object> childMap = childList.get(0);
            for (String childKey : childMap.keySet()) {
                nodeList.add(new Node(childKey, nodeList.get(parentIndex)));
                addChildrenRecursion(nodeList, childMap, childKey);
            }
        }
    }

    public List<Node> getRootDepartments() {
        return NODE_LIST.stream()
                .filter(node -> node.getParent() == null)
                .collect(Collectors.toList());
    }

    public List<Node> getChildDepartments(Node parent) {
        return NODE_LIST.stream()
                .filter(node -> Objects.equals(node.getParent(), parent))
                .collect(Collectors.toList());
    }
}
