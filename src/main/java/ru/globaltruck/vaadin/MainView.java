package ru.globaltruck.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {
    private final ExternalSystemRepository systemRepository;

    private final Grid<ExternalSystem> grid = new Grid<>(ExternalSystem.class);

    // Окошко для фильтрации списка
    private final TextField filter = new TextField("", "Type to filter");

    // Кнопка для добавления
    private final Button addNewBtn = new Button("Add new");

    // Панель инструментов, где расположено окошко для фильтра и кнопка добавления новой записи
    private final HorizontalLayout toolbar = new HorizontalLayout(filter, addNewBtn);

    private final ExternalSystemEditor editor;

    public MainView(ExternalSystemRepository repo, ExternalSystemEditor editor) {
        this.systemRepository = repo;
        this.editor = editor;

        // Добавляет дочерние компоненты
        add(toolbar, grid, editor);

        // Позволяет искать сразу, когда начал печатать текст, без нажатия на Enter
        filter.setValueChangeMode(ValueChangeMode.EAGER);


        filter.addValueChangeListener(event -> listSystems(event.getValue()));

        SingleSelect<Grid<ExternalSystem>, ExternalSystem> gridExternalSystemSingleSelect = grid.asSingleSelect();
        gridExternalSystemSingleSelect.addValueChangeListener(event -> editor.editExternalSystem(event.getValue()));

        addNewBtn.addClickListener(event -> editor.editExternalSystem(new ExternalSystem()));

        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listSystems(filter.getValue());
        });

        listSystems("");
    }

    private void listSystems(String filter) {
        if (filter.isEmpty()) {
            grid.setItems(systemRepository.findAll());
        } else {
            grid.setItems(systemRepository.findByNameStartsWith(filter));
        }

    }
}
