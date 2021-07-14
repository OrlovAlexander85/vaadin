package ru.globaltruck.vaadin;

import lombok.Data;

@Data
public class Node {
    private String name;
    private Node parent;

    public Node(String name, Node parent) {
        this.name = name;
        this.parent = parent;
    }
}
