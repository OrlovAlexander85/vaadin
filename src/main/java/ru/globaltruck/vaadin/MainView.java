package ru.globaltruck.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;

@Route
public class MainView extends VerticalLayout {
    private final ExternalSystemRepository systemRepository;
    private final Grid<ExternalSystem> grid = new Grid<>(ExternalSystem.class);

    private final TextField filter = new TextField("", "Type to filter");
    private final Button addNewBtn = new Button("Add new");
    private final HorizontalLayout toolbar = new HorizontalLayout(filter, addNewBtn);

    private final ExternalSystemEditor editor;

    public MainView(ExternalSystemRepository repo, ExternalSystemEditor editor) {
        this.systemRepository = repo;
        this.editor = editor;
        add(toolbar, grid, editor);

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(e -> listSystems(e.getValue()));

        // Connect selected Customer to editor or hide if none is selected
        grid.asSingleSelect().addValueChangeListener(e -> editor.editExternalSystem(e.getValue()));

        // Instantiate and edit new Customer the new button is clicked
        addNewBtn.addClickListener(e -> editor.editExternalSystem(new ExternalSystem()));

        // Listen changes made by the editor, refresh data from backend
        editor.setChangeHandler(() -> {
            editor.setVisible(false);
            listSystems(filter.getValue());
        });

        listSystems("");
    }

    private void listSystems(String filter) {
        grid.setItems(systemRepository.findAll());

        if (StringUtils.isEmpty(filter)) {
            grid.setItems(systemRepository.findAll());
        }
        else {
            grid.setItems(systemRepository.findByName(filter));
        }

    }



}
