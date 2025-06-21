package de.test;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_employee")
public class Employee {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "name")
    private String name;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "department")
    private String department;

    @Column(name = "business_unit")
    private String businessUnit;

    @Column(name = "gender")
    private String gender;

    @Column(name = "ethnicity")
    private String ethnicity;

    @Column(name = "age")
    private int age;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "salary")
    private double salary;

    @Column(name = "bonus")
    private double bonus;

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "exit_date")
    private LocalDate exitDate;
}
