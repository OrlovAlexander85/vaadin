package ru.globaltruck.vaadin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class NodeData {


    private static final List<Node> NODE_LIST = createDepartmentList();

    @SneakyThrows
    private static List<Node> createDepartmentList() {
        List<Node> nodeList = new ArrayList<>();

        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(Files.readAllBytes(Path.of("src/main/resources/Selta.xml")));
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);
        JSONObject object = new JSONObject(json);

        Map<String, Object> stringObjectMap = object.toMap();

        for (String key : stringObjectMap.keySet()){
            nodeList.add(new Node(key, null));
            int index = nodeList.size() - 1;
            Object map = stringObjectMap.get(key);
            if (map instanceof HashMap) {
                Map<String, Object> stringObjectMap1 = (Map<String, Object>) map;
                for (String key2 : stringObjectMap1.keySet()){
                    nodeList.add(new Node(key2, nodeList.get(index)));
                }
            }
        }

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
