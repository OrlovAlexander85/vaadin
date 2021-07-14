package ru.globaltruck.vaadin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.DataProvider;
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

    private final TreeGrid<JsonNode> treeGrid = new TreeGrid<>();

    public MainView(ExternalSystemRepository repo) throws IOException {
        this.systemRepository = repo;

//        List<ExternalSystem> externalSystems = systemRepository.findAll();
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(Files.readAllBytes(Path.of("src/main/resources/Selta.xml")));
        ObjectMapper jsonMapper = new ObjectMapper();
        String json = jsonMapper.writeValueAsString(node);

//        наименование колонки дерева
        treeGrid.addHierarchyColumn(s -> s).setHeader("изи bitch");
//        try {
        //читаем json
        JSONObject object = new JSONObject(json);
        //заполнение дерева из произвольного json
        List<String> rootItems = Arrays.asList(JSONObject.getNames(object));
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
        treeGrid.setItems();


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
}
