package com.mycompany;

import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.models.*;

import io.swagger.models.auth.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

public class Bootstrap extends HttpServlet {
  @Override
  public void init(ServletConfig config) throws ServletException {
    Info info = new Info()
      .title("Employee CRUD Service")
      .description("This service supports Create, Read, Update, Delete of the Employee model")
      .termsOfService("http://swagger.io/terms/")
      .contact(new Contact()
        .email("apiteam@swagger.io"))
      .license(new License()
        .name("Apache 2.0")
        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));

    ServletContext context = config.getServletContext();
    Swagger swagger = new Swagger().info(info);
    swagger.externalDocs(new ExternalDocs("Find out more about Swagger", "http://swagger.io"));
    swagger.tag(new Tag()
      .name("Employee")
      .description("Employee CRUD Service"));
    new SwaggerContextService().withServletConfig(config).updateSwagger(swagger);
  }
}
