package TaxService;

import TaxService.DAOs.*;
import TaxService.Netty.ClientAgent;
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
	private TableColumnsBuilder() {}

	public static <T extends AbstractDAO> List<TableColumn<T, String>> buildForDAO(Class<T> clazz)
	{
		List<TableColumn<T, String>> result = null;

		if (AbstractRefDAO.class.isAssignableFrom(clazz))
			result = (List<TableColumn<T, String>>) (Object) buildForRefDAO(); //lulz
		else
			try
			{
				Method builder = TableColumnsBuilder.class.getMethod("buildFor" + clazz.getSimpleName());
				result = (List<TableColumn<T, String>>) builder.invoke(null);
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
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(70);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Название");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(350);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Тип отделения");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDeptype())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(130);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Год открытия");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStartyear())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(130);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Телефон");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPhone())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(160);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Город");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCity())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(150);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Улица");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStreet())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(170);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Дом");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getHouse())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(60);
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<Employee, String>> buildForEmployee()
	{
		TableColumn<Employee, String> anotherColumn;
		List<TableColumn<Employee, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(70);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Фамилия");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSurname())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(150);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Имя");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(150);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Отчество");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPatronymic())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(150);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Отделение");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDepartment())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(350);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Дата рождения");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(ClientAgent.df.format(data.getValue().getBirthdate()))));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(140);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Должность");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPost())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(175);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Зарплата");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSalary())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(90);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Образование");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEducation())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(130);
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<Company, String>> buildForCompany()
	{
		TableColumn<Company, String> anotherColumn;
		List<TableColumn<Company, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(70);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Название");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(350);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Тип собственности");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getOwntype())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(170);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Телефон");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPhone())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(160);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Год открытия");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStartyear())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(130);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Штат");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getStatesize())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(60);
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<Payment, String>> buildForPayment()
	{
		TableColumn<Payment, String> anotherColumn;
		List<TableColumn<Payment, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(70);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Тип платежа");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getPaytype())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(350);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Дата");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(ClientAgent.df.format(data.getValue().getDate()))));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(90);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Сумма");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getAmount())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(90);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Сотрудник-оформитель");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEmployee())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(200);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Отделение-оформитель");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDepartment())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(350);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Компания-плательщик");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getCompany())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(350);
		list.add(anotherColumn);
		return list;
	}

	public static <T extends AbstractRefDAO> List<TableColumn<T, String>> buildForRefDAO()
	{
		TableColumn<T, String> anotherColumn;
		List<TableColumn<T, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(70);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Наименование");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getName())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth(350);
		list.add(anotherColumn);
		return list;
	}

	public static List<TableColumn<List, String>> buildForListOfStrings(List<String> headers)
	{
		TableColumn<List, String> anotherColumn;
		List<TableColumn<List, String>> list = new ArrayList<>();

		for (String header : headers)
		{
			anotherColumn = new TableColumn<>(header);
			anotherColumn.setCellValueFactory(data ->
			{
				int index = data.getTableView().getColumns().indexOf(data.getTableColumn());
				return new SimpleStringProperty(String.valueOf(data.getValue().get(index)));
			});
			anotherColumn.setSortable(false);
			list.add(anotherColumn);
		}
		return list;
	}

	/*public static List<TableColumn<, String>> buildFor...()
	{
		TableColumn<, String> anotherColumn;
		List<TableColumn<, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth();
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().get())));
		anotherColumn.setSortable(false);
		anotherColumn.setPrefWidth();
		list.add(anotherColumn);
		return list;
	}*/
}
