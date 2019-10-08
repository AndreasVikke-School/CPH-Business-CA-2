package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.InfoEntity;
import entities.Person;
import entities.Phone;
import entities.dto.HobbyDTO;
import entities.dto.PersonDTO;
import entities.dto.PhoneDTO;
import facades.AddressFacade;
import facades.HobbyFacade;
import facades.PersonFacade;
import facades.PhoneFacade;
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
    public PersonDTO addPerson(PersonDTO obj) {

        List<Phone> phones = new ArrayList();
        for (PhoneDTO phone : obj.getPhones()) {
            Phone ph = new Phone(phone.getNumber(), phone.getDescription());
            phones.add(ph);
            pFACADE.add(ph);
        }

        CityInfo ci = new CityInfo(obj.getAddress().getCityInfo().getCity(),
                obj.getAddress().getCityInfo().getCity());

        Address address = new Address(obj.getAddress().getStreet(),
                ci);

        aFACADE.add(address);

        InfoEntity ie = new InfoEntity(obj.getEmail(), phones, address);

        List<Hobby> hobby = new ArrayList();
        for (HobbyDTO h : obj.getHobbies()) {
            Hobby ho = new Hobby(h.getName(), h.getDescription());
            ho = hFACADE.add(ho);
            hobby.add(ho);
        }

        Person p = new Person(obj.getFirsName(), obj.getLastName(), hobby, ie);

        PersonDTO dto = new PersonDTO(FACADE.add(p));

        return dto;
    }

    @PUT
    @Path("/edit/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PersonDTO editPerson(@PathParam("id") long id, PersonDTO obj) {

        Person p = FACADE.getById(id);

        List<Phone> phones = new ArrayList();
        for (PhoneDTO phone : obj.getPhones()) {
            Phone ph = new Phone(phone.getNumber(), phone.getDescription());
            phones.add(ph);
            pFACADE.add(ph);
        }

        CityInfo ci = new CityInfo(obj.getAddress().getCityInfo().getCity(),
                obj.getAddress().getCityInfo().getCity());

        Address address = new Address(obj.getAddress().getStreet(),
                ci);

        aFACADE.add(address);

        InfoEntity ie = new InfoEntity(obj.getEmail(), phones, address);

        List<Hobby> hobby = new ArrayList();
        for (HobbyDTO h : obj.getHobbies()) {
            Hobby ho = new Hobby(h.getName(), h.getDescription());
            ho = hFACADE.add(ho);
            hobby.add(ho);
        }

        p.setFirsName(obj.getFirsName());
        p.setLastName(obj.getLastName());
        p.setHobbies(hobby);

        PersonDTO dto = new PersonDTO(FACADE.edit(p));

        return dto;

    }

    @DELETE
    @Path("/delete/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletePerson(@PathParam("id") long id) {
        Person p = FACADE.getById(id);
        FACADE.delete(id);

        return Response.status(200)
                .entity("{\"code\" : \"200\", \"message\" : \"Person with id: " + p.getId()
                        + " was deleted sucesfully\"}").type(MediaType.APPLICATION_JSON).build();
    }

}
