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
import java.util.*;

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

        /**
         * Получаем корневые Ноды
         */
        List<String> rootNodes = new ArrayList<>();
        node.fields().forEachRemaining(e -> {
            if (node.findParent(e.getKey()) == node) {
                rootNodes.add(e.getKey());
            }
        });
//        наименование колонки дерева
        treeGrid.addHierarchyColumn(s -> s).setHeader("изи bitch");
//        try {
        //читаем json
        JSONObject object = new JSONObject(json);
        //заполнение дерева из произвольного json
//        List<String> rootItems = Arrays.asList(JSONObject.getNames(object));
//            treeGrid.setItems(rootItems, s -> {
////                return test(rootItems, s, object);
//                if (object.has(s) && object.get(s) instanceof JSONObject) {
//                    JSONObject level1 = (JSONObject) object.get(s);
////                    return Arrays.stream(JSONObject.getNames(level1)).map(s1 -> s + "." + s1).collect(Collectors.toList());
////                    return Arrays.asList(JSONObject.getNames(level1));
//                    return Arrays.asList(JSONObject.getNames(level1));
//                } else
//                    return Collections.emptyList();
//            });
//        } catch (Exception e) {
//            throw new RuntimeException("гамно а не json");
//        }
        treeGrid.setItems(rootNodes, s -> {
            return testJsonNode(rootNodes, s, node);

        });


//        TreeGrid<Department> grid = new TreeGrid<>();
//        DepartmentData departmentData = new DepartmentData();
//        grid.setItems(departmentData.getRootDepartments(),
//            departmentData::getChildDepartments);
//        grid.addHierarchyColumn(Department::getName)
//            .setHeader("Department Name");
//        grid.addColumn(Department::getManager).setHeader("Manager");
//
//        add(grid);

        labelSelect.setLabel("Внешняя система");

        value.setText("Выберите внешнюю систему");
        labelSelect.addValueChangeListener(
            event -> value.setText("Выбрано: " + event.getValue()));

        // Добавляет дочерние компоненты
        add(treeGrid);

    }

    private List<String> test(List<String> rootItems, String s, JSONObject object) {
        if (object.has(s) && object.get(s) instanceof JSONObject) {
            JSONObject level1 = (JSONObject) object.get(s);
//                    return Arrays.stream(JSONObject.getNames(level1)).map(s1 -> s + "." + s1).collect(Collectors.toList());
            return test(rootItems, s, level1);
//            return Arrays.asList(JSONObject.getNames(level1));
        } else
            return Collections.emptyList();
    }




    private List<String> testJsonNode(List<String> rootItems, String s, JsonNode node) {
        if (node.has(s) && node.get(s) != null) {
            JsonNode level1 = node.get(s);
//                    return Arrays.stream(JSONObject.getNames(level1)).map(s1 -> s + "." + s1).collect(Collectors.toList());
//            return testJsonNode(rootItems, s, level1);
            node.fields().forEachRemaining(e -> {
                if (node.findParent(e.getKey()) == node) {
                    rootNodes.add(e.getKey());
                }
            });
            return Arrays.asList(level1));
        } else
            return Collections.emptyList();
    }
}
