package TaxService.DAOs;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class Department extends AbstractDAO
{
	public Deptype deptype;
	public String name;
	public BigDecimal startyear;
	public String phone;
	public City city;
	public String street;
	public String house;

	static
	{
		try
		{
			readEvenIfLazy.put(Department.class, new Field[] {Department.class.getField("id"),
															  Department.class.getField("name")});
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	public static final void init(){}

	public Department()
	{
		super();
	}

	public Department(String name, Deptype deptype, BigDecimal startyear, String phone, City city, String street, String house)
	{
		this.name = name;
		this.deptype = deptype;
		this.startyear = startyear;
		this.phone = phone;
		this.city = city;
		this.street = street;
		this.house = house;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
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

	public BigDecimal getStartyear()
	{
		return startyear;
	}

	public void setStartyear(BigDecimal startyear)
	{
		this.startyear = startyear;
	}

	public String getPhone()
	{
		return phone;
	}

	public void setPhone(String phone)
	{
		this.phone = phone;
	}

	public City getCity()
	{
		return city;
	}

	public void setCity(City city)
	{
		this.city = city;
	}

	public String getStreet()
	{
		return street;
	}

	public void setStreet(String street)
	{
		this.street = street;
	}

	public String getHouse()
	{
		return house;
	}

	public void setHouse(String house)
	{
		this.house = house;
	}

	@Override
	public String toString()
	{
		//return "Department{" + "id=" + id + ", deptype=" + deptype + ", name='" + name + '\'' + ", startyear=" + startyear + ", phone='" + phone + '\'' + ", city='" + city + '\'' + ", street='" + street + '\'' + ", house='" + house + '\'' + '}';
		return name + " - #" + id;
	}
}
