package TaxService.DAOs;

import TaxService.POJO;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Paytype")
public class Paytype extends POJO
{
    private static final long serialVersionID = 666000123210007L;

    //id SERIAL NOT NULL
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //name VARCHAR(100) NOT NULL
    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "paytype", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    public Paytype(String name)
    {
        this.name = name;
    }

    public Paytype()
    {
        ;
    }

    public long getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return "Paytype{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
