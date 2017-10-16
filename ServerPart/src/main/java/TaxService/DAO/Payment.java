package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table (name = "Payment")
public class Payment implements Serializable
{
    private static final long serialVersionID = 666000123210004L;

	//id SERIAL NOT NULL
    @Id
    @Column (name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //paytype_id INTEGER NOT NULL REFERENCES paytypes(id)
    //@Column(name = "deptype_id", nullable = false)
    //private long deptype_id

    //date DATE NOT NULL
	@Column(name = "date", nullable = false)
	private Date date;

    //amount NUMERIC(12, 2) NOT NULL
	@Column(name = "amount", precision = 12, scale = 2, nullable = false)
	private BigDecimal amount;

    //emp_id INTEGER NOT NULL REFERENCES employees(id)
    //@Column(name = "deptype_id", nullable = false)
    //private long deptype_id

    //dep_id INTEGER NOT NULL REFERENCES departments(id)
    //@Column(name = "deptype_id", nullable = false)
    //private long deptype_id

    //company_id INTEGER NOT NULL REFERENCES companies(id)
    //@Column(name = "deptype_id", nullable = false)
    //private long deptype_id


	public Payment()
	{
		;
	}

	public Payment(Date date, BigDecimal amount)
	{
		this.date = date;
		this.amount = amount;
	}

	public long getId()
	{
		return id;
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

	@Override
	public String toString()
	{
		return "Payment{" + "id=" + id + ", date=" + date + ", amount=" + amount + '}';
	}
}
