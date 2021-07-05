package com.mycompany.services;


import com.mycompany.dao.EmployeeDAO;
import com.mycompany.model.Employee;

import java.io.IOException;
import java.util.Properties;

public class EmployeeService implements AbstractEmployeeService {
    private final EmployeeDAO employeeDAO;
    public EmployeeService(Properties props) throws IOException {
        employeeDAO = new EmployeeDAO(props);
    }

    public boolean createEmployee(Employee employee){
        return employeeDAO.createEmployee(employee);
    }

    public Employee getEmployee(int empId){
        return employeeDAO.getEmployee(empId);
    }

    public boolean updateEmployee(Employee employee){
        return employeeDAO.updateEmployee(employee);
    }

    public boolean deleteEmployee(int empId){
        return employeeDAO.deleteEmployee(empId);
    }

}
