package rest;

import javax.enterprise.context.Dependent;
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
public interface iResource<O> {

    @GET
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    O getById(@PathParam("id") long id);
    
    
    @GET
    @Path("/all")
    @Consumes(MediaType.APPLICATION_JSON)
    O getAll();

    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    O add(String obj);

    @PUT
    @Path("/edit")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    O edit(String obj);

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    O delete(@PathParam("id") long id);

}
