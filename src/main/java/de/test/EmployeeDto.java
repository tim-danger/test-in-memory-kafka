package de.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private Long id;
    private String employeeId;
    private String name;
    private String jobTitle;
    private String department;
    private String businessUnit;
    private String gender;
    private String ethnicity;
    private int age;
    private LocalDate hireDate;
    private double salary;
    private double bonus;
    private String country;
    private String city;
    private LocalDate exitDate;
}
