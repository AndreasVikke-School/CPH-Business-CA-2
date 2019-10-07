package facades;

import entities.Address;
import entities.CityInfo;
import entities.Hobby;
import entities.InfoEntity;
import entities.Person;
import entities.Phone;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

/**
 *
 * @author Joe
 */
public class test {

    public static void main(String[] args) throws Throwable {
        CityInfo ci1 = new CityInfo("2800", "Skodsborg");
        CityInfo ci2 = new CityInfo("2900", "Cph");
        CityInfo ci3 = new CityInfo("3000", "Odense C");
        
        List<Address> address = new ArrayList();
        address.add(new Address("Hejvejtest1", ci1));
        address.add(new Address("Hejvejtest2", ci1));
        address.add(new Address("Hejvejtest1", ci2));
        address.add(new Address("Hejvejtest2", ci2));
        address.add(new Address("Hejvejtest1", ci3));
        address.add(new Address("Hejvejtest2", ci3));
        
        List<Phone> phones = new ArrayList();
        phones.add(new Phone("12345678", "Moms phone"));
        phones.add(new Phone("12345679", "Dads phone"));
        
        List<Phone> onePhone = new ArrayList();
        phones.add(new Phone("87654321", "Moms phone1"));
        
        List<Hobby> hobbies = new ArrayList();
        hobbies.add(new Hobby("Boxing", "Slap peoples head"));
        hobbies.add(new Hobby("KickBoxing", "Slapping peoples head with leg"));
        
        InfoEntity ie = new InfoEntity("db@db.dk", phones, address.get(0));
        InfoEntity onePhoneIe = new InfoEntity("db@db.dk", onePhone, address.get(0));
        
        List<Person> persons = new ArrayList();
        persons.add(new Person("testFirstName", "testLastName", hobbies, ie));
        persons.add(new Person("firstName", "lastName", hobbies, ie));
        
        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.DROP_AND_CREATE);
        EntityManager em = emf.createEntityManager();
        AddressFacade af = AddressFacade.getAddressFacade(emf);
        
        em.getTransaction().begin();
        em.persist(ci1);
        em.persist(ci2);
        em.persist(ci3);
        em.getTransaction().commit();
        
        //Address facade methods
        for(Address a : address){
            af.add(a);
            System.out.println(af.getById(a.getId()));
        }
        System.out.println(af.getAll());
        Address aTest = af.add(new Address("testNewAddress", new CityInfo("1337", "NonExsistingCity")));
        aTest.setCity(new CityInfo("133337", "NewCityEdit"));
        af.edit(aTest);
        af.delete(aTest.getId());
        
        //Phone facade methods
        PhoneFacade phf = PhoneFacade.getPhoneFacade(emf);
        for(Phone p : phones) {
            phf.add(p);
            System.out.println(phf.getById(p.getId()));
        }
        System.out.println(phf.getAll());
        Phone ph1 = phf.add(new Phone("12121212", "TestAddPhone"));
        ph1.setDescription("NewDescriptionForEdit");
        phf.edit(ph1);
        phf.delete(ph1.getId());
        
        //Hobby facade methods
        HobbyFacade hf = HobbyFacade.getHobbyFacade(emf);
        for(Hobby h : hobbies) {
            hf.add(h);
            System.out.println(hf.getById(h.getId()));
        }
        System.out.println(hf.getAll());
        Hobby h1 = hf.add(new Hobby("Assignment", "Coding my assignment"));
        h1.setDescription("Coding edited");
        hf.edit(h1);
        hf.delete(h1.getId());
        
        //Hobby facade methods(Remember to add hobbies before person)
        PersonFacade pf = PersonFacade.getPersonFacade(emf);
        for(Person p : persons) {
            pf.add(p);
            System.out.println(pf.getById(p.getId()));
        }
        System.out.println(pf.getAll());
        Person testP = pf.add(new Person("addFirstName", "addLastName", hobbies, onePhoneIe));
        System.out.println(pf.getByPhone("87654321"));
        testP.setFirsName("newAddFirstName");
        //pf.edit(testP);
        //pf.delete(testP.getId());
        System.out.println(pf.getPersonsByCity("Skodsborg"));
        
        //Last 2 hobby facade methods(Remember to add person before this test)
        System.out.println(hf.getPersonsByHobby(hobbies.get(1).getId()));
        System.out.println(hf.getPersonCountByHobby(hobbies.get(0).getId()));
    }
}
