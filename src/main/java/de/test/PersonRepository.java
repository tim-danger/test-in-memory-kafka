package de.test;

public interface PersonRepository {
    void savePerson(EmployeeDto employeeDto);
    EmployeeDto findPerson(long id);
    long numberOfAllEmployees();
}
