package dto;

import entities.CityInfo;

/**
 *
 * @author andreas
 */
public class CityInfoDTO {
    private long id;
    private String city;
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
