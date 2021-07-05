package io.swagger.sample.services;


import io.swagger.sample.model.Employee;

public interface AbstractEmployeeService {
    boolean createEmployee(Employee employee);
    Employee getEmployee(int employeeId);
    boolean updateEmployee(Employee employee);
    boolean deleteEmployee(int employeeId);
}
