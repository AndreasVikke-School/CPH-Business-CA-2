package dto;

import entities.Hobby;
import entities.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andreas
 */
public class HobbyDTO {
    private long id;
    private String name;
    private String description;
    private List<PersonDTO> persons;

    public HobbyDTO(long id, String name, String description, List<PersonDTO> persons) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.persons = persons;
    }
    
    public HobbyDTO(Hobby hobby) {
        this.id = hobby.getId();
        this.name = hobby.getName();
        this.description = hobby.getDescription();
        
        this.persons = new ArrayList();
        for(Person p : hobby.getPersons())
            this.persons.add(new PersonDTO(p));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PersonDTO> getPersons() {
        return persons;
    }

    public void setPersons(List<PersonDTO> persons) {
        this.persons = persons;
    }
}
