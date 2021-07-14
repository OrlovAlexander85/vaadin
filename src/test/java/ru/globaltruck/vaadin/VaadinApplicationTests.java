package ru.globaltruck.vaadin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//@SpringBootTest
class VaadinApplicationTests {

    @Test
    void test_node() throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(Files.readAllBytes(Path.of("src/main/resources/Selta.xml")));
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);

        List<Map.Entry<String,JsonNode>> rootNodes = new ArrayList<>();
        node.fields().forEachRemaining(e -> {
            if (node.findParent(e.getKey()) == node){
                rootNodes.add(e);
            }
        });
    }

}
