package TaxService.CustomUI.EditorBoxes;

import TaxService.Callback;
import TaxService.CustomUI.MaskField;
import TaxService.DAOs.*;
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
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;

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
		paytype1.setPrefWidth(170);
		paytype1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Paytype>(Paytype.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		paytype2 = new ComboBox<>();
		paytype2.setPrefWidth(170);
		paytype2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Paytype>(Paytype.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Тип платежа", paytype1, paytype2, false);

		date1 = new DatePicker();
		date1.setPrefWidth(140);
		date2 = new DatePicker();
		date2.setPrefWidth(140);
		date2.setVisible(false); //Будет генерироваться автоматически при добавлении записи, изменить нельзя
		addField("Дата", date1, date2, true);

		amount1 = new TextField();
		amount1.setPrefWidth(100);
		amount2 = new TextField();
		amount2.setPrefWidth(100);
		addField("Сумма", amount1, amount2, false);

		employee1 = new ComboBox<>();
		employee1.setPrefWidth(200);
		employee1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Employee>(Employee.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		employee2 = new ComboBox<>();
		employee2.setPrefWidth(200);
		employee2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Employee>(Employee.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Сотрудник-оформитель", employee1, employee2, false);

		department1 = new ComboBox<>();
		department1.setPrefWidth(200);
		department1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Department>(Department.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		department2 = new ComboBox<>();
		department2.setPrefWidth(200);
		department2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Department>(Department.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Отделение-оформитель", department1, department2, false);

		company1 = new ComboBox<>();
		company1.setPrefWidth(200);
		company1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Company>(Company.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		company2 = new ComboBox<>();
		company2.setPrefWidth(200);
		company2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Company>(Company.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Компания-плательщик", company1, company2, false);

		ClientAgent.subscribeAllReceived(o ->
		{
			AllDelivery delivery = (AllDelivery) o;

			if (delivery.getPurpose() == ReadAllOrder.Purposes.REFRESH_CB )
			{
				if (delivery.getContentClazz() == Paytype.class)
					Platform.runLater(() ->
					{
						if (paytype1.isShowing())
						{
							paytype1.getSelectionModel().clearSelection();
							paytype1.setItems(FXCollections.observableList(delivery.getContent()));
						}
						else if (paytype2.isShowing())
						{
							paytype2.getSelectionModel().clearSelection();
							paytype2.setItems(FXCollections.observableList(delivery.getContent()));
						}
					});
				else if (delivery.getContentClazz() == Employee.class)
					Platform.runLater(() ->
					{
						if (employee1.isShowing())
						{
							employee1.getSelectionModel().clearSelection();
							employee1.setItems(FXCollections.observableList(delivery.getContent()));
						}
						else if (employee2.isShowing())
						{
							employee2.getSelectionModel().clearSelection();
							employee2.setItems(FXCollections.observableList(delivery.getContent()));
						}
					});
				else if (delivery.getContentClazz() == Department.class)
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
				else if (delivery.getContentClazz() == Company.class)
					Platform.runLater(() ->
					{
						if (company1.isShowing())
						{
							company1.getSelectionModel().clearSelection();
							company1.setItems(FXCollections.observableList(delivery.getContent()));
						}
						else if (company2.isShowing())
						{
							company2.getSelectionModel().clearSelection();
							company2.setItems(FXCollections.observableList(delivery.getContent()));
						}
					});
			}
		});
	}

	@Override
	public Payment withdrawPrimaryAll()
	{
		Paytype paytype = paytype1.getSelectionModel().getSelectedItem();
		Date date = Date.valueOf(date1.getValue());
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

	/*protected boolean validatePayDate(DatePicker field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getValue() == null ||
				field.getValue() != null && field.getValue().compareTo(ChronoLocalDate.from(LocalDateTime.now())) <= 0;
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}*/

	@Override
	public boolean validatePrimary(boolean allRequired)
	{
		return validateComboBox(paytype1, allRequired) &
				//validatePayDate(date1, allRequired) &
				validateTextPositiveFloatField(amount1, allRequired) &
				validateComboBox(employee1, allRequired) &
				validateComboBox(department1, allRequired) &
				validateComboBox(company1, allRequired);
	}

	@Override
	public boolean validateSecondary(boolean allRequired)
	{
		return validateComboBox(paytype2, allRequired) &
				//validatePayDate(date2, allRequired) &
				validateTextPositiveFloatField(amount2, allRequired) &
				validateComboBox(employee2, allRequired) &
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
		paytype1.setEffect(null);
		date1.getEditor().clear();
		date1.setValue(null);
		date1.setEffect(null);
		amount1.clear();
		amount1.setEffect(null);
		employee1.getSelectionModel().clearSelection();
		employee1.setEffect(null);
		department1.getSelectionModel().clearSelection();
		department1.setEffect(null);
		company1.getSelectionModel().clearSelection();
		company1.setEffect(null);
		paytype2.getSelectionModel().clearSelection();
		paytype2.setEffect(null);
		date2.getEditor().clear();
		date2.setEffect(null);
		date2.setValue(null);
		date2.setEffect(null);
		amount2.clear();
		amount2.setEffect(null);
		employee2.getSelectionModel().clearSelection();
		employee2.setEffect(null);
		department2.getSelectionModel().clearSelection();
		department2.setEffect(null);
		company2.getSelectionModel().clearSelection();
		company2.setEffect(null);
	}
}
