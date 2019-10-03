package facades;

import java.util.List;

/**
 *
 * @author Joe
 */
public interface IFacade <T>{
    T getById(long id);
    List<T> getAll();
    T add(T object);
    T edit(T object);
    T delete(long id);
}
