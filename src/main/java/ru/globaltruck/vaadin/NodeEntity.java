package ru.globaltruck.vaadin;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
public class NodeEntity {
    @Id
    private UUID uuid;
    private String name;
    @OneToOne(cascade = CascadeType.ALL)
    private NodeEntity parent;
}
