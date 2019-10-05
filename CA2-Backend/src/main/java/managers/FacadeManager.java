package managers;

import java.util.List;
import javax.persistence.TypedQuery;

/**
 *
 * @author andreas
 */
public class FacadeManager {
    public static <T> T getSingleResult(TypedQuery<T> query) {
        query.setMaxResults(1);
        List<T> list = query.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }
}
