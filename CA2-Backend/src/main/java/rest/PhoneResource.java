package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.dto.PhoneDTO;
import entities.Phone;
import utils.EMF_Creator;
import facades.PhoneFacade;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//Todo Remove or change relevant parts before ACTUAL use
@Path("phone")
public class PhoneResource implements iResource<PhoneDTO>{

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/ca2",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
    private static final PhoneFacade FACADE =  PhoneFacade.getPhoneFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @Override
    public PhoneDTO getById(long id) {
        PhoneDTO dto = new PhoneDTO(FACADE.getById(id));
        return dto;
    }

    @Override
    public List<PhoneDTO> getAll() {
        List<PhoneDTO> dto = new ArrayList();
        for(Phone p : FACADE.getAll()){
            dto.add(new PhoneDTO(p));
        }
        return dto;
    }

    @Override
    public PhoneDTO add(PhoneDTO obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
    }

    @Override
    public PhoneDTO edit(PhoneDTO obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PhoneDTO delete(long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}
