package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Hobby;
import entities.dto.HobbyDTO;
import errorhandling.dto.ExceptionDTO;
import facades.HobbyFacade;
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
@Path("hobby")
public class HobbyResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/ca2",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final HobbyFacade FACADE = HobbyFacade.getHobbyFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all hobbies",
            tags = {"hobby"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = HobbyDTO.class)),
                        responseCode = "200", description = "Succesfull operation")

            })
    public List<HobbyDTO> getAll() {
        List<HobbyDTO> dto = new ArrayList();

        for (Hobby h : FACADE.getAll()) {
            dto.add(new HobbyDTO(h));
        }

        return dto;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get a single hobby from an id",
            tags = {"hobby"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = HobbyDTO.class)),
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
    public HobbyDTO getByID(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid id", 400);
        }

        Hobby hobby = FACADE.getById(id);
        if (hobby == null) {
            throw new WebApplicationException("Hobby not found", 404);
        }
        return new HobbyDTO(hobby);
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a hobby",
            tags = {"hobby"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = HobbyDTO.class)),
                        responseCode = "200", description = "Successful Operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Hobby not Found")
            })
    public HobbyDTO addHobby(HobbyDTO hobbydto) {
        if (!validateHobbyDTO(hobbydto)) {
            throw new WebApplicationException("Invalid Input", 400);
        }
        Hobby hobby = new Hobby(hobbydto.getName(), hobbydto.getDescription());
        HobbyDTO dto = new HobbyDTO(FACADE.add(hobby));
        return dto;
    }

    @PUT
    @Path("/edit/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Editing a hobby",
            tags = {"hobby"},
            responses = {
                @ApiResponse(
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = HobbyDTO.class)),
                        responseCode = "200", description = "Operation Successful"),
                @ApiResponse(
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)),
                responseCode = "404", description = "Hobby not found")
            })
    public HobbyDTO editHobby(@PathParam("id") long id, HobbyDTO hobbydto) {
        if (id <= 0 || !validateHobbyDTO(hobbydto)) {
            throw new WebApplicationException("Invalid Id", 400);
        }

        Hobby hobby = FACADE.getById(id);
        if (hobby == null) {
            throw new WebApplicationException("Hobby not found", 404);
        }

        hobby.setName(hobbydto.getName());
        hobby.setDescription(hobbydto.getDescription());
        HobbyDTO dto = new HobbyDTO(FACADE.edit(hobby));
        return dto;
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Deleting a Hobby",
            tags = {"hobby"},
            responses = {
                @ApiResponse(
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = HobbyDTO.class)),
                        responseCode = "200", description = "Operation Successful"),
                @ApiResponse(
                content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                content = @Content(mediaType = "application/json", 
                        schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Hobby not Found")
            })
    public Response deleteHobby(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid ID provided", 400);
        }

        Hobby hobby = FACADE.getById(id);
        if (hobby == null) {
            throw new WebApplicationException("Hobby not found", 404);
        }

        FACADE.delete(hobby.getId());
        return Response.status(200)
                .entity("{\"code\" : \"200\", \"message\" : \"Hobby with id: " + hobby.getId()
                        + " was deleted sucesfully\"}").type(MediaType.APPLICATION_JSON).build();
    }
    
    private boolean validateHobbyDTO(HobbyDTO hobbydto) {
        if(hobbydto == null || hobbydto.getName() == null || hobbydto.getDescription() == null
                || hobbydto.getName().isEmpty() || hobbydto.getDescription().isEmpty()) {
            return false;
        }
        return true;
    }
}
