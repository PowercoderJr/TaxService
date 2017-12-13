package TaxService.DAOs;

import java.math.BigDecimal;

public class Company extends AbstractDAO
{
    private static final long serialVersionID = 666000123210003L;

    public String name;
	public Owntype owntype;
	public String phone;
	public BigDecimal startyear;
	public int statesize;

	public Company(String name, Owntype owntype, String phone, BigDecimal startyear, int statesize)
	{
		this.name = name;
		this.owntype = owntype;
		this.phone = phone;
		this.startyear = startyear;
		this.statesize = statesize;
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
