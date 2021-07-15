package ru.globaltruck.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Route
public class MainView extends VerticalLayout {
    private final NodeService nodeService;

    private final TreeGrid<NodeDto> nodeTreeGrid = new TreeGrid<>();
    private final NodeData nodeData;

    private final FormLayout settingsFormLayout = new FormLayout();
    private final Button openGrid = new Button("Развернуть все");
    private final Button closeGrid = new Button("Свернуть все");
    private final Button expandSelected = new Button("Развернуть выбранную");
    private final TextField filter = new TextField();

    public MainView(NodeRepository nodeRepository, NodeService nodeService, NodeData nodeData) {
        this.nodeService = nodeService;
        this.nodeData = nodeData;

        List<NodeDto> nodeDtoList = nodeService.findAll();

        List<NodeDto> rootNodeDtos = this.nodeData.getRootNods(nodeDtoList);

        TreeData<NodeDto> treeData = new TreeData<>();
        treeData.addItems(null, rootNodeDtos);
        nodeDtoList.forEach(nodeDto -> treeData.addItems(nodeDto, nodeData.getChildNodes(nodeDto, nodeDtoList)));
        TreeDataProvider<NodeDto> dataProvider = new TreeDataProvider<>(treeData);
        nodeTreeGrid.setDataProvider(dataProvider);
        nodeTreeGrid.addHierarchyColumn(NodeDto::getName)
            .setHeader("Внешние системы");

        // Развернуть все ноды
        openGrid.addClickListener(event -> {
            final Stream<NodeDto> rootNodes2 = nodeTreeGrid
                .getDataProvider()
                .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.expandRecursively(rootNodes2, 4);
        });

        // Свернуть все ноды
        closeGrid.addClickListener(event -> {
            final Stream<NodeDto> rootNodes2 = nodeTreeGrid
                .getDataProvider()
                .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.collapseRecursively(rootNodes2, 4);
        });

        // Развернуть выбранную ноду
        List<NodeDto> nodeDtoSelected = new ArrayList<>();
        nodeTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        nodeTreeGrid.addSelectionListener(selectionEvent -> {
            nodeDtoSelected.clear();
            nodeDtoSelected.add(selectionEvent.getFirstSelectedItem().orElseThrow());
        });

        expandSelected.addClickListener(event -> {
            nodeTreeGrid.expandRecursively(nodeDtoSelected, 10);
            nodeDtoSelected.clear();
        });

        // Окно ввода текста для поиска по дереву
        filter.setPlaceholder("Фильтр по названию");
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(field -> filterDataProvider(field.getValue(), dataProvider));

        // Форма с настройками

        TextField firstName = new TextField();
        firstName.setPlaceholder("John");

        TextField email = new TextField();
        Checkbox doNotCall = new Checkbox("Do not call");

        settingsFormLayout.addFormItem(firstName, "First name");
        settingsFormLayout.addFormItem(email, "E-mail");
//        FormLayout.FormItem phoneItem = settingsFormLayout.addFormItem(phone, "Phone");
//        phoneItem.add(doNotCall);

        VerticalLayout vLayoutMain = new VerticalLayout();
        HorizontalLayout hLayoutTreeAndForm = new HorizontalLayout();
        HorizontalLayout hLayoutWithButtons = new HorizontalLayout();
        hLayoutWithButtons.add(openGrid, closeGrid, expandSelected);
//        hLayoutTreeAndForm.add(nodeTreeGrid);
        vLayoutMain.add(filter, nodeTreeGrid, hLayoutWithButtons);
        add(vLayoutMain);
    }

    private void filterDataProvider(String text, TreeDataProvider<NodeDto> dataProvider) {
        dataProvider.setFilter(nodeDto -> nodeDto.getName().toLowerCase().contains(text.toLowerCase()));
    }
}
