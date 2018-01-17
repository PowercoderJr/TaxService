package TaxService.Orders;

import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.Account;
import TaxService.DAOs.Employee;

import java.io.Serializable;

public class CreateEmployeePlusAccountOrder implements Serializable
{
	private Employee employee;
	private Account account;

	public CreateEmployeePlusAccountOrder(Employee employee, Account account)
	{
		this.employee = employee;
		this.account = account;
	}

	public Employee getEmployee()
	{
		return employee;
	}

	public Account getAccount()
	{
		return account;
	}

	@Override
	public String toString()
	{
		return "CreateEmployeePlusAccountOrder with " + employee + " as " + account.getLogin();
	}
}
