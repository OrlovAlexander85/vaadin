package ru.globaltruck.vaadin;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NodeMapper {
    NodeEntity mapToEntity(NodeDto nodeDto);

    NodeDto mapToDto(NodeEntity nodeEntity);
}
