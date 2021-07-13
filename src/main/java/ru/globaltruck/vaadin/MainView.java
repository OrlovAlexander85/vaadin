package ru.globaltruck.vaadin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Route
public class MainView extends VerticalLayout {
    private final ExternalSystemRepository systemRepository;

    private final Select<String> labelSelect = new Select<>();
    private final Div value = new Div();

    private final TreeGrid<String> treeGrid = new TreeGrid<>();

    public MainView(ExternalSystemRepository repo) throws IOException {
        this.systemRepository = repo;

//        List<ExternalSystem> externalSystems = systemRepository.findAll();
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(Files.readAllBytes(Path.of("src/main/resources/Selta.xml")));
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);

        //наименование колонки дерева
        treeGrid.addHierarchyColumn(s -> s).setHeader("изи bitch");
        try {
            //читаем json
            JSONObject object = new JSONObject(json);
            //заполнение дерева из произвольного json
            List<String> rootItems = Arrays.asList(JSONObject.getNames(object));
            treeGrid.setItems(rootItems, s -> {
                if (object.has(s) && object.get(s) instanceof JSONObject) {
                    JSONObject level1 = (JSONObject) object.get(s);
                    return Arrays.asList(JSONObject.getNames(level1));
                } else
                    return Collections.emptyList();
            });
        } catch (Exception e) {
            throw new RuntimeException("гамно а не json");
        }

        labelSelect.setLabel("Внешняя система");

        value.setText("Выберите внешнюю систему");
        labelSelect.addValueChangeListener(
                event -> value.setText("Выбрано: " + event.getValue()));

        // Добавляет дочерние компоненты
        add(labelSelect, value, treeGrid);

    }
}
