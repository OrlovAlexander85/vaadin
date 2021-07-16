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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Route
@Slf4j
public class MainView extends VerticalLayout {
    private final NodeService nodeService;
    private final NodeData nodeData;

    private final TreeGrid<NodeDto> nodeTreeGrid = new TreeGrid<>();
    private final Button openGridButton = new Button("Развернуть все");
    private final Button closeGridButton = new Button("Свернуть все");
    private final Button expandSelectedButton = new Button("Развернуть выбранную");
    private final Button saveSettingsButton = new Button("Сохранить");

    private final TextField filterTextField = new TextField();

    public MainView(NodeRepository nodeRepository, NodeService nodeService, NodeData nodeData) {
        this.nodeService = nodeService;
        this.nodeData = nodeData;

        FormLayout settingsFormLayout = new FormLayout();

        List<NodeDto> nodeDtoList = nodeService.findAll();

        List<NodeDto> rootNodeDtos = this.nodeData.getRootNodes(nodeDtoList);

        TreeDataProvider<NodeDto> dataProvider = getNodeDtoTreeDataProvider(nodeData, nodeDtoList, rootNodeDtos);

        // Развернуть все ноды
        expandAllNodesListener();

        // Свернуть все ноды
        rollUpAllNodesListener();

        // Развернуть выбранную ноду
        expandSelectedNodeListener();

        // Окно ввода текста для поиска по дереву
        filterByName(dataProvider);

        // Форма ввода имени
        TextField nameTextField = createTextField(settingsFormLayout);
        settingsFormLayout.addFormItem(nameTextField, "Имя");

        // Чекбокс вкл/выкл
        Checkbox activeCheckbox = new Checkbox();
        settingsFormLayout.addFormItem(activeCheckbox, "Вкл/Выкл");

        // Выпадушка: тип поля
        Select<FieldType> fieldTypeSelect = createFieldTypeSelect(settingsFormLayout);
        settingsFormLayout.addFormItem(fieldTypeSelect, "Тип поля");

        // Кнопка сохранить
        settingsFormLayout.addFormItem(saveSettingsButton, "Сохранить");

        // Слушатель три грида
        nodeTreeGreedListener(nameTextField, activeCheckbox, fieldTypeSelect, settingsFormLayout);

        // Слушатель кнопки сохранить настройки
//        saveSettingsButtonListener(nodeService, nameTextField, activeCheckbox, fieldTypeSelect);

        HorizontalLayout hLayoutTreeAndForm = createHorizontalLayout(settingsFormLayout);

        HorizontalLayout hLayoutWithButtons = new HorizontalLayout();
        hLayoutWithButtons.add(openGridButton, closeGridButton, expandSelectedButton);

        VerticalLayout vLayoutMain = new VerticalLayout();
        vLayoutMain.add(filterTextField, hLayoutTreeAndForm, hLayoutWithButtons);

        add(vLayoutMain);
    }

    private HorizontalLayout createHorizontalLayout(FormLayout settingsFormLayout) {
        HorizontalLayout hLayoutTreeAndForm = new HorizontalLayout();
        hLayoutTreeAndForm.setWidthFull();
        hLayoutTreeAndForm.add(nodeTreeGrid);
        hLayoutTreeAndForm.add(settingsFormLayout);
        return hLayoutTreeAndForm;
    }

    private void nodeTreeGreedListener(TextField nameTextField, Checkbox activeCheckbox, Select<FieldType> fieldTypeSelect, FormLayout settingsFormLayout) {
        nodeTreeGrid.addSelectionListener(selectionEvent -> {
            // Выбрал ноду
            selectionEvent.getFirstSelectedItem().ifPresent(nodeDto -> {

                // Является ли листом
                if (nodeDto.isLeaf()) {
                    // Включить видимость
                    settingsFormLayout.setVisible(true);

                    // Если есть
                    if (nodeDto.getSettings() != null) {
                        NodeSettingsDto nodeSettingsDto = nodeDto.getSettings();
                        nameTextField.setValue(nodeSettingsDto.getHumanReadableName());
                        activeCheckbox.setValue(nodeSettingsDto.isActive());
                        fieldTypeSelect.setValue(nodeSettingsDto.getType());
                    }
                    // Если нету
                    else {
                        nameTextField.clear();
                        activeCheckbox.clear();
                        fieldTypeSelect.clear();
                    }

                    saveSettingsButton.addClickListener(event -> {
                        NodeSettingsDto nodeSettingsDto;

                        if (nodeDto.getSettings() != null) {
                            nodeSettingsDto = nodeDto.getSettings();
                        } else {
                            nodeSettingsDto = new NodeSettingsDto();
                            nodeSettingsDto.setId(UUID.randomUUID());
                        }

                        nodeSettingsDto.setType(fieldTypeSelect.getValue());
                        nodeSettingsDto.setHumanReadableName(nameTextField.getValue());
                        nodeSettingsDto.setActive(activeCheckbox.getValue());
                        nodeDto.setSettings(nodeSettingsDto);
                        nodeService.save(nodeDto);
                        log.info("Saved: " + nodeDto);
                    });
                } else {
                    settingsFormLayout.setVisible(false);
                }
            });
        });
    }

    private Select<FieldType> createFieldTypeSelect(FormLayout settingsFormLayout) {
        Select<FieldType> fieldTypeSelect = new Select<>();
        fieldTypeSelect.setItemLabelGenerator(FieldType::name);
        fieldTypeSelect.setItems(FieldType.values());
        return fieldTypeSelect;
    }

    private TextField createTextField(FormLayout settingsFormLayout) {
        TextField nameTextField = new TextField();
        nameTextField.setPlaceholder("Имя");
        return nameTextField;
    }

    private void filterByName(TreeDataProvider<NodeDto> dataProvider) {
        filterTextField.setPlaceholder("Фильтр по названию");
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER);
        filterTextField.addValueChangeListener(field -> filterDataProvider(field.getValue(), dataProvider));
    }

    private TreeDataProvider<NodeDto> getNodeDtoTreeDataProvider(NodeData
                                                                         nodeData, List<NodeDto> nodeDtoList, List<NodeDto> rootNodeDtos) {
        TreeData<NodeDto> treeData = new TreeData<>();
        treeData.addItems(null, rootNodeDtos);
        nodeDtoList.forEach(nodeDto -> treeData.addItems(nodeDto, nodeData.getChildNodes(nodeDto, nodeDtoList)));
        TreeDataProvider<NodeDto> dataProvider = new TreeDataProvider<>(treeData);
        nodeTreeGrid.setDataProvider(dataProvider);
        nodeTreeGrid.addHierarchyColumn(NodeDto::getName)
                .setHeader("Внешние системы");
        return dataProvider;
    }

    private void expandAllNodesListener() {
        openGridButton.addClickListener(event -> {
            final Stream<NodeDto> rootNodes2 = nodeTreeGrid
                    .getDataProvider()
                    .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.expandRecursively(rootNodes2, 4);
        });
    }

    private void rollUpAllNodesListener() {
        closeGridButton.addClickListener(event -> {
            final Stream<NodeDto> rootNodes2 = nodeTreeGrid
                    .getDataProvider()
                    .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.collapseRecursively(rootNodes2, 4);
        });
    }

    private void expandSelectedNodeListener() {
        List<NodeDto> nodeDtoSelected = new ArrayList<>();
        nodeTreeGrid.setSelectionMode(Grid.SelectionMode.SINGLE);
        nodeTreeGrid.addSelectionListener(selectionEvent -> {
            nodeDtoSelected.clear();
            selectionEvent.getFirstSelectedItem().ifPresent(nodeDtoSelected::add);
        });

        expandSelectedButton.addClickListener(event -> {
            nodeTreeGrid.expandRecursively(nodeDtoSelected, 10);
            nodeDtoSelected.clear();
        });
    }

    private void filterDataProvider(String text, TreeDataProvider<NodeDto> dataProvider) {
        dataProvider.setFilter(nodeDto -> nodeDto.getName().toLowerCase().contains(text.toLowerCase()));
    }
}
