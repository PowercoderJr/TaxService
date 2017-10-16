package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table (name = "Company")
public class Company implements Serializable
{
    private static final long serialVersionID = 666000123210003L;

    //id SERIAL NOT NULL
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //name VARCHAR(50) NOT NULL
    @Column(name = "name", length = 50, nullable = false)
    private String name;

    //owntype_id INTEGER NOT NULL REFERENCES owntypes(id)
	//@Column (name = "owntype_id", nullable = false)
	//private long owntype_id

    //phone VARCHAR(13) NOT NULL
	@Column(name = "phone", length = 13, nullable = false)
	private String phone;

    //startyear SMALLINT NOT NULL
	@Column(name = "startyear", nullable = false)
	private short startyear;

    //statesize INTEGER NOT NULL
	@Column(name = "statesize", nullable = false)
	private int statesize;

	public Company()
	{
		;
	}

	public Company(String name, String phone, short startyear, int statesize)
	{
		this.name = name;
		this.phone = phone;
		this.startyear = startyear;
		this.statesize = statesize;
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

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public short getStartyear()
	{
		return startyear;
	}

	public void setStartyear(short startyear)
	{
		this.startyear = startyear;
	}

	public int getStatesize()
	{
		return statesize;
	}

	public void setStatesize(int statesize)
	{
		this.statesize = statesize;
	}

	@Override
	public String toString()
	{
		return "Company{" + "id=" + id + ", name='" + name + '\'' + ", phone='" + phone + '\'' + ", startyear=" + startyear + ", statesize=" + statesize + '}';
	}
}
