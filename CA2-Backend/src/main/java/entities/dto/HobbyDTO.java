package entities.dto;

import entities.Hobby;
import entities.Person;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andreas
 */
@Schema(name = "Hobby")
public class HobbyDTO {
    private long id;
    @Schema(required = true, example = "Fishing")
    private String name;
    @Schema(required = true, example = "Fishing in a lake")
    private String description;
    private List<PersonDTO> persons;

    public HobbyDTO() {
    }

    
    
    public HobbyDTO(long id, String name, String description, List<PersonDTO> persons) {
        this.id = id;
        this.name = name;
        this.description = description;
        //this.persons = persons;
    }
    
    public HobbyDTO(Hobby hobby) {
        this.id = hobby.getId();
        this.name = hobby.getName();
        this.description = hobby.getDescription();
        
//        this.persons = new ArrayList();
//        for(Person p : hobby.getPersons())
//            this.persons.add(new PersonDTO(p));
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
