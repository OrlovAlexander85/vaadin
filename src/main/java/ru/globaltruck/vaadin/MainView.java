package ru.globaltruck.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
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
    private final Button expendFirst = new Button("Развернуть первую");

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
        nodeTreeGrid.setItems(rootNodes,
            parent -> this.nodeData.getChildDepartments(parent, nodeList));

        nodeTreeGrid.addHierarchyColumn(Node::getName)
            .setHeader("Node Name");

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

        nodeTreeGrid.addSelectionListener(e ->
            nodeTreeGrid.getSelectedItems().forEach(item ->
                nodeTreeGrid.getTreeData().getChildren(item).forEach(nodeTreeGrid::select))
        );
        List<Node> nodeSelected = new ArrayList<>();
        nodeTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        nodeTreeGrid.addSelectionListener(selectionEvent -> {
            nodeSelected.clear();
            nodeSelected.add(selectionEvent.getFirstSelectedItem().get());
        });
        expendFirst.addClickListener(event -> {
            nodeTreeGrid.expandRecursively(nodeSelected, 10);
            nodeSelected.clear();
        });

        add(nodeTreeGrid, openGrid, closeGrid, expendFirst);
    }
}
