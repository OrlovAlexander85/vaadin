package ru.globaltruck.vaadin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;

@SpringComponent
@UIScope
public class ExternalSystemEditor extends VerticalLayout implements KeyNotifier {
    private final ExternalSystemRepository externalSystemRepository;

    private ExternalSystem externalSystem;

    private TextField name = new TextField("Name");

    private Button save = new Button("Save", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Cancel");
    private Button delete = new Button("Delete", VaadinIcon.TRASH.create());
    private HorizontalLayout actions = new HorizontalLayout(save, cancel, delete);

    private Binder<ExternalSystem> binder = new Binder<>(ExternalSystem.class);
    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }

    public ExternalSystemEditor(ExternalSystemRepository externalSystemRepository) {
        this.externalSystemRepository = externalSystemRepository;
        add(name, actions);
        binder.bindInstanceFields(this);

        setSpacing(true);

        save.getElement().getThemeList().add("primary");
        delete.getElement().getThemeList().add("error");

        addKeyPressListener(Key.ENTER, e -> save());

        // wire action buttons to save, delete and reset
        save.addClickListener(e -> save());
        delete.addClickListener(e -> delete());
        cancel.addClickListener(e -> editExternalSystem(externalSystem));
        setVisible(false);
    }

    private void delete() {
        externalSystemRepository.delete(externalSystem);
        changeHandler.onChange();
    }

    private void save() {
        externalSystemRepository.save(externalSystem);
        changeHandler.onChange();
    }

    public void editExternalSystem(ExternalSystem editedExternalSystem) {
        if (editedExternalSystem == null) {
            setVisible(false);
            return;
        }

        if (editedExternalSystem.getUuid() != null) {
            externalSystem = externalSystemRepository.findById(editedExternalSystem.getUuid()).orElse(editedExternalSystem);
        }else {
            externalSystem = editedExternalSystem;
        }

        binder.setBean(externalSystem);

        setVisible(true);

        name.focus();
    }
}
