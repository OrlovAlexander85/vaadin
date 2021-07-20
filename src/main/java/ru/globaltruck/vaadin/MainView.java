package ru.globaltruck.vaadin;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Route
@Slf4j
public class MainView extends VerticalLayout {
    private final NodeService nodeService;

    private final TextField filterTextField = new TextField();

    private NodeDto draggedNode;
    private List<NodeDto> seltaNodes;
    private Grid<NodeDto> grid;

    public MainView(NodeService nodeService, NodeData nodeData) {
        this.nodeService = nodeService;



        TreeGrid<NodeDto> nodeTreeGrid = new TreeGrid<>();

        List<NodeDto> nodeList = nodeService.findAll();

        List<NodeDto> rootNodes = nodeData.getRootNodes(nodeList);

        TreeDataProvider<NodeDto> dataProvider = getNodeDtoTreeDataProvider(nodeData, nodeList, rootNodes, nodeTreeGrid);

        Button openGridButton = new Button("Развернуть все");
        Button closeGridButton = new Button("Свернуть все");
        Button expandSelectedButton = new Button("Развернуть выбранную");

        // Развернуть все ноды
        expandAllNodesListener(nodeTreeGrid, openGridButton);

        // Свернуть все ноды
        rollUpAllNodesListener(nodeTreeGrid, closeGridButton);

        // Развернуть выбранную ноду
        expandSelectedNodeListener(nodeTreeGrid, expandSelectedButton);

        // Окно ввода текста для поиска по дереву
        filterByName(dataProvider);

        // Слой настроек
        VerticalLayout settingsFormLayout = createSettingsFormLayout(nodeTreeGrid);

        // Общий слой для настроек и дерева
        HorizontalLayout hLayoutTreeAndForm = createTreeAndFormLayout(settingsFormLayout, nodeTreeGrid);

        // Слой для кнопок дерева
        HorizontalLayout hLayoutWithButtons = new HorizontalLayout();
        hLayoutWithButtons.add(openGridButton, closeGridButton, expandSelectedButton);

        // Создание таблицы включенных полей
        createGrid(nodeService);

        // Кнопка для сохранения порядка настроек
        Button saveOptionsList = getSaveOptionsOrderButton(nodeService);

        // Общий слой
        VerticalLayout vLayoutMain = new VerticalLayout();
        vLayoutMain.add(filterTextField, hLayoutTreeAndForm, hLayoutWithButtons, grid, saveOptionsList);

        add(vLayoutMain);
    }

    private void createGrid(NodeService nodeService) {
        grid = new Grid<>(NodeDto.class);
        initializeGrid(nodeService);
        grid.setColumns("name", "example");
        grid.setSortableColumns();
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setRowsDraggable(true);

        grid.addDragStartListener(event -> {
            draggedNode = event.getDraggedItems().get(0);
            grid.setDropMode(GridDropMode.BETWEEN);
        });

        grid.addDragEndListener(
                event -> {
                    draggedNode = null;
                    // После завершения перетаскивания отключите режим перетаскивания, чтобы
                    // не будет похоже, что другие перетаскиваемые предметы можно уронить
                    grid.setDropMode(null);
                }
        );

        grid.addDropListener(
                event -> {
                    Optional<NodeDto> dropOverItem = event.getDropTargetItem();
                    if (dropOverItem.isPresent() && !dropOverItem.get().equals(draggedNode)) {
                        // переупорядочить перетаскиваемый элемент в контейнере backing gridItems
                        seltaNodes.remove(draggedNode);
                        // рассчитать индекс выпадения на основе dropOverItem
                        int dropIndex =
                                seltaNodes.indexOf(dropOverItem.get()) + (event.getDropLocation() == GridDropLocation.BELOW ? 1 : 0);
                        seltaNodes.add(dropIndex, draggedNode);

                        grid.getDataProvider().refreshAll();
                    }
                }
        );
    }

    private Button getSaveOptionsOrderButton(NodeService nodeService) {
        Button saveOptionsList = new Button("Сохранить порядок");
        Dialog dialog = new Dialog();
        dialog.add(new Text("Порядок сохранён"));
        saveOptionsList.addClickListener(event -> {
            for (int i = 0; i < seltaNodes.size(); i++) {
                NodeDto nodeDto = seltaNodes.get(i);
                nodeDto.setIndex(i);
            }
            nodeService.saveAll(seltaNodes);
            dialog.open();
        });

        return saveOptionsList;
    }

    private void initializeGrid(NodeService nodeService) {
        seltaNodes = nodeService.findActiveNodes("selta", true);
        grid.setItems(seltaNodes);
    }

    private VerticalLayout createSettingsFormLayout(TreeGrid<NodeDto> nodeTreeGrid) {
        // Форма ввода имени
        TextField nameTextField = createTextField();

        // Чекбокс вкл/выкл
        Checkbox activeCheckbox = new Checkbox("Вкл/выкл");

        // Выпадушка: тип поля
        Select<FieldType> fieldTypeSelect = createFieldTypeSelect();

        Button saveSettingsButton = new Button("Сохранить");

        VerticalLayout settingsFormLayout = new VerticalLayout(nameTextField, activeCheckbox, fieldTypeSelect, saveSettingsButton);

        // Слушатель три грида
        nodeTreeGreedListener(nameTextField, activeCheckbox, fieldTypeSelect, settingsFormLayout, nodeTreeGrid, saveSettingsButton);
        return settingsFormLayout;
    }

    private HorizontalLayout createTreeAndFormLayout(VerticalLayout settingsFormLayout, TreeGrid<NodeDto> nodeTreeGrid) {
        HorizontalLayout mainContent = new HorizontalLayout(nodeTreeGrid, settingsFormLayout);
        mainContent.setWidthFull();
        nodeTreeGrid.setWidthFull();
        return mainContent;
    }

    private void nodeTreeGreedListener(TextField nameTextField,
                                       Checkbox activeCheckbox,
                                       Select<FieldType> fieldTypeSelect,
                                       VerticalLayout settingsFormLayout,
                                       TreeGrid<NodeDto> nodeTreeGrid,
                                       Button saveSettingsButton) {
        var ref = new Object() {
            NodeDto nodeDtoSelected;
        };

        nodeTreeGrid.addSelectionListener(selectionEvent -> {
            // Выбрал ноду
            selectionEvent.getFirstSelectedItem().ifPresent(nodeDto -> {

                ref.nodeDtoSelected = nodeDto;

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
                } else {
                    settingsFormLayout.setVisible(false);
                }
            });
        });

        Dialog dialog = new Dialog();
        dialog.add(new Text("Настройки сохранены"));

        saveSettingsButton.addClickListener(event -> {
            NodeSettingsDto nodeSettingsDto;

            if (ref.nodeDtoSelected.getSettings() != null) {
                nodeSettingsDto = ref.nodeDtoSelected.getSettings();
            } else {
                nodeSettingsDto = new NodeSettingsDto();
                nodeSettingsDto.setId(UUID.randomUUID());
            }

            nodeSettingsDto.setType(fieldTypeSelect.getValue());
            nodeSettingsDto.setHumanReadableName(nameTextField.getValue());
            nodeSettingsDto.setActive(activeCheckbox.getValue());
            ref.nodeDtoSelected.setSettings(nodeSettingsDto);
            nodeService.save(ref.nodeDtoSelected);
            log.info("Saved: " + ref.nodeDtoSelected);
            dialog.open();
            initializeGrid(nodeService);
        });
    }

    private Select<FieldType> createFieldTypeSelect() {
        Select<FieldType> fieldTypeSelect = new Select<>();
        fieldTypeSelect.setLabel("Тип поля");
        fieldTypeSelect.setItemLabelGenerator(FieldType::name);
        fieldTypeSelect.setItems(FieldType.values());
        return fieldTypeSelect;
    }

    private TextField createTextField() {
        TextField nameTextField = new TextField("Имя");
        nameTextField.setPlaceholder("Название поля");
        return nameTextField;
    }

    private void filterByName(TreeDataProvider<NodeDto> dataProvider) {
        filterTextField.setPlaceholder("Фильтр по названию");
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER);
        filterTextField.addValueChangeListener(field -> filterDataProvider(field.getValue(), dataProvider));
    }

    private TreeDataProvider<NodeDto> getNodeDtoTreeDataProvider(NodeData nodeData, List<NodeDto> nodeDtoList,
                                                                 List<NodeDto> rootNodes, TreeGrid<NodeDto> nodeTreeGrid) {
        TreeData<NodeDto> treeData = new TreeData<>();
        treeData.addItems(null, rootNodes);
        nodeDtoList.forEach(nodeDto -> treeData.addItems(nodeDto, nodeData.getChildNodes(nodeDto, nodeDtoList)));
        TreeDataProvider<NodeDto> dataProvider = new TreeDataProvider<>(treeData);
        nodeTreeGrid.setDataProvider(dataProvider);
        nodeTreeGrid.addHierarchyColumn(nodeDto ->
                nodeDto.getName() + (nodeDto.getExample() == null ? "" : " : " + nodeDto.getExample())
        )
                .setHeader("Внешние системы");
        return dataProvider;
    }

    private void expandAllNodesListener(TreeGrid<NodeDto> nodeTreeGrid, Button openGridButton) {
        openGridButton.addClickListener(event -> {
            final Stream<NodeDto> rootNodes2 = nodeTreeGrid
                    .getDataProvider()
                    .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.expandRecursively(rootNodes2, 4);
        });
    }

    private void rollUpAllNodesListener(TreeGrid<NodeDto> nodeTreeGrid, Button closeGridButton) {
        closeGridButton.addClickListener(event -> {
            final Stream<NodeDto> rootNodes2 = nodeTreeGrid
                    .getDataProvider()
                    .fetchChildren(new HierarchicalQuery<>(null, null));
            nodeTreeGrid.collapseRecursively(rootNodes2, 4);
        });
    }

    private void expandSelectedNodeListener(TreeGrid<NodeDto> nodeTreeGrid, Button expandSelectedButton) {
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
