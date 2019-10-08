package rest;


import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import java.util.Set;
import javax.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(
                title = "Course Assignment 2",
                version = "1.0",
                description = "CA-2 API documentation"),
        servers = {
                @Server(
                        description = "LocalHost",
                        url = "http://localhost:8080/ca2"),
                @Server(
                        description = "Deployed",
                        url = "https://andreasvikke.dk/CA2-Backend")
        }
)
@javax.ws.rs.ApplicationPath("api")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        
        //These two Resource Classes are not auto discovered so we add them manually
        resources.add(OpenApiResource.class);
        resources.add(AcceptHeaderOpenApiResource.class);

        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method. It is automatically
     * populated with all resources defined in the project. If required, comment
     * out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(errorhandling.GenericExceptionMapper.class);
        resources.add(rest.AddressResource.class);
        resources.add(rest.CompanyResource.class);
        resources.add(rest.HobbyResource.class);
        resources.add(rest.PersonResource.class);
        resources.add(rest.PhoneResource.class);
    }

}
