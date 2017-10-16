package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Department")
//@SequenceGenerator(name = "DepartmentSqGen", allocationSize = 1000000)
public class Department implements Serializable
{
    private static final long serialVersionID = 666000123210001L;

    //id SERIAL NOT NULL
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //deptype_id INTEGER NOT NULL REFERENCES deptypes(id)
    //@Column(name = "deptype_id", nullable = false)
    //private long deptype_id

    //name VARCHAR(100) NOT NULL
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    //startyear SMALLINT NOT NULL
    @Column(name = "startyear", nullable = false)
    private short startyear;

    //phone VARCHAR(13) NOT NULL
    @Column(name = "phone", length = 13, nullable = false)
    private String phone;

    //city VARCHAR(30) NOT NULL
    @Column(name = "city", length = 30, nullable = false)
    private String city;

    //street VARCHAR(30) NOT NULL
    @Column(name = "street", length = 30, nullable = false)
    private String street;

    //house VARCHAR(6) NOT NULL
    @Column(name = "house", length = 6, nullable = false)
    private String house;


    public Department()
    {
        ;
    }

    public Department(String name, short startyear, String phone, String city, String street, String house)
    {
        this.name = name;
        this.startyear = startyear;
        this.phone = phone;
        this.city = city;
        this.street = street;
        this.house = house;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getStartyear() {
        return startyear;
    }

    public void setStartyear(short startyear) {
        this.startyear = startyear;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    @Override
    public String toString() {
        return "Department{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startyear=" + startyear +
                ", phone='" + phone + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                '}';
    }
}
