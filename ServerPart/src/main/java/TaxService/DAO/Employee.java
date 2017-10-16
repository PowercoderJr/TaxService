package TaxService.DAO;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "Employee")
//@SequenceGenerator(name = "EmployeeSqGen", allocationSize = 1000000)
public class Employee implements Serializable
{
    private static final long serialVersionID = 666000123210002L;

    //id SERIAL NOT NULL
    @Id
    @Column(name = "id", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    //surname VARCHAR(30) NOT NULL
	@Column(name = "surname", length = 30, nullable = false)
	private String surname;

    //name VARCHAR(30) NOT NULL
	@Column(name = "name", length = 30, nullable = false)
	private String name;

	//patronymic VARCHAR(30) NOT NULL
	@Column(name = "patronymic", length = 30, nullable = false)
	private String patronymic;

	//dep_id INTEGER NOT NULL REFERENCES departments(id)
	//@Column (name = "dep_id", nullable = false)
	//private long dep_id

    //birthdate DATE NOT NULL
	@Column(name = "birthdate", nullable = false)
	private Date birthdate;

    //post VARCHAR(50) NOT NULL
	@Column (name = "post", length = 50, nullable = false)
	private String post;

	//salary INTEGER NOT NULL
	@Column(name = "salary", nullable = false)
	private int salary;

    //edu_id INTEGER NOT NULL REFERENCES educations(id)
	//@Column (name = "edu_id", nullable = false)
	//private long edu_id

	public Employee()
	{
		;
	}

	public Employee(String surname, String name, String patronymic, Date birthdate, String post, int salary)
	{
		this.surname = surname;
		this.name = name;
		this.patronymic = patronymic;
		this.birthdate = birthdate;
		this.post = post;
		this.salary = salary;
	}

	public long getId()
	{
		return id;
	}

	public String getSurname()
	{
		return surname;
	}

	public void setSurname(String surname)
	{
		this.surname = surname;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPatronymic()
	{
		return patronymic;
	}

	public void setPatronymic(String patronymic)
	{
		this.patronymic = patronymic;
	}

	public Date getBirthdate()
	{
		return birthdate;
	}

	public void setBirthdate(Date birthdate)
	{
		this.birthdate = birthdate;
	}

	public String getPost()
	{
		return post;
	}

	public void setPost(String post)
	{
		this.post = post;
	}

	public int getSalary()
	{
		return salary;
	}

	public void setSalary(int salary)
	{
		this.salary = salary;
	}

	@Override
	public String toString()
	{
		return "Employee{" + "id=" + id + ", surname='" + surname + '\'' + ", name='" + name + '\'' + ", patronymic='" + patronymic + '\'' + ", birthdate=" + birthdate + ", post='" + post + '\'' + ", salary=" + salary + '}';
	}
}
