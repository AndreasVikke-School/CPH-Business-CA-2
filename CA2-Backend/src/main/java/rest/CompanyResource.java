package rest;

import entities.Address;
import entities.CityInfo;
import entities.Company;
import entities.InfoEntity;
import entities.Phone;
import entities.dto.CompanyDTO;
import entities.dto.PhoneDTO;
import errorhandling.dto.ExceptionDTO;
import facades.CompanyFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.ArrayList;
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
import javax.ws.rs.WebApplicationException;
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
    public List<CompanyDTO> getAll() {
        List<CompanyDTO> dto = new ArrayList();
        for (Company c : FACADE.getAll()) {
            dto.add(new CompanyDTO(c));
        }
        return dto;
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
    public CompanyDTO getById(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid Input", 400);
        }
        Company c = FACADE.getById(id);
        if (c == null) {
            throw new WebApplicationException("Company Not Found", 404);
        }

        CompanyDTO dto = new CompanyDTO(c);

        return dto;
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
    public CompanyDTO addCompany(CompanyDTO obj) {
        List<Phone> phones = new ArrayList();
        for (PhoneDTO ph : obj.getPhones()) {
            phones.add(new Phone(ph.getNumber(), ph.getDescription()));
        }

        CityInfo ci = new CityInfo(obj.getAddress().getCityInfo().getCity(),
                obj.getAddress().getCityInfo().getCity());

        Address address = new Address(obj.getAddress().getStreet(), ci);

        InfoEntity ie = new InfoEntity(obj.getEmail(), phones, address);

        Company c = new Company(obj.getName(),
                obj.getDescription(),
                obj.getCvr(),
                obj.getEmployeeCount(),
                obj.getMarketValue(), ie);
        
        CompanyDTO dto = new CompanyDTO(FACADE.add(c));
        
        return dto;

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
    public CompanyDTO editCompany(@PathParam("id") long id, CompanyDTO obj) {
        if(id <= 0){
            throw new WebApplicationException("Invalid Input", 400);
        }
        Company c = FACADE.getById(id);
        if(c == null){
            throw new WebApplicationException("Company not Found", 404);
        }
        
         List<Phone> phones = new ArrayList();
        for (PhoneDTO ph : obj.getPhones()) {
            phones.add(new Phone(ph.getNumber(), ph.getDescription()));
        }
        
        CityInfo ci = new CityInfo(obj.getAddress().getCityInfo().getCity(),
                obj.getAddress().getCityInfo().getCity());

        Address address = new Address(obj.getAddress().getStreet(), ci);

        InfoEntity ie = new InfoEntity(obj.getEmail(), phones, address);
        
        c.setName(obj.getName());
        c.setDescription(obj.getDescription());
        c.setCvr(obj.getCvr());
        c.setEmployeeCount(obj.getEmployeeCount());
        c.setMarketValue(obj.getMarketValue());
        c.setAddress(address);
        
        CompanyDTO dto = new CompanyDTO(FACADE.edit(c));
        
        return dto;
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
    public Response deleteCompany(@PathParam("id") long id) {
        Company c = FACADE.getById(id);
        FACADE.delete(c.getId());
        return Response.status(200)
                .entity("{\"code\" : \"200\", \"message\" : \"Company with id: " + c.getId()
                        + " was deleted sucesfully\"}").type(MediaType.APPLICATION_JSON).build();
    
    }
}
