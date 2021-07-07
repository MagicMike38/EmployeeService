package com.mycompany.services;


import com.mycompany.model.Employee;

public interface AbstractEmployeeService {
    boolean createEmployee(Employee employee) throws Exception;
    Employee getEmployee(int employeeId);
    boolean updateEmployee(Employee employee) throws Exception;
    boolean patchEmployee(Employee employee) throws Exception;
    boolean deleteEmployee(int employeeId) throws Exception;
}
