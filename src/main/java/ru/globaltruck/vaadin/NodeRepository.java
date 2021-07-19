package ru.globaltruck.vaadin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface NodeRepository extends JpaRepository<NodeEntity, UUID> {
    List<NodeEntity> findAllBySourceAndSettings_Active(String source, boolean active);
}
