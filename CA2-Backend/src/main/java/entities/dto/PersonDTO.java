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
@Schema(name = "Person")
public class PersonDTO extends InfoEntityDTO {
    @Schema(required = true, example = "Alan")
    private String firsName;
    @Schema(required = true, example = "Petersen")
    private String lastName;
    private List<HobbyDTO> hobbies;
    
    public PersonDTO(String firsName, String lastName, List<HobbyDTO> hobbies, InfoEntityDTO infoEntitydto) {
        super(infoEntitydto.getId(), infoEntitydto.getEmail(), infoEntitydto.getPhones(), infoEntitydto.getAddress());
        this.firsName = firsName;
        this.lastName = lastName;
        this.hobbies = hobbies;
    }
    
    public PersonDTO(Person p) {
        super(p);
        this.firsName = p.getFirsName();
        this.lastName = p.getLastName();
        
        this.hobbies = new ArrayList();
        for(Hobby h : p.getHobbies())
            this.hobbies.add(new HobbyDTO(h));
    }

    public PersonDTO() {
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

    public List<HobbyDTO> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<HobbyDTO> hobbies) {
        this.hobbies = hobbies;
    }
}
