package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Hobby;
import entities.Phone;
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
            tags = {"Hobby"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Hobby.class)),
                        responseCode = "200", description = "Succesfull operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Hobby not found")

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
            tags = {"Hobby"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Hobby.class)),
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

        HobbyDTO dto = new HobbyDTO(FACADE.getById(id));
        if (dto == null) {
            throw new WebApplicationException("Hobby not found", 404);
        }
        return dto;
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a hobby",
            tags = {"Hobby"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = Hobby.class)),
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
    public HobbyDTO addHobby(HobbyDTO DTO) {
        if (DTO == null || DTO.getName() == null || DTO.getDescription() == null
                || DTO.getName().isEmpty() || DTO.getDescription().isEmpty()) {
            throw new WebApplicationException("Invalid Input", 400);
        }
        Hobby hobby = new Hobby(DTO.getName(), DTO.getDescription());
        HobbyDTO dto = new HobbyDTO(FACADE.add(hobby));
        return dto;
    }

    @PUT
    @Path("/edit/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Editing a hobby",
            tags = {"Hobby"},
            responses = {
                @ApiResponse(
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Hobby.class)),
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
    public HobbyDTO editHobby(@PathParam("id") long id, HobbyDTO DTO) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid Id", 400);
        }

        Hobby hobby = FACADE.getById(id);
        if (hobby == null) {
            throw new WebApplicationException("Hobby not found", 404);
        }

        hobby.setName(DTO.getName());
        hobby.setDescription(DTO.getDescription());
        HobbyDTO dto = new HobbyDTO(FACADE.edit(hobby));
        return dto;
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Deleting a Hobby",
            tags = {"Hobby"},
            responses = {
                @ApiResponse(
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = Hobby.class)),
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
    public Response deleteHobby(@PathParam("id") long id, HobbyDTO DTO) {
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
}
