package entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Martin Frederiksen
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "Person.deleteAllRows", query = "DELETE from Person"),
    @NamedQuery(name = "Person.findAll", query = "SELECT p FROM Person p"),
})
public class Person extends InfoEntity implements Serializable {
    private String firsName;
    private String lastName;
    @ManyToMany
    private List<Hobby> hobbies;
    
    public Person() {
    }

    public Person(String firsName, String lastName, List<Hobby> hobbies, InfoEntity infoEntity) {
        super(infoEntity.getEmail(), infoEntity.getPhones(), infoEntity.getAddress());
        this.firsName = firsName;
        this.lastName = lastName;
        this.hobbies = hobbies;
    }

    public String getFirsName() {
        return firsName;
    }

    public void setFirsName(String firsName) {
        this.firsName = firsName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Hobby> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<Hobby> hobbies) {
        this.hobbies = hobbies;
    }
}
