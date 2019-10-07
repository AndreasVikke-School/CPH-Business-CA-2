package rest;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author asgerhs
 */
public interface iResource<O, T> {

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    O getById(@PathParam("id") long id);
    
    
    @GET
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    List<O> getAll();

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    O add(O obj);

    @PUT
    @Path("/edit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    O edit(O obj);

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    T delete(@PathParam("id") long id);

}
