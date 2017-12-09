package TaxService.DAOs;

import TaxService.POJO;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "Employee")
public class Employee extends POJO
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department", nullable = false)
	private Department department;

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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "Education", nullable = false)
	private Education education;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Payment> payments;

	public Employee(String surname, String name, String patronymic, Department department, Date birthdate, String post, int salary, Education education)
	{
		this.surname = surname;
		this.name = name;
		this.patronymic = patronymic;
		this.department = department;
		this.birthdate = birthdate;
		this.post = post;
		this.salary = salary;
		this.education = education;
	}

	public Employee()
	{
		;
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

	public Department getDepartment()
	{
		return department;
	}

	public void setDepartment(Department department)
	{
		this.department = department;
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

	public Education getEducation()
	{
		return education;
	}

	public void setEducation(Education education)
	{
		this.education = education;
	}

	@Override
	public String toString()
	{
		return "Employee{" + "id=" + id + ", surname='" + surname + '\'' + ", name='" + name + '\'' + ", patronymic='" + patronymic + '\'' + ", department=" + department + ", birthdate=" + birthdate + ", post='" + post + '\'' + ", salary=" + salary + ", education=" + education + '}';
	}
}
