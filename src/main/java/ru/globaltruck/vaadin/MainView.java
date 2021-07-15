package ru.globaltruck.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
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
    private final Button openGridButton = new Button("Развернуть все");
    private final Button closeGridButton = new Button("Свернуть все");
    private final Button expandSelectedButton = new Button("Развернуть выбранную");
    private final Button saveSettingsButton = new Button("Сохранить");

    private final TextField filterTextField = new TextField();

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
        openGridButton.addClickListener(event -> {
            final Stream<NodeDto> rootNodes2 = nodeTreeGrid
                .getDataProvider()
                .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.expandRecursively(rootNodes2, 4);
        });

        // Свернуть все ноды
        closeGridButton.addClickListener(event -> {
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

        expandSelectedButton.addClickListener(event -> {
            nodeTreeGrid.expandRecursively(nodeDtoSelected, 10);
            nodeDtoSelected.clear();
        });

        // Окно ввода текста для поиска по дереву
        filterTextField.setPlaceholder("Фильтр по названию");
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER);
        filterTextField.addValueChangeListener(field -> filterDataProvider(field.getValue(), dataProvider));

        // Форма с настройками

        TextField nameTextField = new TextField();
        nameTextField.setPlaceholder("Имя");

        Checkbox activeCheckbox = new Checkbox();

        Select<FieldType> fieldTypeSelect = new Select<>();
        fieldTypeSelect.setItemLabelGenerator(FieldType::name);
        fieldTypeSelect.setItems(FieldType.values());

        settingsFormLayout.addFormItem(nameTextField, "Имя");
        settingsFormLayout.addFormItem(fieldTypeSelect, "Тип поля");
        settingsFormLayout.addFormItem(activeCheckbox, "Вкл/Выкл");
        saveSettingsButton.addClickListener(event -> {
            NodeSettingsDto settingsDto = new NodeSettingsDto();
            settingsDto.setType(fieldTypeSelect.getValue());
            settingsDto.setHumanReadableName(nameTextField.getValue());
            settingsDto.setActive(activeCheckbox.getValue());
            nodeService.saveSettings();
        });

        settingsFormLayout.add(saveSettingsButton);

        VerticalLayout vLayoutMain = new VerticalLayout();
        HorizontalLayout hLayoutTreeAndForm = new HorizontalLayout();
        VerticalLayout vLayoutForm = new VerticalLayout();
        vLayoutForm.add(settingsFormLayout);
        HorizontalLayout hLayoutWithButtons = new HorizontalLayout();
        hLayoutWithButtons.add(openGridButton, closeGridButton, expandSelectedButton);
        nodeTreeGrid.setWidth("100%");
        hLayoutTreeAndForm.add(nodeTreeGrid, vLayoutForm);
        vLayoutMain.add(filterTextField, hLayoutTreeAndForm, hLayoutWithButtons);
        add(vLayoutMain);
    }

    private void filterDataProvider(String text, TreeDataProvider<NodeDto> dataProvider) {
        dataProvider.setFilter(nodeDto -> nodeDto.getName().toLowerCase().contains(text.toLowerCase()));
    }
}
