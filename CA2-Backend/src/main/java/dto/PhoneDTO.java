package dto;

import entities.Phone;

/**
 *
 * @author andreas
 */
public class PhoneDTO {
    private long id;
    private String number;
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
