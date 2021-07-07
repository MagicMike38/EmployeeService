package com.mycompany.resource;

import com.mongodb.MongoWriteException;
import com.mycompany.model.Employee;
import io.swagger.annotations.*;
import io.swagger.jaxrs.PATCH;
import com.mycompany.exception.NotFoundException;
import com.mycompany.services.AbstractEmployeeService;
import com.mycompany.services.AbstractKakfaService;
import com.mycompany.services.EmployeeKafkaService;
import com.mycompany.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Path("/employee")
@Api(value = "/employee", tags = "employee")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class EmployeeResource {
    private Logger LOGGER = LoggerFactory.getLogger(EmployeeResource.class);

    String propFileName = "application.properties";
    InputStream inputStream;
    Properties props;
    private final AbstractEmployeeService employeeService;
    private final AbstractKakfaService<Employee> employeeKafkaService;
    private void initialize() throws IOException {
        inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
        props = new Properties();
        if (inputStream != null) {
            props.load(inputStream);
        } else {
            throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
        }
    }

    public EmployeeResource() throws IOException {
        initialize();
        employeeService = new EmployeeService(props);
        employeeKafkaService =  new EmployeeKafkaService(props);
    }
    @GET
    @Path("/{employeeId}")
    @ApiOperation(value = "Find Employee by ID",
            notes = "Returns employee information with id",
            response = Employee.class
    )
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Employee not found") })
    public Response getEmployeeById(
            @ApiParam(value = "ID of Employee to return") @PathParam("employeeId") int employeeId) {
        try{
            Employee employee = employeeService.getEmployee(employeeId);
            if (employee != null) {
                return Response.status(Response.Status.OK).entity(employee).type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{employeeId}")
    @ApiOperation(value = "Deletes a employee")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Employee not found") })
    public Response deleteEmployee(
            @ApiParam(value = "Employee id to delete", required = true)@PathParam("employeeId") int employeeId) throws Exception {
        String message = "{\"Status\": \"Success\"}";
        try{
            if (employeeService.deleteEmployee(employeeId)) {
                return Response.status(Response.Status.OK).entity(message).type(MediaType.APPLICATION_JSON).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        catch (Exception ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Add new Employee")
    @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") })
    public Response createEmployee(
            @ApiParam(value = "Employee Details to add", required = true) Employee employee) {

        String message = "{\"Status\": \"Success\"}";

        try{
            employeeService.createEmployee(employee);
        }
        catch(MongoWriteException mex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Duplicate Id. Unable to Create.").build();
        }
        catch (Exception ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(message).type(MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Update an existing employee")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Employee not found"),
            @ApiResponse(code = 405, message = "Validation exception") })
    public Response updateEmployee(
            @ApiParam(value = "Employee to update", required = true)Employee employee) {
        String message = "{\"Status\": \"Success\"}";
        try{
            if(!employeeService.updateEmployee(employee)){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Id not found. Unable to Edit.").build();
            }
        }
        catch (Exception ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(message).type(MediaType.APPLICATION_JSON).build();
    }

    @PATCH
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Patch an existing employee")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Employee not found"),
            @ApiResponse(code = 405, message = "Validation exception") })
    public Response patchEmployee(
            @ApiParam(value = "Employee to update", required = true)Employee employee) {
        String message = "{\"Status\": \"Success\"}";
        try{
            if(!employeeService.patchEmployee(employee)){
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Id not found. Unable to Edit.").build();
            }
        }
        catch (Exception ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).build();
        }
        return Response.status(Response.Status.CREATED).entity(message).type(MediaType.APPLICATION_JSON).build();
    }

    @POST
    @Path("publish")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Publish new employee details to Kafka")
    @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") })
    public Response publish(
            @ApiParam(value = "Employee Details to add", required = true) Employee employee) {
        String message = "{\"Status\": \"Published successfully\"}";
        try{
            employeeKafkaService.publish(employee);
        }
        catch (Exception ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CREATED).entity(message).type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("consume")
    @ApiOperation(value = "Consume from Kafka and write to MongoDB")
    @ApiResponses(value = { @ApiResponse(code = 405, message = "Invalid input") })
    public Response consume() {
        String message = "{\"Status\": \"Consumed successfully\"}";
        try{
            employeeKafkaService.consume();
        }
        catch (Exception ex){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
        return Response.status(Response.Status.CREATED).entity(message).type(MediaType.APPLICATION_JSON).build();
    }
}

