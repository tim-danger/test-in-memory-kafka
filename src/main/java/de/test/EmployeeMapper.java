package de.test;

import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta")
public interface EmployeeMapper {
    EmployeeDto entityToDto(Employee entity);
    Employee dtoToEntity(EmployeeDto dto);
}
