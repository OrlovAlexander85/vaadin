package ru.globaltruck.vaadin;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.UUID;

@Getter
@Setter
@Entity
public class NodeEntity {
    @Id
    private UUID uuid;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    private NodeEntity parent;
    @OneToOne(cascade = CascadeType.ALL)
    private NodeSettingsEntity settings;
    private boolean isLeaf;
    private String source;
    private String example;
}
