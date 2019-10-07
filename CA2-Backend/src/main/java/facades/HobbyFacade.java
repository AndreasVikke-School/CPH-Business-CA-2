package facades;

import entities.Hobby;
import entities.Person;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import managers.FacadeManager;

/**
 *
 * @author Martin Frederiksen
 */
public class HobbyFacade implements IFacade<Hobby> {

    private static HobbyFacade instance;
    private static EntityManagerFactory emf;

    public HobbyFacade() {
    }

    public static HobbyFacade getHobbyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new HobbyFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public Hobby getById(long id) {
        return getEntityManager().find(Hobby.class, id);
    }

    @Override
    public List<Hobby> getAll() {
        return getEntityManager().createQuery("SELECT hobby FROM Hobby hobby").getResultList();
    }

    @Override
    public Hobby add(Hobby hobby) {
        EntityManager em = getEntityManager();
        Hobby h = FacadeManager.getSingleResult(em.createQuery("SELECT hobby FROM Hobby hobby WHERE hobby.name = :name AND hobby.description = :description", Hobby.class).setParameter("name", hobby.getName()).setParameter("description", hobby.getDescription()));
        if (h == null) {
            try {
                h = hobby;
                em.getTransaction().begin();
                em.persist(h);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }
        return h;
    }

    @Override
    public Hobby edit(Hobby hobby) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(hobby);
            em.getTransaction().commit();
            return hobby;
        } finally {
            em.close();
        }
    }

    @Override
    public Hobby delete(long id) {
        EntityManager em = getEntityManager();
        Hobby h = em.find(Hobby.class, id);
        if (h != null) {
            try {
                em.getTransaction().begin();
                em.remove(h);
                em.getTransaction().commit();
            } finally {
                em.close();
            }
        }
        return h;
    }

    public List<Person> getPersonsByHobby(long hobbyId) {
        return getEntityManager().createQuery("SELECT person FROM Person person JOIN person.hobbies hobbies WHERE hobbies.id = :id", Person.class).setParameter("id", hobbyId).getResultList();
    }

    public long getPersonCountByHobby(long hobbyId) {
        return getEntityManager().createQuery("SELECT count(person) FROM Person person JOIN person.hobbies hobbies WHERE hobbies.id = :id", Long.class).setParameter("id", hobbyId).getSingleResult();
    }
}
