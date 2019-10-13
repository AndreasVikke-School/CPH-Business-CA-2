package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.dto.PhoneDTO;
import entities.Phone;
import errorhandling.dto.ExceptionDTO;
import utils.EMF_Creator;
import facades.PhoneFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
                                schema = @Schema(implementation = PhoneDTO.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid Id supplied"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Phone not found")}
    )
    public PhoneDTO getById(@PathParam("id") long id) {
        //Validates Id
        if (id <= 0) {
            throw new WebApplicationException("Invalid Id supplied", 400);
        }
        //Gets a phone, and checks if it exists.
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
                                array = @ArraySchema(schema = @Schema(implementation = PhoneDTO.class))),
                        responseCode = "200", description = "Succesful operation")}
    )
    public List<PhoneDTO> getAll() {
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
                                schema = @Schema(implementation = PhoneDTO.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input")}
    )
    public PhoneDTO add(PhoneDTO phonedto) throws WebApplicationException {
        //Validates everything needed, method at bottom.
        if (!validatePhoneDTO(phonedto)) {
            throw new WebApplicationException("Invalid input", 400);
        }

        Phone phone = new Phone(phonedto.getNumber(), phonedto.getDescription());
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
                                schema = @Schema(implementation = PhoneDTO.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Phone not found")}
    )
    public PhoneDTO edit(@PathParam("id") long id, PhoneDTO phonedto) {
        //validates Id and inputs from the user. 
        if (id <= 0 || !validatePhoneDTO(phonedto)) {
            throw new WebApplicationException("Invalid input", 400);
        }
        //checks if phone exists.
        Phone p = FACADE.getById(id);
        if (p == null) {
            throw new WebApplicationException("Phone not found", 404);
        }

        p.setNumber(phonedto.getNumber());
        p.setDescription(phonedto.getDescription());
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

    private boolean validatePhoneDTO(PhoneDTO phonedto) {
        if (phonedto == null
                || phonedto.getNumber() == null || phonedto.getDescription() == null
                || phonedto.getNumber().isEmpty() || phonedto.getDescription().isEmpty()) {
            return false;
        }
        return true;
    }
}
