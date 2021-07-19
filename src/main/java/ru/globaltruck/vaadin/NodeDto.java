package ru.globaltruck.vaadin;

import lombok.Data;

import java.util.UUID;

@Data
public class NodeDto {
    private UUID uuid;
    private String name;
    private NodeDto parent;
    private NodeSettingsDto settings;
    private boolean isLeaf;
    private String source;

    public NodeDto(String name, NodeDto parent, boolean isLeaf, String source) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.parent = parent;
        this.isLeaf = isLeaf;
        this.source = source;
    }
}
