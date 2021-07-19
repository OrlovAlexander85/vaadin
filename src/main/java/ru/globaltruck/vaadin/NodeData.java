package ru.globaltruck.vaadin;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NodeData {

    @SneakyThrows
    public List<NodeDto> createNodeList(JSONObject jsonObject, String source) {
        List<NodeDto> nodeDtoList = new ArrayList<>();

        Map<String, Object> stringObjectMap = jsonObject.toMap();
        for (String keyParent : stringObjectMap.keySet()) {
            nodeDtoList.add(new NodeDto(keyParent, null, false, source));
            addChildrenRecursion(nodeDtoList, stringObjectMap, keyParent, source);
        }

        return nodeDtoList;
    }

    private void addChildrenRecursion(List<NodeDto> nodeDtoList, Map<String, Object> objectMap, String keyParent, String source) {
        int parentIndex = nodeDtoList.size() - 1;
        Object childObject = objectMap.get(keyParent);
        if (childObject instanceof HashMap) {
            Map<String, Object> childMap = (Map<String, Object>) childObject;
            for (String childKey : childMap.keySet()) {
                if (!(childMap.get(childKey) instanceof HashMap) && !(childMap.get(childKey) instanceof ArrayList)) {
                    nodeDtoList.add(new NodeDto(getName(childMap, childKey), nodeDtoList.get(parentIndex), true, source));
                } else {
                    nodeDtoList.add(new NodeDto(childKey, nodeDtoList.get(parentIndex),false, source));
                }
                addChildrenRecursion(nodeDtoList, childMap, childKey, source);
            }
        } else if (childObject instanceof ArrayList) {
            List<Map<String, Object>> childList = (List<Map<String, Object>>) childObject;
            Map<String, Object> childMap = childList.get(0);
            for (String childKey : childMap.keySet()) {
                if (!(childMap.get(childKey) instanceof HashMap)) {
                    nodeDtoList.add(new NodeDto(getName(childMap, childKey), nodeDtoList.get(parentIndex), true, source));
                } else
                    nodeDtoList.add(new NodeDto(childKey, nodeDtoList.get(parentIndex),false, source));
                addChildrenRecursion(nodeDtoList, childMap, childKey, source);
            }
        }
    }

    private String getName(Map<String, Object> childMap, String childKey) {
        String childValue = childMap.get(childKey).toString();
        return childKey + " : " + (childValue.length() > 30 ? childValue.substring(0, 30) : childValue);
    }

    public List<NodeDto> getRootNodes(List<NodeDto> nodeDtoList) {
        return nodeDtoList.stream()
                .filter(nodeDto -> nodeDto.getParent() == null)
                .collect(Collectors.toList());
    }

    public List<NodeDto> getChildNodes(NodeDto parent, List<NodeDto> nodeDtoList) {
        return nodeDtoList.stream()
                .filter(nodeDto -> Objects.equals(nodeDto.getParent(), parent))
                .collect(Collectors.toList());
    }
}
