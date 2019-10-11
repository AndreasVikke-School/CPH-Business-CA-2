package rest;

import entities.Address;
import entities.CityInfo;
import entities.Company;
import entities.InfoEntity;
import entities.Phone;
import entities.dto.CompanyDTO;
import entities.dto.PersonDTO;
import entities.dto.PhoneDTO;
import errorhandling.dto.ExceptionDTO;
import facades.AddressFacade;
import facades.CompanyFacade;
import facades.PhoneFacade;
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
    private static final AddressFacade aFACADE = AddressFacade.getAddressFacade(EMF);
    private static final PhoneFacade pFACADE = PhoneFacade.getPhoneFacade(EMF);

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
    @Path("/getByCount/{value}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all Companies with more than x employees",
            tags = {"company"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CompanyDTO.class)),
                        responseCode = "200", description = "Succesfull operation")

            })
     public List<CompanyDTO> getAllByCount(@PathParam("value") int value) {
         List<CompanyDTO> dto = new ArrayList();
         for (Company c : FACADE.getCompanyWithMoreThanPersons(value)){
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
        if (!validateCompanyDTO(obj)) {
            throw new WebApplicationException("Invalid Input", 400);
        }

        // Create List of Phones
        List<Phone> phones = new ArrayList();
        for (PhoneDTO phone : obj.getPhones()) {
            phones.add(pFACADE.add(new Phone(phone.getNumber(), phone.getDescription())));
        }
        
         // Create Address
        CityInfo ci = new CityInfo(obj.getAddress().getCityInfo().getZip(), obj.getAddress().getCityInfo().getCity());
        Address address = new Address(obj.getAddress().getStreet(), ci);
        address = aFACADE.add(address);


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
        //Checks for a valid Id
        if (id <= 0) {
            throw new WebApplicationException("Invalid Input", 400);
        }
        
        //get's a company by it's Id and checks if it exists 
        Company c = FACADE.getById(id);
        if (c == null) {
            throw new WebApplicationException("Company not Found", 404);
        }
        
        //checks if all inputs are valid, method at the bottom
        if(!validateCompanyDTO(obj)){
            throw new WebApplicationException("Invalid Input", 400);
        }
        
           // Create List of Phones
        List<Phone> phones = new ArrayList();
        for (PhoneDTO phone : obj.getPhones()) {
            phones.add(pFACADE.add(new Phone(phone.getNumber(), phone.getDescription())));
        }
        
         // Create Address
        CityInfo ci = new CityInfo(obj.getAddress().getCityInfo().getZip(), obj.getAddress().getCityInfo().getCity());
        Address address = new Address(obj.getAddress().getStreet(), ci);
        address = aFACADE.add(address);

        //sets everything to the company from what was recieved
        //from the ComanyDTO
        c.setName(obj.getName());
        c.setDescription(obj.getDescription());
        c.setCvr(obj.getCvr());
        c.setEmployeeCount(obj.getEmployeeCount());
        c.setMarketValue(obj.getMarketValue());
        c.setAddress(address);
        c.setEmail(obj.getEmail());
        c.setPhones(phones);

        //finally, creates a new DTO and edits the company. 
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
        //Checks whether the provided Id is valid (eg. above 0)
        if(id <= 0){
            throw new WebApplicationException("Invalid Id", 400);
        }
        
        //Gets the company by its id, and checks whether the company exists.
        Company c = FACADE.getById(id);
        if(c == null){
            throw new WebApplicationException("Company not found", 404);
        }
        FACADE.delete(c.getId());
        return Response.status(200)
                .entity("{\"code\" : \"200\", \"message\" : \"Company with id: " + c.getId()
                        + " was deleted sucesfully\"}").type(MediaType.APPLICATION_JSON).build();

    }

    private boolean validateCompanyDTO(CompanyDTO c) {
        if (c.getPhones() == null
                || c.getAddress() == null
                || c.getAddress().getStreet() == null || c.getAddress().getStreet().isEmpty()
                || c.getAddress().getCityInfo().getCity() == null || c.getAddress().getCityInfo().getCity().isEmpty()
                || c.getAddress().getCityInfo().getZip() == null || c.getAddress().getCityInfo().getZip().isEmpty()
                || c.getEmail() == null || c.getEmail().isEmpty()
                || c.getCvr() == null || c.getCvr().isEmpty()
                || c.getDescription() == null || c.getDescription().isEmpty()
                || c.getEmployeeCount() <= 0  
                || c.getName() == null || c.getName().isEmpty() ) {
            return false;
        }
        return true;
    }
}
