package TaxService;

import TaxService.DAOs.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TableColumnsBuilder
{
	private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

	private TableColumnsBuilder() {}

	public static List<TableColumn<? extends AbstractDAO, String>> buildForDAO(Class<? extends AbstractDAO> clazz)
	{
		List<TableColumn<? extends AbstractDAO, String>> result = null;

		if (AbstractRefDAO.class.isAssignableFrom(clazz))
			result = (List<TableColumn<? extends AbstractDAO, String>>) (Object) buildForRefDAO(); //lulz
		else
			try
			{
				Method builder = TableColumnsBuilder.class.getMethod("buildFor" + clazz.getSimpleName());
				result = (List<TableColumn<? extends AbstractDAO, String>>) builder.invoke(null);
			}
			catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
			{
				e.printStackTrace();
			}
		return result;
	}

	public static List<TableColumn<Department, String>> buildForDepartment()
	{
		TableColumn<Department, String> anotherColumn;
		List<TableColumn<Department, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Название");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Тип отделения");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDeptype())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Год открытия");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStartyear())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Телефон");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPhone())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Город");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCity())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Улица");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStreet())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Дом");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getHouse())));
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<Employee, String>> buildForEmployee()
	{
		TableColumn<Employee, String> anotherColumn;
		List<TableColumn<Employee, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Фамилия");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSurname())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Имя");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Отчество");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPatronymic())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Отделение");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDepartment())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Дата рождения");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(df.format(data.getValue().getBirthdate()))));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Должность");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPost())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Зарплата");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSalary())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Образование");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEducation())));
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<Company, String>> buildForCompany()
	{
		TableColumn<Company, String> anotherColumn;
		List<TableColumn<Company, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Название");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Тип собственности");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getOwntype())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Телефон");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPhone())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Год открытия");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStartyear())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Штат");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStatesize())));
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<Payment, String>> buildForPayment()
	{
		TableColumn<Payment, String> anotherColumn;
		List<TableColumn<Payment, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Тип платежа");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPaytype())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Дата");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(df.format(data.getValue().getDate()))));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Сумма");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAmount())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Сотрудник-оформитель");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEmployee())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Отделение-оформитель");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDepartment())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Компания-плательщик");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCompany())));
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<? extends AbstractRefDAO, String>> buildForRefDAO()
	{
		TableColumn<AbstractRefDAO, String> anotherColumn;
		List<TableColumn<? extends AbstractRefDAO, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Наименование");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		list.add(anotherColumn);
		return list;
	}

	/*public static List<TableColumn<, String>> buildFor...()
	{
		TableColumn<, String> anotherColumn;
		List<TableColumn<, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get())));
		list.add(anotherColumn);
		return list;
	}*/
}
