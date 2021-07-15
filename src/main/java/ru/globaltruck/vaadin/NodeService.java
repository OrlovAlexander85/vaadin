package ru.globaltruck.vaadin;

import lombok.SneakyThrows;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NodeService {
    private final NodeData nodeData;
    private final NodeMapper nodeMapper;
    private final NodeRepository nodeRepository;

    public NodeService(NodeData nodeData, NodeMapper nodeMapper, NodeRepository nodeRepository) {
        this.nodeData = nodeData;
        this.nodeMapper = nodeMapper;
        this.nodeRepository = nodeRepository;
    }

    @SneakyThrows
    void save(){
        Reader xmlSourceAtr = new FileReader("src/main/resources/Atrucks.xml");
//        Reader xmlSourceSel = new FileReader("src/main/resources/Selta.xml");
        JSONObject objectAtr = XML.toJSONObject(xmlSourceAtr);
//        JSONObject objectSel = XML.toJSONObject(xmlSourceSel);
        List<NodeDto> nodeDtos = nodeData.createNodeList(objectAtr);
        List<NodeEntity> nodeEntities = nodeDtos.stream()
                .map(nodeMapper::mapToEntity)
                .collect(Collectors.toList());
        nodeRepository.saveAll(nodeEntities);
    }

    List<NodeDto> findAll() {
        return nodeRepository.findAll().stream()
                .map(nodeMapper::mapToDto)
                .collect(Collectors.toList());
    }
}
