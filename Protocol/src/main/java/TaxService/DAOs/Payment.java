package TaxService.DAOs;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "Payment")
public class Payment extends AbstractDAO
{
    private static final long serialVersionID = 666000123210004L;

	//id SERIAL NOT NULL
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //paytype_id INTEGER NOT NULL REFERENCES paytypes(id)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Paytype", nullable = false)
	private Paytype paytype;

    //date DATE NOT NULL
	@Column(name = "date", nullable = false)
	private Date date;

    //amount NUMERIC(12, 2) NOT NULL
	@Column(name = "amount", precision = 12, scale = 2, nullable = false)
	private BigDecimal amount;

    //emp_id INTEGER NOT NULL REFERENCES employees(id)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Employee", nullable = false)
	private Employee employee;

    //dep_id INTEGER NOT NULL REFERENCES departments(id)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Department", nullable = false)
	private Department department;

    //company_id INTEGER NOT NULL REFERENCES companies(id)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Company", nullable = false)
	private Company company;

	public Payment(Paytype paytype, Date date, BigDecimal amount, Employee employee, Department department, Company company)
	{
		this.paytype = paytype;
		this.date = date;
		this.amount = amount;
		this.employee = employee;
		this.department = department;
		this.company = company;
	}

	public Payment()
	{
		;
	}

	public long getId()
	{
		return id;
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
