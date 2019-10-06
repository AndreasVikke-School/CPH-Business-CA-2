package entities.dto;

import entities.InfoEntity;
import entities.Phone;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andreas
 */
@Schema(name = "InfoEntity")
public class InfoEntityDTO {
    private long id;
    @Schema(required = true, example = "example@example.com")
    private String email;
    private List<PhoneDTO> phones;
    private AddressDTO address;

    public InfoEntityDTO(long id, String email, List<PhoneDTO> phones, AddressDTO addresses) {
        this.id = id;
        this.email = email;
        this.phones = phones;
        this.address = addresses;
    }
    
    public InfoEntityDTO(InfoEntity infoEntity) {
        this.id = infoEntity.getId();
        this.email = infoEntity.getEmail();
        this.address = new AddressDTO(infoEntity.getAddress());
        
        this.phones = new ArrayList();
        for(Phone p : infoEntity.getPhones())
            this.phones.add(new PhoneDTO(p));
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PhoneDTO> getPhones() {
        return phones;
    }

    public void setPhones(List<PhoneDTO> phones) {
        this.phones = phones;
    }

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }
}
