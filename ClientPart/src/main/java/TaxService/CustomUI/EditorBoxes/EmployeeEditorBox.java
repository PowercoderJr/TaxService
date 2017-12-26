package TaxService.CustomUI.EditorBoxes;

import TaxService.Callback;
import TaxService.DAOs.Department;
import TaxService.DAOs.Education;
import TaxService.DAOs.Employee;
import TaxService.DAOs.Post;
import TaxService.Deliveries.AllDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadAllOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeeEditorBox extends AbstractEditorBox<Employee>
{
	private TextField surname1, surname2;
	private TextField name1, name2;
	private TextField patronymic1, patronymic2;
	private ComboBox<Department> department1, department2;
	private DatePicker birthdate1, birthdate2;
	private ComboBox<Post> post1, post2;
	private TextField salary1, salary2;
	private ComboBox<Education> education1, education2;
	
	public EmployeeEditorBox()
	{
		super(Employee.class);

		surname1 = new TextField();
		surname1.setPrefWidth(150);
		surname2 = new TextField();
		surname2.setPrefWidth(150);
		addField("Фамилия", surname1, surname2, false);

		name1 = new TextField();
		name1.setPrefWidth(150);
		name2 = new TextField();
		name2.setPrefWidth(150);
		addField("Имя", name1, name2, false);

		patronymic1 = new TextField();
		patronymic1.setPrefWidth(150);
		patronymic2 = new TextField();
		patronymic2.setPrefWidth(150);
		addField("Отчество", patronymic1, patronymic2, false);

		department1 = new ComboBox<>();
		department1.setPrefWidth(150);
		department1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Department>(Department.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		department2 = new ComboBox<>();
		department2.setPrefWidth(150);
		department2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Department>(Department.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Отделение", department1, department2, false);

		birthdate1 = new DatePicker();
		birthdate1.setPrefWidth(140);
		birthdate2 = new DatePicker();
		birthdate2.setPrefWidth(140);
		addField("Дата рождения", birthdate1, birthdate2, false);
		
		post1 = new ComboBox<>();
		post1.setPrefWidth(200);
		post1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Post>(Post.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		post2 = new ComboBox<>();
		post2.setPrefWidth(200);
		post2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Post>(Post.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Должность", post1, post2, false);

		salary1 = new TextField();
		salary1.setPrefWidth(100);
		salary2 = new TextField();
		salary2.setPrefWidth(100);
		addField("Зарплата", salary1, salary2, false);

		education1 = new ComboBox<>();
		education1.setPrefWidth(150);
		education1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Education>(Education.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		education2 = new ComboBox<>();
		education2.setPrefWidth(150);
		education2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Education>(Education.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Образование", education1, education2, false);

		ClientAgent.subscribeAllReceived(o ->
		{
			AllDelivery delivery = (AllDelivery) o;

			if (delivery.getPurpose() == ReadAllOrder.Purposes.REFRESH_CB )
			{
				if (delivery.getContentClazz() == Department.class)
					Platform.runLater(() ->
					{
						if (department1.isShowing())
						{
							department1.getSelectionModel().clearSelection();
							department1.setItems(FXCollections.observableList(delivery.getContent()));
						}
						else if (department2.isShowing())
						{
							department2.getSelectionModel().clearSelection();
							department2.setItems(FXCollections.observableList(delivery.getContent()));
						}
					});
				else if (delivery.getContentClazz() == Post.class)
					Platform.runLater(() ->
					{
						if (post1.isShowing())
						{
							post1.getSelectionModel().clearSelection();
							post1.setItems(FXCollections.observableList(delivery.getContent()));
						}
						else if (post2.isShowing())
						{
							post2.getSelectionModel().clearSelection();
							post2.setItems(FXCollections.observableList(delivery.getContent()));
						}
					});
				else if (delivery.getContentClazz() == Education.class)
					Platform.runLater(() ->
					{
						if (education1.isShowing())
						{
							education1.getSelectionModel().clearSelection();
							education1.setItems(FXCollections.observableList(delivery.getContent()));
						}
						else if (education2.isShowing())
						{
							education2.getSelectionModel().clearSelection();
							education2.setItems(FXCollections.observableList(delivery.getContent()));
						}
					});
			}
		});
	}

	@Override
	public Employee withdrawPrimaryAll()
	{
		String surname = surname1.getText().trim();
		String name = name1.getText().trim();
		String patronymic = patronymic1.getText().trim();
		Department department = department1.getSelectionModel().getSelectedItem();
		Date birthdate = Date.valueOf(birthdate1.getValue());
		Post post = post1.getSelectionModel().getSelectedItem();
		int salary = Integer.parseInt(salary1.getText().trim());
		Education education = education1.getSelectionModel().getSelectedItem();
		return new Employee(surname, name, patronymic, department, birthdate, post, salary, education);
	}

	@Override
	public Pair<Employee, List<Field>> withdrawPrimaryFilled()
	{
		try
		{
			List<Field> filledFields = new ArrayList<>();

			long id;
			if (id1.getText().trim().isEmpty())
				id = 0;
			else
			{
				id = Integer.parseInt(id1.getText().trim());
				filledFields.add(clazz.getField("id"));
			}

			String surname;
			if (surname1.getText().trim().isEmpty())
				surname = null;
			else
			{
				surname = surname1.getText().trim();
				filledFields.add(clazz.getField("surname"));
			}

			String name;
			if (name1.getText().trim().isEmpty())
				name = null;
			else
			{
				name = name1.getText().trim();
				filledFields.add(clazz.getField("name"));
			}

			String patronymic;
			if (patronymic1.getText().trim().isEmpty())
				patronymic = null;
			else
			{
				patronymic = patronymic1.getText().trim();
				filledFields.add(clazz.getField("patronymic"));
			}

			Department department;
			if (department1.getSelectionModel().isEmpty())
				department = null;
			else
			{
				department = department1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("department"));
			}

			Date birthdate;
			if (birthdate1.getValue() == null)
				birthdate = null;
			else
			{
				birthdate = Date.valueOf(birthdate1.getValue());
				filledFields.add(clazz.getField("birthdate"));
			}

			Post post;
			if (post1.getSelectionModel().isEmpty())
				post = null;
			else
			{
				post = post1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("post"));
			}

			int salary;
			if (salary1.getText().trim().isEmpty())
				salary = 0;
			else
			{
				salary = Integer.parseInt(salary1.getText().trim());
				filledFields.add(clazz.getField("salary"));
			}

			Education education;
			if (education1.getSelectionModel().isEmpty())
				education = null;
			else
			{
				education = education1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("education"));
			}

			Employee dao = new Employee(surname, name, patronymic, department, birthdate, post, salary, education);
			dao.id = id;
			return new Pair<>(dao, filledFields);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Pair<Employee, List<Field>> withdrawSecondaryFilled()
	{
		try
		{
			List<Field> filledFields = new ArrayList<>();

			String surname;
			if (surname2.getText().trim().isEmpty())
				surname = null;
			else
			{
				surname = surname2.getText().trim();
				filledFields.add(clazz.getField("surname"));
			}

			String name;
			if (name2.getText().trim().isEmpty())
				name = null;
			else
			{
				name = name2.getText().trim();
				filledFields.add(clazz.getField("name"));
			}

			String patronymic;
			if (patronymic2.getText().trim().isEmpty())
				patronymic = null;
			else
			{
				patronymic = patronymic2.getText().trim();
				filledFields.add(clazz.getField("patronymic"));
			}

			Department department;
			if (department2.getSelectionModel().isEmpty())
				department = null;
			else
			{
				department = department2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("department"));
			}

			Date birthdate;
			if (birthdate2.getValue() == null)
				birthdate = null;
			else
			{
				birthdate = Date.valueOf(birthdate2.getValue());
				filledFields.add(clazz.getField("birthdate"));
			}

			Post post;
			if (post2.getSelectionModel().isEmpty())
				post = null;
			else
			{
				post = post2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("post"));
			}

			int salary;
			if (salary2.getText().trim().isEmpty())
				salary = 0;
			else
			{
				salary = Integer.parseInt(salary2.getText().trim());
				filledFields.add(clazz.getField("salary"));
			}

			Education education;
			if (education2.getSelectionModel().isEmpty())
				education = null;
			else
			{
				education = education2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("education"));
			}

			return new Pair<>(new Employee(surname, name, patronymic, department, birthdate, post, salary, education), filledFields);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	protected boolean validateBirthdate(DatePicker field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getValue() == null ||
				field.getValue() != null && field.getValue().isBefore(ChronoLocalDate.from(LocalDateTime.now()));
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	@Override
	public boolean validatePrimary(boolean allRequired)
	{
		return validateTextField(surname1, allRequired) &
				validateTextField(name1, allRequired) &
				validateTextField(patronymic1, allRequired) &
				validateComboBox(department1, allRequired) &
				validateBirthdate(birthdate1, allRequired) &
				validateComboBox(post1, allRequired) &
				validateTextPositiveIntField(salary1, allRequired) &
				validateComboBox(education1, allRequired);
	}

	@Override
	public boolean validateSecondary(boolean allRequired)
	{
		return validateTextField(surname2, allRequired) &
				validateTextField(name2, allRequired) &
				validateTextField(patronymic2, allRequired) &
				validateComboBox(department2, allRequired) &
				validateBirthdate(birthdate2, allRequired) &
				validateComboBox(post2, allRequired) &
				validateTextPositiveIntField(salary2, allRequired) &
				validateComboBox(education2, allRequired);
	}

	@Override
	public int countFilledPrimary()
	{
		int count = 0;
		if (!id1.getText().trim().isEmpty())
			++count;
		if (!surname1.getText().trim().isEmpty())
				++count;
		if (!name1.getText().trim().isEmpty())
				++count;
		if (!patronymic1.getText().trim().isEmpty())
				++count;
		if (!department1.getSelectionModel().isEmpty())
				++count;
		if (birthdate1.getValue() != null)
				++count;
		if (!post1.getSelectionModel().isEmpty())
				++count;
		if (!salary1.getText().trim().isEmpty())
				++count;
		if (!education1.getSelectionModel().isEmpty())
				++count;
		return count;
	}

	@Override
	public int countFilledSecondary()
	{
		int count = 0;
		if (!surname2.getText().trim().isEmpty())
			++count;
		if (!name2.getText().trim().isEmpty())
			++count;
		if (!patronymic2.getText().trim().isEmpty())
			++count;
		if (!department2.getSelectionModel().isEmpty())
			++count;
		if (birthdate2.getValue() != null)
			++count;
		if (!post2.getSelectionModel().isEmpty())
			++count;
		if (!salary2.getText().trim().isEmpty())
			++count;
		if (!education2.getSelectionModel().isEmpty())
			++count;
		return count;
	}

	@Override
	public void clearAll()
	{
		id1.clear();
		id1.setEffect(null);
		surname1.clear();
		surname1.setEffect(null);
		name1.clear();
		name1.setEffect(null);
		patronymic1.clear();
		patronymic1.setEffect(null);
		department1.getSelectionModel().clearSelection();
		department1.setEffect(null);
		birthdate1.getEditor().clear();
		birthdate1.setValue(null);
		birthdate1.setEffect(null);
		post1.getSelectionModel().clearSelection();
		post1.setEffect(null);
		salary1.clear();
		salary1.setEffect(null);
		education1.getSelectionModel().clearSelection();
		education1.setEffect(null);
		surname2.clear();
		surname2.setEffect(null);
		name2.clear();
		name2.setEffect(null);
		patronymic2.clear();
		patronymic2.setEffect(null);
		department2.getSelectionModel().clearSelection();
		department2.setEffect(null);
		birthdate2.getEditor().clear();
		birthdate2.setValue(null);
		birthdate2.setEffect(null);
		post2.getSelectionModel().clearSelection();
		post2.setEffect(null);
		salary2.clear();
		salary2.setEffect(null);
		education2.getSelectionModel().clearSelection();
		education2.setEffect(null);
	}
}
