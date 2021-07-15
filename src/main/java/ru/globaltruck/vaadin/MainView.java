package ru.globaltruck.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.json.JSONObject;
import org.json.XML;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Route
public class MainView extends VerticalLayout {

    private final TreeGrid<Node> nodeTreeGrid = new TreeGrid<>();
    private final NodeData nodeData;

    private final Button openGrid = new Button("Развернуть все");
    private final Button closeGrid = new Button("Свернуть все");
    private final Button expandSelected = new Button("Развернуть выбранную");
    private final TextField filter = new TextField();

    public MainView(NodeData nodeData) throws FileNotFoundException {
        this.nodeData = nodeData;

        Reader xmlSourceAtr = new FileReader("src/main/resources/Atrucks.xml");
        Reader xmlSourceSel = new FileReader("src/main/resources/Selta.xml");
        JSONObject objectAtr = XML.toJSONObject(xmlSourceAtr);
        JSONObject objectSel = XML.toJSONObject(xmlSourceSel);

        List<Node> nodeList = new ArrayList<>();
        this.nodeData.update(nodeList, objectAtr);
        this.nodeData.update(nodeList, objectSel);
        List<Node> rootNodes = this.nodeData.getRootNods(nodeList);

        TreeData<Node> treeData = new TreeData<>();
        treeData.addItems(null, rootNodes);
        nodeList.forEach(node -> treeData.addItems(node, nodeData.getChildNodes(node, nodeList)));
        TreeDataProvider<Node> dataProvider = new TreeDataProvider<>(treeData);
        nodeTreeGrid.setDataProvider(dataProvider);
        nodeTreeGrid.addHierarchyColumn(Node::getName)
            .setHeader("Внешние системы");

        // Развернуть все ноды
        openGrid.addClickListener(event -> {
            final Stream<Node> rootNodes2 = nodeTreeGrid
                .getDataProvider()
                .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.expandRecursively(rootNodes2, 4);
        });

        // Свернуть все ноды
        closeGrid.addClickListener(event -> {
            final Stream<Node> rootNodes2 = nodeTreeGrid
                .getDataProvider()
                .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.collapseRecursively(rootNodes2, 4);
        });

        // Развернуть выбранную ноду
        List<Node> nodeSelected = new ArrayList<>();
        nodeTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        nodeTreeGrid.addSelectionListener(selectionEvent -> {
            nodeSelected.clear();
            nodeSelected.add(selectionEvent.getFirstSelectedItem().orElseThrow());
        });

        expandSelected.addClickListener(event -> {
            nodeTreeGrid.expandRecursively(nodeSelected, 10);
            nodeSelected.clear();
        });

        // Окно ввода текста для поиска по дереву
        filter.setPlaceholder("Фильтр по названию");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(field -> filterDataProvider(field.getValue(), dataProvider));

        HorizontalLayout hLayout = new HorizontalLayout();
        hLayout.add(openGrid, closeGrid, expandSelected);
        add(filter, nodeTreeGrid, hLayout);
    }

    private void filterDataProvider(String text, TreeDataProvider<Node> dataProvider) {
        dataProvider.setFilter(node -> node.getName().toLowerCase().contains(text.toLowerCase()));
    }
}
