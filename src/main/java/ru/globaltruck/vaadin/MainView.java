package ru.globaltruck.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;
import com.vaadin.flow.router.Route;

import java.util.*;
import java.util.stream.Stream;

@Route
public class MainView extends VerticalLayout {
    private TreeGrid<Node> nodeTreeGrid = new TreeGrid<>();
    private NodeData nodeData = new NodeData();

    private Button openGrid = new Button("Развернуть все");
    private Button closeGrid = new Button("Свернуть все");

    private Button expendFirst = new Button("Развернуть первую");

    public MainView() {

        List<Node> rootNodes = nodeData.getRootDepartments();
        nodeTreeGrid.setItems(rootNodes,
                parent -> nodeData.getChildDepartments(parent));

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



        expendFirst.addClickListener(event -> {
            List<Node> nodeList = Arrays.asList(NodeData.NODE_LIST.get(0));
            nodeTreeGrid.expandRecursively(nodeList, 4);
        });


        add(nodeTreeGrid, openGrid, closeGrid, expendFirst);

    }
}
