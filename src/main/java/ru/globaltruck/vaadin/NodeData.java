package ru.globaltruck.vaadin;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NodeData {

    @SneakyThrows
    public void update(List<Node> nodeList, JSONObject jsonObject) {
        Map<String, Object> stringObjectMap = jsonObject.toMap();
        for (String keyParent : stringObjectMap.keySet()) {
            nodeList.add(new Node(keyParent, null));
            addChildrenRecursion(nodeList, stringObjectMap, keyParent);
        }
    }

    private void addChildrenRecursion(List<Node> nodeList, Map<String, Object> objectMap, String keyParent) {
        int parentIndex = nodeList.size() - 1;
        Object childObject = objectMap.get(keyParent);
        if (childObject instanceof HashMap) {
            Map<String, Object> childMap = (Map<String, Object>) childObject;
            for (String childKey : childMap.keySet()) {
                if (!(childMap.get(childKey) instanceof HashMap) && !(childMap.get(childKey) instanceof ArrayList)) {
                    nodeList.add(new Node(childKey + " : " + childMap.get(childKey), nodeList.get(parentIndex)));
                } else {
                    nodeList.add(new Node(childKey, nodeList.get(parentIndex)));
                }
                addChildrenRecursion(nodeList, childMap, childKey);
            }
        } else if (childObject instanceof ArrayList) {
            List<Map<String, Object>> childList = (List<Map<String, Object>>) childObject;
            Map<String, Object> childMap = childList.get(0);
            for (String childKey : childMap.keySet()) {
                if (!(childMap.get(childKey) instanceof HashMap)) {
                    nodeList.add(new Node(childKey + " : " + childMap.get(childKey), nodeList.get(parentIndex)));
                } else
                    nodeList.add(new Node(childKey, nodeList.get(parentIndex)));
                addChildrenRecursion(nodeList, childMap, childKey);
            }
        }
    }

    public List<Node> getRootNods(List<Node> nodeList) {
        return nodeList.stream()
                .filter(node -> node.getParent() == null)
                .collect(Collectors.toList());
    }

    public List<Node> getChildDepartments(Node parent, List<Node> nodeList) {
        return nodeList.stream()
                .filter(node -> Objects.equals(node.getParent(), parent))
                .collect(Collectors.toList());
    }
}
