package ru.globaltruck.vaadin;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;

import java.util.*;

@Route
public class MainView extends VerticalLayout {
    private TreeGrid<Department> departmentTreeGrid = new TreeGrid<>();
    private TreeGrid<Node> nodeTreeGrid = new TreeGrid<>();
    private DepartmentData departmentData = new DepartmentData();
    private NodeData nodeData = new NodeData();

    public MainView() {

        List<Node> rootNodes = nodeData.getRootDepartments();
        nodeTreeGrid.setItems(rootNodes,
                parent -> nodeData.getChildDepartments(parent));

//        List<Department> rootDepartments = departmentData.getRootDepartments();
//        departmentTreeGrid.setItems(rootDepartments,
//                parent -> departmentData.getChildDepartments(parent));

        nodeTreeGrid.addHierarchyColumn(Node::getName)
                .setHeader("Node Name");

//        treeGrid.addHierarchyColumn(Department::getName)
//                .setHeader("Department Name");

        add(nodeTreeGrid);

    }
}
