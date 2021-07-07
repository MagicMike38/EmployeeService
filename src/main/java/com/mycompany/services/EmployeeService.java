package com.mycompany.services;


import com.mycompany.model.Employee;
import com.mycompany.dao.EmployeeDAO;

import java.io.IOException;
import java.util.Properties;

public class EmployeeService implements AbstractEmployeeService {
    private final EmployeeDAO employeeDAO;
    public EmployeeService(Properties props) throws IOException {
        employeeDAO = new EmployeeDAO(props);
    }

    public boolean createEmployee(Employee employee) throws Exception {
        if(employee.getId() == 0){
            throw new Exception("Id cannot be null");
        }
        if(employee.getName() == null || employee.getDesignation()==null){
            throw new Exception("Name/Designation both cannot be null");
        }
        return employeeDAO.createEmployee(employee);
    }

    public Employee getEmployee(int empId){
        return employeeDAO.getEmployee(empId);
    }

    public boolean updateEmployee(Employee employee) throws Exception {
        if(employee.getId() == 0){
            throw new Exception("Id cannot be null");
        }
        if(employee.getName() == null || employee.getDesignation()==null){
            throw new Exception("Name/Designation both cannot be null");
        }
        return employeeDAO.updateEmployee(employee);
    }


    public boolean patchEmployee(Employee employee) throws Exception {
        if(employee.getId() == 0){
            throw new Exception("Id cannot be null");
        }
        if(employee.getName() == null && employee.getDesignation()==null){
            throw new Exception("Name and Designation both cannot be null");
        }
        return employeeDAO.updateEmployee(employee);
    }

    public boolean deleteEmployee(int empId) throws Exception {
        if(empId == 0){
            throw new Exception("Id cannot be null");
        }
        return employeeDAO.deleteEmployee(empId);
    }

}
