package entities.dto;

import entities.Company;
import entities.InfoEntity;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 *
 * @author andreas
 */
@Schema(name = "Company")
public class CompanyDTO extends InfoEntityDTO {
    @Schema(required = true, example = "CompanyName A/S")
    private String name;
    @Schema(required = true, example = "Shipping Containers")
    private String description;
    @Schema(required = true, example = "12345678")
    private String cvr;
    @Schema(required = true, example = "100")
    private int employeeCount;
    @Schema(required = true, example = "5000000")
    private long marketValue;

    public CompanyDTO(String name, String description, String cvr, int employeeCount, long marketValue, InfoEntity infoEntity) {
        super(infoEntity);
        this.name = name;
        this.description = description;
        this.cvr = cvr;
        this.employeeCount = employeeCount;
        this.marketValue = marketValue;
    }
    
    public CompanyDTO(Company company) {
        super(company);
        this.name = company.getName();
        this.description = company.getDescription();
        this.cvr = company.getCvr();
        this.employeeCount = company.getEmployeeCount();
        this.marketValue = company.getMarketValue();
    }

    public CompanyDTO() {
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

    public String getCvr() {
        return cvr;
    }

    public void setCvr(String cvr) {
        this.cvr = cvr;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(int employeeCount) {
        this.employeeCount = employeeCount;
    }

    public long getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(long marketValue) {
        this.marketValue = marketValue;
    }
}
