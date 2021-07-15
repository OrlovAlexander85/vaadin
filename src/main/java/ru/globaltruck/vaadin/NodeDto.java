package ru.globaltruck.vaadin;

import lombok.Data;

import java.util.UUID;

@Data
public class NodeDto {
    private UUID uuid;
    private String name;
    private NodeDto parent;

    public NodeDto(String name, NodeDto parent) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.parent = parent;
    }
}
