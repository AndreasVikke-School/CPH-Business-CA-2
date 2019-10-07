package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.dto.PhoneDTO;
import entities.Phone;
import errorhandling.dto.ExceptionDTO;
import utils.EMF_Creator;
import facades.PhoneFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

//Todo Remove or change relevant parts before ACTUAL use
@Path("phone")
public class PhoneResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/ca2",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final PhoneFacade FACADE = PhoneFacade.getPhoneFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all phones",
            tags = {"phone"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Phone.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "400", description = "Invalid Id supplied"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "404", description = "Phone not found")}
    )
    public PhoneDTO getById(@PathParam("id") long id) throws WebApplicationException {
        if (id <= 0) {
            throw new WebApplicationException("Invalid Id supplied", 400);
        }

        Phone phone = FACADE.getById(id);
        if (phone == null) {
            throw new WebApplicationException("Phone not found", 404);
        }

        return new PhoneDTO(phone);
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all phones",
            tags = {"phone"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = Phone.class))),
                        responseCode = "200", description = "Succesful operation")}
    )
    public List<PhoneDTO> getAll() throws WebApplicationException {
        List<PhoneDTO> dto = new ArrayList();
        for (Phone p : FACADE.getAll()) {
            dto.add(new PhoneDTO(p));
        }
        return dto;
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new phone",
            tags = {"phone"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Phone.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "400", description = "Invalid Id supplied")}
    )
    public PhoneDTO add(PhoneDTO phoneDTO) throws WebApplicationException {
        if (phoneDTO == null
                || phoneDTO.getNumber() == null || phoneDTO.getDescription() == null
                || phoneDTO.getNumber().isEmpty() || phoneDTO.getNumber().isEmpty()) {
            throw new WebApplicationException("Invalid input", 400);
        }

        Phone phone = new Phone(phoneDTO.getNumber(), phoneDTO.getDescription());
        PhoneDTO dto = new PhoneDTO(FACADE.add(phone));
        return dto;
    }

    @PUT
    @Path("/edit/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Edit a phone",
            tags = {"phone"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Phone.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "400", description = "Invalid Id supplied"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "404", description = "Phone not found")}
    )
    public PhoneDTO edit(@PathParam("id") long id, PhoneDTO obj) {
        if (id <= 0 || obj == null
                || obj.getNumber() == null || obj.getDescription() == null
                || obj.getNumber().isEmpty() || obj.getNumber().isEmpty()) {
            throw new WebApplicationException("Invalid input", 400);
        }

        Phone p = FACADE.getById(id);
        if (p == null) {
            throw new WebApplicationException("Phone not found", 404);
        }

        p.setNumber(obj.getNumber());
        p.setDescription(obj.getDescription());
        Phone phone = FACADE.edit(p);
        return new PhoneDTO(phone);
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a phone",
            tags = {"phone"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "400", description = "Invalid Id supplied"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "404", description = "Phone not found")}
    )
    public Response delete(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid Id supplied", 400);
        }

        Phone phone = FACADE.getById(id);
        if (phone == null) {
            throw new WebApplicationException("Phone not found", 404);
        }

        FACADE.delete(phone.getId());
        return Response.status(200)
                .entity("{\"code\" : \"200\", \"message\" : \"Phone with id: " + phone.getId() + " deleted successfully.\"}")
                .type(MediaType.APPLICATION_JSON).build();
    }
}