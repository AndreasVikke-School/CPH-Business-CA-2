package entities.dto;

import entities.Phone;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author andreas
 */
@Schema(name = "Phone")
public class PhoneDTO {
    private long id;
    @Schema(required = true, example = "12345678")
    private String number;
    @Schema(required = true, example = "Mobile")
    private String description;

    public PhoneDTO(long id, String number, String description) {
        this.id = id;
        this.number = number;
        this.description = description;
    }
    
    public PhoneDTO(Phone phone) {
        this.id = phone.getId();
        this.number = phone.getNumber();
        this.description = phone.getDescription();
    }

    public PhoneDTO() {
    }
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
