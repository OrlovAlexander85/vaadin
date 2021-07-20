package ru.globaltruck.vaadin;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.Reader;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NodeService {
    private final NodeData nodeData;
    private final NodeMapper nodeMapper;
    private final NodeRepository nodeRepository;

    @SneakyThrows
    void save(){
//        Reader xmlSourceAtr = new FileReader("src/main/resources/Atrucks.xml");
        Reader xmlSourceSel = new FileReader("src/main/resources/Selta.xml");
        String source = "selta";
//        JSONObject objectAtr = XML.toJSONObject(xmlSourceAtr);
        JSONObject objectSel = XML.toJSONObject(xmlSourceSel);
        List<NodeDto> nodeDtos = nodeData.createNodeList(objectSel, source);
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

    List<NodeDto> findActiveNodes(String source, boolean active){
        return nodeRepository.findAllBySourceAndSettings_Active(source, active)
                .stream()
                .map(nodeMapper::mapToDto)
                .sorted(Comparator.comparing(NodeDto::getIndex))
                .collect(Collectors.toList());
    }

    public void save(NodeDto nodeDto) {
        NodeEntity nodeEntity = nodeMapper.mapToEntity(nodeDto);
        nodeRepository.save(nodeEntity);
    }

    public void saveAll(List<NodeDto> nodes) {
        nodeRepository.saveAll(nodes
                .stream()
                .map(nodeMapper::mapToEntity)
                .collect(Collectors.toList()));
    }
}
