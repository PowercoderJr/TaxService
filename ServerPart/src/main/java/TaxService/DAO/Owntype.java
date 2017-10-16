package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "Owntype")
public class Owntype implements Serializable
{
    private static final long serialVersionID = 666000123210008L;

    //id SERIAL NOT NULL
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //name VARCHAR(40) NOT NULL
    @Column(name = "name", length = 40, nullable = false)
    private String name;

    public Owntype()
    {
        ;
    }

    public Owntype(String name)
    {
        this.name = name;
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
}
