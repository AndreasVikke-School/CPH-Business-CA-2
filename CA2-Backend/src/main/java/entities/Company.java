package entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

/**
 *
 * @author Martin Frederiksen
 */
@Entity
@NamedQuery(name = "Company.deleteAllRows", query = "DELETE from Company")
public class Company extends InfoEntity implements Serializable {
    private String name;
    private String description;
    private String cvr;
    private int employeeCount;
    private long marketValue;

    public Company() {
    }

    public Company(String name, String description, String cvr, int employeeCount, long marketValue, InfoEntity infoEntity) {
        super(infoEntity.getEmail(), infoEntity.getPhones(), infoEntity.getAddress());
        this.name = name;
        this.description = description;
        this.cvr = cvr;
        this.employeeCount = employeeCount;
        this.marketValue = marketValue;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        hash = 29 * hash + Objects.hashCode(this.description);
        hash = 29 * hash + Objects.hashCode(this.cvr);
        hash = 29 * hash + this.employeeCount;
        hash = 29 * hash + (int) (this.marketValue ^ (this.marketValue >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Company other = (Company) obj;
        if (this.employeeCount != other.employeeCount) {
            return false;
        }
        if (this.marketValue != other.marketValue) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.cvr, other.cvr)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Company{" + "name=" + name + ", description=" + description + ", cvr=" + cvr + ", employeeCount=" + employeeCount + ", marketValue=" + marketValue + '}';
    }
}
