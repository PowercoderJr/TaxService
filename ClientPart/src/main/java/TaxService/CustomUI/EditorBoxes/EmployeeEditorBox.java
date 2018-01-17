package TaxService.CustomUI.EditorBoxes;

import TaxService.DAOs.*;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateEmployeePlusAccountOrder;
import TaxService.Orders.ReadAllOrder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

	private ToggleButton toggleAccBtn;
	private VBox accountBox;
	private TextField login;
	private PasswordField pass, passConf;
	private ComboBox<Account.Roles> role;
	private CheckBox blocked;
	
	public EmployeeEditorBox()
	{
		super(Employee.class);

		surname1 = new TextField();
		setLengthLimit(surname1, 30);
		surname2 = new TextField();
		setLengthLimit(surname2, 30);
		addField("Фамилия", surname1, surname2, false);

		name1 = new TextField();
		setLengthLimit(name1, 30);
		name2 = new TextField();
		setLengthLimit(name2, 30);
		addField("Имя", name1, name2, false);

		patronymic1 = new TextField();
		setLengthLimit(patronymic1, 30);
		patronymic2 = new TextField();
		setLengthLimit(patronymic2, 30);
		addField("Отчество", patronymic1, patronymic2, false);

		department1 = new ComboBox<>();
		department1.setOnShowing(event ->
		{
			department1.getSelectionModel().clearSelection();
			department1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Department>(Department.class, true, null));
		});
		department2 = new ComboBox<>();
		department2.setOnShowing(event ->
		{
			department2.getSelectionModel().clearSelection();
			department2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Department>(Department.class, true, null));
		});
		addField("Отделение", department1, department2, false);

		birthdate1 = new DatePicker();
		birthdate2 = new DatePicker();
		addField("Дата рождения", birthdate1, birthdate2, false);
		
		post1 = new ComboBox<>();
		post1.setOnShowing(event ->
		{
			post1.getSelectionModel().clearSelection();
			post1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Post>(Post.class, true, null));
		});
		post2 = new ComboBox<>();
		post2.setOnShowing(event ->
		{
			post2.getSelectionModel().clearSelection();
			post2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Post>(Post.class, true, null));
		});
		addField("Должность", post1, post2, false);

		salary1 = new TextField();
		salary2 = new TextField();
		addField("Зарплата", salary1, salary2, false);

		education1 = new ComboBox<>();
		education1.setOnShowing(event ->
		{
			education1.getSelectionModel().clearSelection();
			education1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Education>(Education.class, true, null));
		});
		education2 = new ComboBox<>();
		education2.setOnShowing(event ->
		{
			education2.getSelectionModel().clearSelection();
			education2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Education>(Education.class, true, null));
		});
		addField("Образование", education1, education2, false);

		////

		accountBox = new VBox(SPACING);
		accountBox.setAlignment(Pos.TOP_CENTER);
		accountBox.setVisible(false);
		accountBox.setManaged(false);

		login = new TextField();
		setLengthLimit(login, 100);
		addFieldToAccountBox("Логин", login);
		pass = new PasswordField();
		setLengthLimit(pass, 100);
		addFieldToAccountBox("Пароль", pass);
		passConf = new PasswordField();
		setLengthLimit(passConf, 100);
		addFieldToAccountBox("Ещё раз", passConf);
		role = new ComboBox<>(FXCollections.observableArrayList(Account.Roles.values()));
		addFieldToAccountBox("Роль", role);
		blocked = new CheckBox();
		addFieldToAccountBox("Заблокирован", blocked);

		toggleAccBtn = new ToggleButton("При добавлении также создать аккаунт");
		toggleAccBtn.setWrapText(true);
		toggleAccBtn.setMaxWidth(Double.MAX_VALUE);
		VBox.setMargin(toggleAccBtn, new Insets(10, 0, 0, 0));
		toggleAccBtn.setOnAction(new EventHandler<ActionEvent>()
		{
			@Override
			public void handle(ActionEvent event)
			{
				accountBox.setVisible(toggleAccBtn.isSelected());
				accountBox.setManaged(toggleAccBtn.isSelected());
			}
		});

		highBox.getChildren().addAll(toggleAccBtn, accountBox);
	}

	protected void addFieldToAccountBox(String name, Control primary)
	{
		primary.setPrefWidth(200);
		VBox primaryBox = new VBox(SPACING);
		Label label1 = new Label(name);
		primaryBox.getChildren().addAll(label1, primary);
		accountBox.getChildren().add(primaryBox);
		primary.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
					primary.setEffect(null);
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

	public Pair<Employee, Account> withdrawPrimaryAllPlusAccount()
	{
		String surname = surname1.getText().trim();
		String name = name1.getText().trim();
		String patronymic = patronymic1.getText().trim();
		Department department = department1.getSelectionModel().getSelectedItem();
		Date birthdate = Date.valueOf(birthdate1.getValue());
		Post post = post1.getSelectionModel().getSelectedItem();
		int salary = Integer.parseInt(salary1.getText().trim());
		Education education = education1.getSelectionModel().getSelectedItem();
		Employee employee = new Employee(surname, name, patronymic, department, birthdate, post, salary, education);

		String login = this.login.getText();
		String pass = this.pass.getText();
		Account.Roles role = this.role.getSelectionModel().getSelectedItem();
		boolean blocked = this.blocked.isSelected();
		Account account = new Account(login, pass, null, role, blocked);

		return new Pair<>(employee, account);
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

	public boolean validatePrimaryPlusAccount(boolean allRequired)
	{
		if (!pass.getText().equals(passConf.getText()))
		{
			markAsInvalid(pass);
			markAsInvalid(passConf);
		}

		return validateTextField(surname1, allRequired) &
				validateTextField(name1, allRequired) &
				validateTextField(patronymic1, allRequired) &
				validateComboBox(department1, allRequired) &
				validateBirthdate(birthdate1, allRequired) &
				validateComboBox(post1, allRequired) &
				validateTextPositiveIntField(salary1, allRequired) &
				validateComboBox(education1, allRequired) &

				validateTextField(login, allRequired) &
				validateTextField(pass, allRequired) &
				validateTextField(passConf, allRequired) &
				pass.getText().equals(passConf.getText()) &
				validateComboBox(role, allRequired);
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
		department1.getEditor().clear();
		department2.setValue(null);
		department1.setEffect(null);
		birthdate1.getEditor().clear();
		birthdate1.setValue(null);
		birthdate1.setEffect(null);
		post1.getSelectionModel().clearSelection();
		post1.getEditor().clear();
		post1.setValue(null);
		post1.setEffect(null);
		salary1.clear();
		salary1.setEffect(null);
		education1.getSelectionModel().clearSelection();
		education1.getEditor().clear();
		education1.setValue(null);
		education1.setEffect(null);
		surname2.clear();
		surname2.setEffect(null);
		name2.clear();
		name2.setEffect(null);
		patronymic2.clear();
		patronymic2.setEffect(null);
		department2.getSelectionModel().clearSelection();
		department2.getEditor().clear();
		department2.setValue(null);
		department2.setEffect(null);
		birthdate2.getEditor().clear();
		birthdate2.setValue(null);
		birthdate2.setEffect(null);
		post2.getSelectionModel().clearSelection();
		post2.getEditor().clear();
		post2.setValue(null);
		post2.setEffect(null);
		salary2.clear();
		salary2.setEffect(null);
		education2.getSelectionModel().clearSelection();
		education2.getEditor().clear();
		education2.setValue(null);
		education2.setEffect(null);

		login.clear();
		login.setEffect(null);
		pass.clear();
		pass.setEffect(null);
		passConf.clear();
		passConf.setEffect(null);
		role.getSelectionModel().clearSelection();
		role.getEditor().clear();
		role.setValue(null);
		role.setEffect(null);
		blocked.setSelected(false);
	}

	@Override
	public void bindDataSources(Map<Class<AbstractDAO>, ObservableList> sources)
	{
		ObservableList<Department> departments = sources.get(Department.class);
		department1.setItems(departments);
		department2.setItems(departments);

		ObservableList<Post> posts = sources.get(Post.class);
		post1.setItems(posts);
		post2.setItems(posts);

		ObservableList<Education> educations = sources.get(Education.class);
		education1.setItems(educations);
		education2.setItems(educations);
	}

	public ToggleButton getToggleAccBtn()
	{
		return toggleAccBtn;
	}

	public boolean createPlusAccount()
	{
		Pair<Employee, Account> pair = withdrawPrimaryAllPlusAccount();
		ClientAgent.getInstance().send(new CreateEmployeePlusAccountOrder(pair.getKey(), pair.getValue()));
		return true;
	}
}
