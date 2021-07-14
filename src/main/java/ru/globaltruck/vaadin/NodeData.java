package ru.globaltruck.vaadin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class NodeData {


    private static final List<Node> NODE_LIST = createDepartmentList();

    private static List<Node> createDepartmentList() {
        List<Node> nodeList = new ArrayList<>();

//        XmlMapper xmlMapper = new XmlMapper();
//        JsonNode node = xmlMapper.readTree(Files.readAllBytes(Path.of("src/main/resources/Selta.xml")));
//        ObjectMapper jsonMapper = new ObjectMapper();
//        String json = jsonMapper.writeValueAsString(node);
//        JSONObject object = new JSONObject(json);

        nodeList.add(
                new Node("Selta", null));
        nodeList.add(
                new Node("Груз", nodeList.get(0)));
        nodeList.add(
                new Node("Цена", nodeList.get(0)));
        nodeList.add(
                new Node("Вес", nodeList.get(1)));

        return nodeList;
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
