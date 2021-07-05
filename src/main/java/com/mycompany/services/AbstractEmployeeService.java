package com.mycompany.services;


import com.mycompany.model.Employee;

public interface AbstractEmployeeService {
    boolean createEmployee(Employee employee);
    Employee getEmployee(int employeeId);
    boolean updateEmployee(Employee employee);
    boolean deleteEmployee(int employeeId);
}
