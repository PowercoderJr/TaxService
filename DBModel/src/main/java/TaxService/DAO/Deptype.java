package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "Deptype")
public class Deptype implements Serializable
{
    private static final long serialVersionID = 666000123210005L;

    //id SERIAL NOT NULL
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //name VARCHAR(40) NOT NULL
    @Column(name = "name", length = 40, unique = true, nullable = false)
    private String name;

	@OneToMany(mappedBy = "deptype", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Department> departments;

	public Deptype(String name)
	{
		this.name = name;
	}

	public Deptype()
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
		return "Deptype{" + "id=" + id + ", name='" + name + '\'' + '}';
	}
}
