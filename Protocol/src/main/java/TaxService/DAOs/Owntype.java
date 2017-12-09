package TaxService.DAOs;

import TaxService.POJO;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Owntype")
public class Owntype extends POJO
{
    private static final long serialVersionID = 666000123210008L;

    //id SERIAL NOT NULL
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //name VARCHAR(100) NOT NULL
    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String name;

    @OneToMany(mappedBy = "owntype", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Company> companies;

    public Owntype(String name)
    {
        this.name = name;
    }

    public Owntype()
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
        return "Owntype{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
