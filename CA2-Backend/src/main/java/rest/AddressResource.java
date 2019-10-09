package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Address;
import entities.CityInfo;
import entities.dto.AddressDTO;
import errorhandling.dto.ExceptionDTO;
import facades.AddressFacade;
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
import utils.EMF_Creator;

/**
 *
 * @author Andreas Vikke
 */
@Path("address")
public class AddressResource {
     private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/ca2",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final AddressFacade FACADE = AddressFacade.getAddressFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all adrress",
            tags = {"address"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AddressDTO.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "400", description = "Invalid Id supplied"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "404", description = "Address not found")}
    )
    public AddressDTO getById(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid Id supplied", 400);
        }

        Address address = FACADE.getById(id);
        if (address == null) {
            throw new WebApplicationException("Address not found", 404);
        }

        return new AddressDTO(address);
    }
    
    
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Get all addresses",
            tags = {"address"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        array = @ArraySchema(schema = @Schema(implementation = AddressDTO.class))),
                        responseCode = "200", description = "Succesful operation")}
    )
    public List<AddressDTO> getAll() {
        List<AddressDTO> dto = new ArrayList();
        for (Address a : FACADE.getAll()) {
            dto.add(new AddressDTO(a));
        }
        return dto;
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add a new address",
            tags = {"address"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AddressDTO.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "400", description = "Invalid input")}
    )
    public AddressDTO add(AddressDTO addressdto) throws WebApplicationException {
        if (!validateAddressDTO(addressdto)) {
            throw new WebApplicationException("Invalid input", 400);
        }

        Address address = new Address(addressdto.getStreet(), new CityInfo(addressdto.getCityInfo().getZip(), addressdto.getCityInfo().getCity()));
        AddressDTO dto = new AddressDTO(FACADE.add(address));
        return dto;
    }

    @PUT
    @Path("/edit/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Edit a address",
            tags = {"address"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = AddressDTO.class)),
                        responseCode = "200", description = "Succesful operation"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "400", description = "Invalid input"),
                @ApiResponse(content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ExceptionDTO.class)), 
                        responseCode = "404", description = "Address not found")}
    )
    public AddressDTO edit(@PathParam("id") long id, AddressDTO addressdto) {
        if (id <= 0 || !validateAddressDTO(addressdto)) {
            throw new WebApplicationException("Invalid input", 400);
        }

        Address a = FACADE.getById(id);
        if (a == null) {
            throw new WebApplicationException("Address not found", 404);
        }

        a.setStreet(addressdto.getStreet());
        a.setCityInfo(new CityInfo(addressdto.getCityInfo().getZip(), addressdto.getCityInfo().getCity()));
        Address address = FACADE.edit(a);
        return new AddressDTO(address);
    }

    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete a Address",
            tags = {"address"},
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
                        responseCode = "404", description = "Address not found")}
    )
    public Response delete(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid Id supplied", 400);
        }

        Address address = FACADE.getById(id);
        if (address == null) {
            throw new WebApplicationException("Address not found", 404);
        }

        FACADE.delete(address.getId());
        return Response.status(200)
                .entity("{\"code\" : \"200\", \"message\" : \"Address with id: " + address.getId() + " deleted successfully.\"}")
                .type(MediaType.APPLICATION_JSON).build();
    }
    
    private boolean validateAddressDTO(AddressDTO addressdto) {
        if (addressdto == null
                || addressdto.getStreet() == null || addressdto.getCityInfo() == null
                || addressdto.getCityInfo().getZip() == null || addressdto.getCityInfo().getCity() == null
                || addressdto.getCityInfo().getZip().isEmpty() || addressdto.getCityInfo().getCity().isEmpty()
                || addressdto.getStreet().isEmpty()) {
            return false;
        }
        return true;
    }
}
