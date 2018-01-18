package TaxService.CustomUI.EditorBoxes;

import TaxService.Callback;
import TaxService.CustomUI.MaskField;
import TaxService.DAOs.*;
import TaxService.Deliveries.AllDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadAllOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PaymentEditorBox extends AbstractEditorBox<Payment>
{
	private ComboBox<Paytype> paytype1, paytype2;
	private DatePicker date1, date2;
	private TextField amount1, amount2;
	private ComboBox<Employee> employee1, employee2;
	private ComboBox<Department> department1, department2;
	private ComboBox<Company> company1, company2;
	
	public PaymentEditorBox()
	{
		super(Payment.class);

		paytype1 = new ComboBox<>();
		paytype1.setOnShowing(event ->
		{
			paytype1.getSelectionModel().clearSelection();
			paytype1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Paytype>(Paytype.class, true, null));
		});
		paytype2 = new ComboBox<>();
		paytype2.setOnShowing(event ->
		{
			paytype2.getSelectionModel().clearSelection();
			paytype2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Paytype>(Paytype.class, true, null));
		});
		addField("Тип платежа", paytype1, paytype2, false);

		date1 = new DatePicker();
		date2 = new DatePicker();
		date2.setVisible(false); //Будет генерироваться автоматически при добавлении записи, изменить нельзя
		addField("Дата", date1, date2, true);

		amount1 = new TextField();
		amount2 = new TextField();
		addField("Сумма", amount1, amount2, false);

		employee1 = new ComboBox<>();
		employee1.setOnShowing(event ->
		{
			employee1.getSelectionModel().clearSelection();
			employee1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Employee>(Employee.class, true, null));
		});
		employee2 = new ComboBox<>();
		employee2.setOnShowing(event ->
		{
			employee2.getSelectionModel().clearSelection();
			employee2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Employee>(Employee.class, true, null));
		});
		employee2.setVisible(false); //Будет генерироваться автоматически при добавлении записи, изменить нельзя
		addField("Сотрудник-оформитель", employee1, employee2, true);

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
		addField("Отделение-оформитель", department1, department2, false);

		company1 = new ComboBox<>();
		company1.setOnShowing(event ->
		{
			company1.getSelectionModel().clearSelection();
			company1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Company>(Company.class, true, null));
		});
		company2 = new ComboBox<>();
		company2.setOnShowing(event ->
		{
			company2.getSelectionModel().clearSelection();
			company2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Company>(Company.class, true, null));
		});
		addField("Компания-плательщик", company1, company2, false);
	}

	@Override
	public Payment withdrawPrimaryAll()
	{
		Paytype paytype = paytype1.getSelectionModel().getSelectedItem();
		Date date = date1.getValue() == null ? Date.valueOf("2001-01-01") : Date.valueOf(date1.getValue());
		BigDecimal amount = new BigDecimal(amount1.getText().trim());
		Employee employee = employee1.getSelectionModel().getSelectedItem();
		Department department = department1.getSelectionModel().getSelectedItem();
		Company company = company1.getSelectionModel().getSelectedItem();
		return new Payment(paytype, date, amount, employee, department, company);
	}

	@Override
	public Pair<Payment, List<Field>> withdrawPrimaryFilled()
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

			Paytype paytype;
			if (paytype1.getSelectionModel().isEmpty())
				paytype = null;
			else
			{
				paytype = paytype1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("paytype"));
			}

			Date date;
			if (date1.getValue() == null)
				date = null;
			else
			{
				date = Date.valueOf(date1.getValue());
				filledFields.add(clazz.getField("date"));
			}

			BigDecimal amount;
			if (amount1.getText().isEmpty())
				amount = null;
			else
			{
				amount = new BigDecimal(amount1.getText().replace(',', '.'));
				filledFields.add(clazz.getField("amount"));
			}

			Employee employee;
			if (employee1.getSelectionModel().isEmpty())
				employee = null;
			else
			{
				employee = employee1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("employee"));
			}

			Department department;
			if (department1.getSelectionModel().isEmpty())
				department = null;
			else
			{
				department = department1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("department"));
			}

			Company company;
			if (company1.getSelectionModel().isEmpty())
				company = null;
			else
			{
				company = company1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("company"));
			}

			Payment dao = new Payment(paytype, date, amount, employee, department, company);
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
	public Pair<Payment, List<Field>> withdrawSecondaryFilled()
	{
		try
		{
			List<Field> filledFields = new ArrayList<>();

			Paytype paytype;
			if (paytype2.getSelectionModel().isEmpty())
				paytype = null;
			else
			{
				paytype = paytype2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("paytype"));
			}

			Date date;
			if (date2.getValue() == null)
				date = null;
			else
			{
				date = Date.valueOf(date2.getValue());
				filledFields.add(clazz.getField("date"));
			}

			BigDecimal amount;
			if (amount2.getText().isEmpty())
				amount = null;
			else
			{
				amount = new BigDecimal(amount2.getText().replace(',', '.'));
				filledFields.add(clazz.getField("amount"));
			}

			Employee employee;
			if (employee2.getSelectionModel().isEmpty())
				employee = null;
			else
			{
				employee = employee2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("employee"));
			}

			Department department;
			if (department2.getSelectionModel().isEmpty())
				department = null;
			else
			{
				department = department2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("department"));
			}

			Company company;
			if (company2.getSelectionModel().isEmpty())
				company = null;
			else
			{
				company = company2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("company"));
			}

			return new Pair<>(new Payment(paytype, date, amount, employee, department, company), filledFields);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean validatePrimary(boolean allRequired)
	{
		return validateComboBox(paytype1, allRequired) &
				//validatePayDate(date1, allRequired) &
				validateTextPositiveFloatField(amount1, allRequired) &
				//validateComboBox(employee1, allRequired) &
				validateComboBox(department1, allRequired) &
				validateComboBox(company1, allRequired);
	}

	@Override
	public boolean validateSecondary(boolean allRequired)
	{
		return validateComboBox(paytype2, allRequired) &
				//validatePayDate(date2, allRequired) &
				validateTextPositiveFloatField(amount2, allRequired) &
				//validateComboBox(employee2, allRequired) &
				validateComboBox(department2, allRequired) &
				validateComboBox(company2, allRequired);
	}

	@Override
	public int countFilledPrimary()
	{
		int count = 0;
		if (!id1.getText().trim().isEmpty())
			++count;
		if (!paytype1.getSelectionModel().isEmpty())
			++count;
		if (date1.getValue() != null)
			++count;
		if (!amount1.getText().trim().isEmpty())
			++count;
		if (!employee1.getSelectionModel().isEmpty())
			++count;
		if (!department1.getSelectionModel().isEmpty())
			++count;
		if (!company1.getSelectionModel().isEmpty())
			++count;
		return count;
	}

	@Override
	public int countFilledSecondary()
	{
		int count = 0;
		if (!paytype2.getSelectionModel().isEmpty())
			++count;
		if (date2.getValue() != null)
			++count;
		if (!amount2.getText().trim().isEmpty())
			++count;
		if (!employee2.getSelectionModel().isEmpty())
			++count;
		if (!department2.getSelectionModel().isEmpty())
			++count;
		if (!company2.getSelectionModel().isEmpty())
			++count;
		return count;
	}

	@Override
	public void clearAll()
	{
		id1.clear();
		id1.setEffect(null);
		paytype1.getSelectionModel().clearSelection();
		paytype1.getEditor().clear();
		paytype1.setValue(null);
		paytype1.setEffect(null);
		date1.getEditor().clear();
		date1.setValue(null);
		date1.setEffect(null);
		amount1.clear();
		amount1.setEffect(null);
		employee1.getSelectionModel().clearSelection();
		employee1.getEditor().clear();
		employee1.setValue(null);
		employee1.setEffect(null);
		department1.getSelectionModel().clearSelection();
		department1.getEditor().clear();
		department1.setValue(null);
		department1.setEffect(null);
		company1.getSelectionModel().clearSelection();
		company1.getEditor().clear();
		company1.setValue(null);
		company1.setEffect(null);
		paytype2.getSelectionModel().clearSelection();
		paytype2.getEditor().clear();
		paytype2.setValue(null);
		paytype2.setEffect(null);
		date2.getEditor().clear();
		date2.setEffect(null);
		date2.setValue(null);
		date2.setEffect(null);
		amount2.clear();
		amount2.setEffect(null);
		employee2.getSelectionModel().clearSelection();
		employee2.getEditor().clear();
		employee2.setValue(null);
		employee2.setEffect(null);
		department2.getSelectionModel().clearSelection();
		department2.getEditor().clear();
		department2.setValue(null);
		department2.setEffect(null);
		company2.getSelectionModel().clearSelection();
		company2.getEditor().clear();
		company2.setValue(null);
		company2.setEffect(null);
	}

	@Override
	public void bindDataSources(Map<Class<AbstractDAO>, ObservableList> sources)
	{
		ObservableList<Paytype> paytypes = sources.get(Paytype.class);
		paytype1.setItems(paytypes);
		paytype2.setItems(paytypes);
		ObservableList<Employee> employees = sources.get(Employee.class);
		employee1.setItems(employees);
		employee2.setItems(employees);
		ObservableList<Department> departments = sources.get(Department.class);
		department1.setItems(departments);
		department2.setItems(departments);
		ObservableList<Company> template = sources.get(Company.class);
		company1.setItems(template);
		company2.setItems(template);
	}
}
