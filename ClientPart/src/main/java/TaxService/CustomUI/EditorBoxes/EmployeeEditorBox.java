package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.Department;
import TaxService.DAOs.Education;
import TaxService.DAOs.Employee;
import TaxService.DAOs.Post;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.Date;

public class EmployeeEditorBox //extends AbstractEditorBox<Employee>
{
	private TextField surname1, surname2;
	private TextField name1, name2;
	private TextField patronymic1, patronymic2;
	private ComboBox<Department> department1, department2;
	private DatePicker birthdate1, birthdate2;
	private ComboBox<Post> post1, post2;
	private TextField salary1, salary2;
	private ComboBox<Education> education1, education2;
	
	/*public EmployeeEditorBox()
	{
		super(Employee.class);

		surname1 = new TextField();
		surname1.setPrefWidth(150);
		surname2 = new TextField();
		surname2.setPrefWidth(150);
		addField("Фамилия", surname1, surname2);

		name1 = new TextField();
		name1.setPrefWidth(150);
		name2 = new TextField();
		name2.setPrefWidth(150);
		addField("Имя", name1, name2);

		patronymic1 = new TextField();
		patronymic1.setPrefWidth(150);
		patronymic2 = new TextField();
		patronymic2.setPrefWidth(150);
		addField("Отчество", patronymic1, patronymic2);

		department1 = new ComboBox<>();
		department1.setPrefWidth(150);
		department1.setEditable(true);
		department2 = new ComboBox<>();
		department2.setPrefWidth(150);
		department2.setEditable(true);
		addField("Отделение", department1, department2);

		birthdate1 = new DatePicker();
		birthdate1.setPrefWidth(120);
		birthdate2 = new DatePicker();
		birthdate2.setPrefWidth(120);
		addField("Дата рождения", birthdate1, birthdate2);
		
		post1 = new ComboBox<>();
		post1.setPrefWidth(200);
		post1.setEditable(true);
		post2 = new ComboBox<>();
		post2.setPrefWidth(200);
		post2.setEditable(true);
		addField("Должность", post1, post2);

		salary1 = new TextField();
		salary1.setPrefWidth(100);
		salary2 = new TextField();
		salary2.setPrefWidth(100);
		addField("Зарплата", salary1, salary2);

		education1 = new ComboBox<>();
		education1.setPrefWidth(150);
		education1.setEditable(true);
		education2 = new ComboBox<>();
		education2.setPrefWidth(150);
		education2.setEditable(true);
		addField("Образование", education1, education2);
	}*/
}
