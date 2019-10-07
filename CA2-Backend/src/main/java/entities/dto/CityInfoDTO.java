package entities.dto;

import entities.CityInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author andreas
 */
@Schema(name = "CityInfo")
public class CityInfoDTO {

    private long id;
    @Schema(required = true, example = "Lyngby")
    private String city;
    @Schema(required = true, example = "1234")
    private String zip;

    public CityInfoDTO(long id, String city, String zip) {
        this.id = id;
        this.city = city;
        this.zip = zip;
    }

    public CityInfoDTO(CityInfo cityInfo) {
        this.id = cityInfo.getId();
        this.city = cityInfo.getCity();
        this.zip = cityInfo.getZip();
    }

    public CityInfoDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
