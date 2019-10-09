package rest;

import entities.Company;
import entities.dto.CompanyDTO;
import errorhandling.dto.ExceptionDTO;
import facades.CompanyFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import utils.EMF_Creator;

/**
 *
 * @author asgerhs
 */
@Path("company")
public class CompanyResource {
       private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/ca2",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final CompanyFacade FACADE = CompanyFacade.getCompanyFacade(EMF);
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
       @Operation(summary = "Get all Companies",
            tags = {"company"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CompanyDTO.class)),
                        responseCode = "200", description = "Succesfull operation")

            })
    public List<CompanyDTO> getAll(){
        throw new UnsupportedOperationException();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a single company from an id",
            tags = {"company"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CompanyDTO.class)),
                        responseCode = "200", description = "Successful operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Hobby not found")
            })
    public CompanyDTO getById(@PathParam("id") long id){
        throw new UnsupportedOperationException();
    }
    
    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a single company from an id",
            tags = {"company"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CompanyDTO.class)),
                        responseCode = "200", description = "Successful operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Hobby not found")
            })
    public CompanyDTO addCompany(CompanyDTO obj){
        throw new UnsupportedOperationException();
    }
    
    @PUT
    @Path("/edit/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a single company from an id",
            tags = {"company"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CompanyDTO.class)),
                        responseCode = "200", description = "Successful operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Hobby not found")
            })
    public CompanyDTO editCompany(@PathParam("id") long id){
        throw new UnsupportedOperationException();
    }
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a single company from an id",
            tags = {"company"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CompanyDTO.class)),
                        responseCode = "200", description = "Successful operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Hobby not found")
            })
    public Response deleteCompany(@PathParam("id") long id){
        throw new UnsupportedOperationException();
    }
}
