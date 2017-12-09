package TaxService.DAOs;

import TaxService.POJO;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "Department")
public class Department extends POJO
{
    private static final long serialVersionID = 666000123210001L;

    //id SERIAL NOT NULL
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //deptype_id INTEGER NOT NULL REFERENCES deptypes(id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Deptype", nullable = false)
    private Deptype deptype;

    //name VARCHAR(100) NOT NULL
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    //startyear SMALLINT NOT NULL
    @Column(name = "startyear", precision = 4, scale = 0, nullable = false)
    private BigDecimal startyear;

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

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees;

	@OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Payment> payments;

    public Department(String name, Deptype deptype, BigDecimal startyear, String phone, String city, String street, String house)
    {
        this.name = name;
        this.deptype = deptype;
        this.startyear = startyear;
        this.phone = phone;
        this.city = city;
        this.street = street;
        this.house = house;
    }

    public Department()
    {
        ;
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

    public Deptype getDeptype()
    {
        return deptype;
    }

    public void setDeptype(Deptype deptype)
    {
        this.deptype = deptype;
    }

    public BigDecimal getStartyear() {
        return startyear;
    }

    public void setStartyear(BigDecimal startyear) {
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
    public String toString()
    {
        return "Department{" + "id=" + id + ", deptype=" + deptype + ", name='" + name + '\'' + ", startyear=" + startyear + ", phone='" + phone + '\'' + ", city='" + city + '\'' + ", street='" + street + '\'' + ", house='" + house + '\'' + '}';
    }
}
