package TaxService.DAOs;

import java.math.BigDecimal;
import java.sql.Date;

public class Payment extends AbstractDAO
{
    public static final long serialVersionID = 666000123210004L;
	public Paytype paytype;
	public Date date;
	public BigDecimal amount;
	public Employee employee;
	public Department department;
	public Company company;

	public Payment(Paytype paytype, Date date, BigDecimal amount, Employee employee, Department department, Company company)
	{
		this.paytype = paytype;
		this.date = date;
		this.amount = amount;
		this.employee = employee;
		this.department = department;
		this.company = company;
	}

	public Paytype getPaytype()
	{
		return paytype;
	}

	public void setPaytype(Paytype paytype)
	{
		this.paytype = paytype;
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public BigDecimal getAmount()
	{
		return amount;
	}

	public void setAmount(BigDecimal amount)
	{
		this.amount = amount;
	}

	public Employee getEmployee()
	{
		return employee;
	}

	public void setEmployee(Employee employee)
	{
		this.employee = employee;
	}

	public Department getDepartment()
	{
		return department;
	}

	public void setDepartment(Department department)
	{
		this.department = department;
	}

	public Company getCompany()
	{
		return company;
	}

	public void setCompany(Company company)
	{
		this.company = company;
	}

	@Override
	public String toString()
	{
		return "Payment{" + "id=" + id + ", paytype=" + paytype + ", date=" + date + ", amount=" + amount + ", employee=" + employee + ", department=" + department + ", company=" + company + '}';
	}
}
