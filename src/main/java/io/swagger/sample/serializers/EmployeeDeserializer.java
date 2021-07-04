package io.swagger.sample.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.sample.model.Employee;
import org.apache.kafka.common.serialization.Deserializer;

public class EmployeeDeserializer implements Deserializer {
    @Override
    public Employee deserialize(String arg0, byte[] arg1) {
        ObjectMapper mapper = new ObjectMapper();
        Employee employee = null;
        try {
            employee = mapper.readValue(arg1, Employee.class);
        } catch (Exception ex) {
            System.out.println("Error with Deserialization "+ex.getMessage());
        }
        return employee;
    }
}
