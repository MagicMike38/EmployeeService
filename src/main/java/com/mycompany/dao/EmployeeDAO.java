package com.mycompany.dao;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mycompany.model.Employee;
import org.bson.Document;

import java.util.Properties;

public class EmployeeDAO {

    private final MongoClient client;
    private final MongoCollection mongoCollection;

    public EmployeeDAO(Properties props) {
        String hostName = props.getProperty("MONGO_HOST_NAME");
        int port = Integer.parseInt(props.getProperty("MONGO_PORT"));
        String db = props.getProperty("MONGO_DB");

        client = new MongoClient(hostName, port);
        mongoCollection = client.getDatabase(db).getCollection("employees");
    }

    public boolean createEmployee(Employee employee){
        Document document = new Document();
        document.append("_id", employee.getId());
        document.append("employeeName", employee.getName());
        document.append("employeeDesignation", employee.getDesignation());

        mongoCollection.insertOne(document);
        return true;
    }

    public Employee getEmployee(int employeeId){
        FindIterable<Document> findIterable = mongoCollection.find(Filters.eq("_id", employeeId));
        for(Document doc : findIterable) {
            return new Employee(doc.getInteger("_id"),
                    doc.getString("employeeName"),
                    doc.getString("employeeDesignation"));
        }
        return null;
    }

    public boolean updateEmployee(Employee employee){
        Document document = new Document();

        if(employee.getName() != null){
            document.append("employeeName", employee.getName());
        }
        if(employee.getDesignation() != null){
            document.append("employeeDesignation", employee.getDesignation());
        }

//        UpdateResult updateResult = mongoCollection.updateOne(new Document("_id", employee.getId()),
//                new Document("$set", new Document("employeeDesignation", employee.getDesignation())
//                        .append("employeeName", employee.getName())));
        UpdateResult updateResult = mongoCollection.updateOne(new Document("_id", employee.getId()),
                new Document("$set", document));

        return updateResult.getModifiedCount() > 0;
    }

    public boolean deleteEmployee(int employeeId) {
        DeleteResult deleteResult = mongoCollection.deleteOne(new Document("_id", employeeId));
        return deleteResult.getDeletedCount() > 0;
    }
}
