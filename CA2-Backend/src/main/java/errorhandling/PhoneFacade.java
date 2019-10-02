package errorhandling;

import entities.CityInfo;
import entities.Phone;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Martin Frederiksen
 */
public class PhoneFacade {
    private static PhoneFacade instance;
    private static EntityManagerFactory emf;

    public static PhoneFacade getPhoneFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PhoneFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public Phone getPhoneById() {
        return null;
    }
    
    public List<Phone> getPhoneAll() {
        return null;
    }
    
    public Phone addPhone(){
        return null;
    }
    
    public Phone deletePhone(){
        return null;
    }

    public Phone editPhone(){
        return null;
    }
}
