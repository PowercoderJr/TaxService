package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Owntype", nullable = false)
	private Owntype owntype;

    //phone VARCHAR(13) NOT NULL
	@Column(name = "phone", length = 13, nullable = false)
	private String phone;

    //startyear SMALLINT NOT NULL
	@Column(name = "startyear", precision = 4, scale = 0, nullable = false)
	private BigDecimal startyear;

    //statesize INTEGER NOT NULL
	@Column(name = "statesize", nullable = false)
	private int statesize;

	public Company(String name, Owntype owntype, String phone, BigDecimal startyear, int statesize)
	{
		this.name = name;
		this.owntype = owntype;
		this.phone = phone;
		this.startyear = startyear;
		this.statesize = statesize;
	}

	public Company()
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

	public Owntype getOwntype()
	{
		return owntype;
	}

	public void setOwntype(Owntype owntype)
	{
		this.owntype = owntype;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public BigDecimal getStartyear()
	{
		return startyear;
	}

	public void setStartyear(BigDecimal startyear)
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
		return "Company{" + "id=" + id + ", name='" + name + '\'' + ", owntype=" + owntype + ", phone='" + phone + '\'' + ", startyear=" + startyear + ", statesize=" + statesize + '}';
	}
}
