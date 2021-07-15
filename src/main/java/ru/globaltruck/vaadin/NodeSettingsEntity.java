package ru.globaltruck.vaadin;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Data
public class NodeSettingsEntity {
    @Id
    private UUID id;
    private String humanReadableName;
    private FieldType type;
    private boolean active;
}
