package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.InfoEntity;
import static entities.InfoEntity_.address;
import entities.Person;
import entities.Phone;
import entities.dto.AddressDTO;
import entities.dto.HobbyDTO;
import entities.dto.PersonDTO;
import entities.dto.PhoneDTO;
import errorhandling.dto.ExceptionDTO;
import facades.AddressFacade;
import facades.HobbyFacade;
import facades.PersonFacade;
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
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/ca2",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final HobbyFacade hFACADE = HobbyFacade.getHobbyFacade(EMF);
    private static final AddressFacade aFACADE = AddressFacade.getAddressFacade(EMF);
    private static final PhoneFacade pFACADE = PhoneFacade.getPhoneFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single person by their id",
            tags = {"person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = PersonDTO.class)),
                        responseCode = "200", description = "Successful Operation")
            })
    public List<PersonDTO> getAll() {
        List<PersonDTO> dto = new ArrayList();

        for (Person p : FACADE.getAll()) {
            dto.add(new PersonDTO(p));
        }
        return dto;
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single person by their id",
            tags = {"person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = PersonDTO.class)),
                        responseCode = "200", description = "Successful Operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid Input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Person not found")
            })
    public PersonDTO getById(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("Invalid input", 400);
        }

        Person p = FACADE.getById(id);
        if (p == null) {
            throw new WebApplicationException("Person Not Found", 400);
        }

        PersonDTO dto = new PersonDTO(p);
        return dto;
    }

    @POST
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single person by their id",
            tags = {"person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = PersonDTO.class)),
                        responseCode = "200", description = "Successful Operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid Input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Person not found")
            })
    public PersonDTO addPerson(PersonDTO obj) {
        if (!validatePersonDTO(obj)) {
            throw new WebApplicationException("Invalid Input", 400);
        }

        // Create List of Phones
        List<Phone> phones = new ArrayList();
        for (PhoneDTO phone : obj.getPhones()) {
            phones.add(pFACADE.add(new Phone(phone.getNumber(), phone.getDescription())));
        }

        // Create Address
        CityInfo ci = new CityInfo(obj.getAddress().getCityInfo().getCity(), obj.getAddress().getCityInfo().getCity());
        Address address = new Address(obj.getAddress().getStreet(), ci);
        address = aFACADE.add(address);

        // Create InfoEntity
        InfoEntity ie = new InfoEntity(obj.getEmail(), phones, address);

        // Create Hobby
        List<Hobby> hobby = new ArrayList();
        for (HobbyDTO h : obj.getHobbies()) {
            hobby.add(hFACADE.add(new Hobby(h.getName(), h.getDescription())));
        }

        // Create Person
        Person p = new Person(obj.getFirsName(), obj.getLastName(), hobby, ie);
        PersonDTO dto = new PersonDTO(FACADE.add(p));

        return dto;
    }

    @PUT
    @Path("/edit/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single person by their id",
            tags = {"person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = PersonDTO.class)),
                        responseCode = "200", description = "Successful Operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid Input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Person not found")
            })
    public PersonDTO editPerson(@PathParam("id") long id, PersonDTO obj) {

        if (id <= 0) {
            throw new WebApplicationException("invalid Input", 400);
        }

        Person p = FACADE.getById(id);
        if (p == null) {
            throw new WebApplicationException("Person Not Found", 404);
        }

        if (!validatePersonDTO(obj)) {
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

        // Create Hobby
        List<Hobby> hobby = new ArrayList();
        for (HobbyDTO h : obj.getHobbies()) {
            hobby.add(hFACADE.add(new Hobby(h.getName(), h.getDescription())));
        }

        p.setFirsName(obj.getFirsName());
        p.setLastName(obj.getLastName());
        p.setHobbies(hobby);
        p.setPhones(phones);
        p.setAddress(address);
        p.setEmail(obj.getEmail());

        PersonDTO dto = new PersonDTO(FACADE.edit(p));

        return dto;

    }

    @DELETE
    @Path("/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Gets a single person by their id",
            tags = {"person"},
            responses = {
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = PersonDTO.class)),
                        responseCode = "200", description = "Successful Operation"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "400", description = "Invalid Input"),
                @ApiResponse(
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ExceptionDTO.class)),
                        responseCode = "404", description = "Person not found")
            })
    public Response deletePerson(@PathParam("id") long id) {
        if (id <= 0) {
            throw new WebApplicationException("invalid Input", 400);
        }

        Person p = FACADE.getById(id);
        if (p == null) {
            throw new WebApplicationException("Person Not Found", 404);
        }
        FACADE.delete(id);

        return Response.status(200)
                .entity("{\"code\" : \"200\", \"message\" : \"Person with id: " + p.getId()
                        + " was deleted sucesfully\"}").type(MediaType.APPLICATION_JSON).build();
    }

    @GET
    @Path("/findByZip/{zip}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonDTO> getByZip(@PathParam("zip") String zip) {
        List<Person> pers = FACADE.getPersonsByCity(zip);
        List<PersonDTO> dto = new ArrayList();
        for (Person person : pers) {
            dto.add(new PersonDTO(person));
        }
        return dto;
    }

    private boolean validatePersonDTO(PersonDTO persondto) {
        if (persondto.getPhones() == null
                || persondto.getAddress() == null
                || persondto.getAddress().getStreet() == null || persondto.getAddress().getStreet().isEmpty()
                || persondto.getAddress().getCityInfo().getCity() == null || persondto.getAddress().getCityInfo().getCity().isEmpty()
                || persondto.getAddress().getCityInfo().getZip() == null || persondto.getAddress().getCityInfo().getZip().isEmpty()
                || persondto.getEmail() == null || persondto.getEmail().isEmpty()
                || persondto.getHobbies() == null
                || persondto.getFirsName() == null || persondto.getFirsName().isEmpty()
                || persondto.getLastName() == null || persondto.getLastName().isEmpty()) {
            return false;
        }
        return true;
    }
}
