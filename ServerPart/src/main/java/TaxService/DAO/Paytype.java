package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "Paytype")
public class Paytype implements Serializable
{
    private static final long serialVersionID = 666000123210007L;

    //id SERIAL NOT NULL
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //name VARCHAR(40) NOT NULL
    @Column(name = "name", length = 40, unique = true, nullable = false)
    private String name;

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
