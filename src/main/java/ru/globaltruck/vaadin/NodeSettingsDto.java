package ru.globaltruck.vaadin;

import lombok.Data;

import java.util.UUID;

@Data
public class NodeSettingsDto {
    private UUID id;
    private String humanReadableName;
    private FieldType type;
    private boolean active;
}
