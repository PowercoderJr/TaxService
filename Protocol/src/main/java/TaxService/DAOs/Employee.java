package TaxService.DAOs;

import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Employee extends AbstractDAO
{
	public String surname;
	public String name;
	public String patronymic;
	public Department department;
	public Date birthdate;
	public Post post;
	public int salary;
	public Education education;

	static
	{
		try
		{
			readEvenIfLazy.put(Employee.class, new Field[] {Employee.class.getField("id"),
															Employee.class.getField("surname"),
															Employee.class.getField("name"),
															Employee.class.getField("patronymic")});
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	public static final void init(){}

	public Employee()
	{
		super();
	}

	public Employee(String surname, String name, String patronymic, Department department, Date birthdate, Post post, int salary, Education education)
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

	public Post getPost()
	{
		return post;
	}

	public void setPost(Post post)
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
		//return "Employee{" + "id=" + id + ", surname='" + surname + '\'' + ", name='" + name + '\'' + ", patronymic='" + patronymic + '\'' + ", department=" + department + ", birthdate=" + birthdate + ", post='" + post + '\'' + ", salary=" + salary + ", education=" + education + '}';
		return surname + " " + name.charAt(0) + ". " + patronymic.charAt(0) + "." + " - #" + id;
	}
}
