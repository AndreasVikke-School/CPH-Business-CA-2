package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Hobby;
import entities.dto.HobbyDTO;
import facades.HobbyFacade;
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
    public HobbyDTO getByID(@PathParam("id") long id) {
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
    public HobbyDTO addHobby(HobbyDTO DTO) {
        Hobby hobby = new Hobby(DTO.getName(), DTO.getDescription());
        HobbyDTO dto = new HobbyDTO(FACADE.add(hobby));
        return dto;
    }

    @PUT
    @Path("/edit/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HobbyDTO editHobby(@PathParam("id") long id, HobbyDTO DTO) {
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
