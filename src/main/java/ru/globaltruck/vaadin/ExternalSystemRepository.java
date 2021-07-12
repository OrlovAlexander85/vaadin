package ru.globaltruck.vaadin;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ExternalSystemRepository extends JpaRepository<ExternalSystem, UUID> {

    List<ExternalSystem> findByName(String name);
}
