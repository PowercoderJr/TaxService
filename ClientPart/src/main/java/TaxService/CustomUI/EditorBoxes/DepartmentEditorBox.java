package TaxService.CustomUI.EditorBoxes;

import TaxService.Callback;
import TaxService.CustomUI.MaskField;
import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.City;
import TaxService.DAOs.Department;
import TaxService.DAOs.Deptype;
import TaxService.Deliveries.AllDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadAllOrder;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DepartmentEditorBox extends AbstractEditorBox<Department>
{
	private TextField name1, name2;
	private ComboBox<Deptype> deptype1, deptype2;
	private MaskField startyear1, startyear2;
	private MaskField phone1, phone2;
	private ComboBox<City> city1, city2;
	private TextField street1, street2;
	private TextField house1, house2;

	public DepartmentEditorBox()
	{
		super(Department.class);

		name1 = new TextField();
		name1.setPrefWidth(200);
		name2 = new TextField();
		name2.setPrefWidth(200);
		addField("Название", name1, name2, false);

		deptype1 = new ComboBox<>();
		deptype1.setPrefWidth(150);
		deptype1.setOnShowing(event ->
		{
			deptype1.getSelectionModel().clearSelection();
			deptype1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Deptype>(Deptype.class, true, null));
		});
		deptype2 = new ComboBox<>();
		deptype2.setPrefWidth(150);
		deptype2.setOnShowing(event ->
		{
			deptype2.getSelectionModel().clearSelection();
			deptype2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Deptype>(Deptype.class, true, null));
		});
		addField("Тип отделения", deptype1, deptype2, false);

		startyear1 = new MaskField();
		startyear1.setPrefWidth(150);
		startyear1.setMask("DDDD");
		startyear1.setWhatMask("####");
		startyear1.setPlaceholder("____");
		startyear2 = new MaskField();
		startyear2.setPrefWidth(150);
		startyear2.setMask("DDDD");
		startyear2.setWhatMask("####");
		startyear2.setPlaceholder("____");
		addField("Год открытия", startyear1, startyear2, false);

		phone1 = new MaskField();
		phone1.setPrefWidth(150);
		phone1.setMask("+38(0DD)DDD-DD-DD");
		phone1.setWhatMask("-----##-###-##-##");
		phone1.setPlaceholder("+38(0__)___-__-__");
		phone2 = new MaskField();
		phone2.setPrefWidth(150);
		phone2.setMask("+38(0DD)DDD-DD-DD");
		phone2.setWhatMask("-----##-###-##-##");
		phone2.setPlaceholder("+38(0__)___-__-__");
		addField("Телефон", phone1, phone2, false);


		city1 = new ComboBox<>();
		city1.setPrefWidth(150);
		city1.setOnShowing(event ->
		{
			city1.getSelectionModel().clearSelection();
			city1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<City>(City.class, true, null));
		});
		city2 = new ComboBox<>();
		city2.setPrefWidth(150);
		city2.setOnShowing(event ->
		{
			city2.getSelectionModel().clearSelection();
			city2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<City>(City.class, true, null));
		});
		addField("Город", city1, city2, false);

		street1 = new TextField();
		street1.setPrefWidth(150);
		street2 = new TextField();
		street2.setPrefWidth(150);
		addField("Улица", street1, street2, false);

		house1 = new TextField();
		house1.setPrefWidth(70);
		house2 = new TextField();
		house2.setPrefWidth(70);
		addField("Дом", house1, house2, false);
	}

	@Override
	public Department withdrawPrimaryAll()
	{
		String name = name1.getText().trim();
		Deptype deptype = deptype1.getSelectionModel().getSelectedItem();
		BigDecimal startyear = new BigDecimal(startyear1.getText());
		String phone = phone1.getText();
		City city = city1.getSelectionModel().getSelectedItem();
		String street = street1.getText().trim();
		String house = house1.getText().trim();
		return new Department(name, deptype, startyear, phone, city, street, house);
	}

	/*@Override
	public Department withdrawSecondaryAll()
	{
		String name = name2.getText().trim();
		Deptype deptype = deptype2.getSelectionModel().getSelectedItem();
		BigDecimal startyear = new BigDecimal(startyear2.getText());
		String phone = phone2.getText();
		String city = city2.getText().trim();
		String street = street2.getText().trim();
		String house = house2.getText().trim();
		return new Department(name, deptype, startyear, phone, city, street, house);
	}*/

	@Override
	public Pair<Department, List<Field>> withdrawPrimaryFilled()
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

			String name;
			if (name1.getText().trim().isEmpty())
				name = null;
			else
			{
				name = name1.getText().trim();
				filledFields.add(clazz.getField("name"));
			}

			Deptype deptype;
			if (deptype1.getSelectionModel().isEmpty())
				deptype = null;
			else
			{
				deptype = deptype1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("deptype"));
			}

			BigDecimal startyear;
			if (startyear1.getPlainText().isEmpty())
				startyear = null;
			else
			{
				startyear = new BigDecimal(startyear1.getText());
				filledFields.add(clazz.getField("startyear"));
			}

			String phone;
			if (phone1.getPlainText().isEmpty())
				phone = null;
			else
			{
				phone = phone1.getText();
				filledFields.add(clazz.getField("phone"));
			}

			City city;
			if (city1.getSelectionModel().isEmpty())
				city = null;
			else
			{
				city = city1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("city"));
			}

			String street;
			if (street1.getText().trim().isEmpty())
				street = null;
			else
			{
				street = street1.getText().trim();
				filledFields.add(clazz.getField("street"));
			}

			String house;
			if (house1.getText().trim().isEmpty())
				house = null;
			else
			{
				house = house1.getText().trim();
				filledFields.add(clazz.getField("house"));
			}

			Department dao = new Department(name, deptype, startyear, phone, city, street, house);
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
	public Pair<Department, List<Field>> withdrawSecondaryFilled()
	{
		try
		{
			List<Field> filledFields = new ArrayList<>();
			String name;
			if (name2.getText().trim().isEmpty())
				name = null;
			else
			{
				name = name2.getText().trim();
				filledFields.add(clazz.getField("name"));
			}

			Deptype deptype;
			if (deptype2.getSelectionModel().isEmpty())
				deptype = null;
			else
			{
				deptype = deptype2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("deptype"));
			}

			BigDecimal startyear;
			if (startyear2.getPlainText().isEmpty())
				startyear = null;
			else
			{
				startyear = new BigDecimal(startyear2.getText());
				filledFields.add(clazz.getField("startyear"));
			}

			String phone;
			if (phone2.getPlainText().isEmpty())
				phone = null;
			else
			{
				phone = phone2.getText();
				filledFields.add(clazz.getField("phone"));
			}

			City city;
			if (city2.getSelectionModel().isEmpty())
				city = null;
			else
			{
				city = city2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("city"));
			}

			String street;
			if (street2.getText().trim().isEmpty())
				street = null;
			else
			{
				street = street2.getText().trim();
				filledFields.add(clazz.getField("street"));
			}

			String house;
			if (house2.getText().trim().isEmpty())
				house = null;
			else
			{
				house = house2.getText().trim();
				filledFields.add(clazz.getField("house"));
			}

			return new Pair<>(new Department(name, deptype, startyear, phone, city, street, house), filledFields);
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private boolean validateStartyear(MaskField field, boolean isRequired)
	{
		boolean isValid;
		try
		{
			isValid = !isRequired && field.getPlainText().isEmpty() || !field.getPlainText().isEmpty() &&
					Integer.parseInt(field.getText()) <= Year.now().getValue();
		}
		catch (Exception e)
		{
			isValid = false;
		}
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	@Override
	public boolean validatePrimary(boolean allRequired)
	{
		return validateTextField(name1, allRequired) &
				validateComboBox(deptype1, allRequired) &
				validateStartyear(startyear1, allRequired) &
				validateMaskField(phone1, allRequired) &
				validateComboBox(city1, allRequired) &
				validateTextField(street1, allRequired) &
				validateTextField(house1, allRequired);
	}

	@Override
	public boolean validateSecondary(boolean allRequired)
	{
		return validateTextField(name2, allRequired) &
				validateComboBox(deptype2, allRequired) &
				validateStartyear(startyear2, allRequired) &
				validateMaskField(phone2, allRequired) &
				validateComboBox(city2, allRequired) &
				validateTextField(street2, allRequired) &
				validateTextField(house2, allRequired);
	}

	@Override
	public int countFilledPrimary()
	{
		int count = 0;
		if (!id1.getText().trim().isEmpty())
			++count;
		if (!name1.getText().trim().isEmpty())
			++count;
		if (!deptype1.getSelectionModel().isEmpty())
			++count;
		if (!startyear1.getPlainText().isEmpty())
			++count;
		if (!phone1.getPlainText().isEmpty())
			++count;
		if (!city1.getSelectionModel().isEmpty())
			++count;
		if (!street1.getText().trim().isEmpty())
			++count;
		if (!house1.getText().trim().isEmpty())
			++count;
		return count;
	}

	@Override
	public int countFilledSecondary()
	{
		int count = 0;
		if (!name2.getText().trim().isEmpty())
			++count;
		if (!deptype2.getSelectionModel().isEmpty())
			++count;
		if (!startyear2.getPlainText().isEmpty())
			++count;
		if (!phone2.getPlainText().isEmpty())
			++count;
		if (!city2.getSelectionModel().isEmpty())
			++count;
		if (!street2.getText().trim().isEmpty())
			++count;
		if (!house2.getText().trim().isEmpty())
			++count;
		return count;
	}

	@Override
	public void clearAll()
	{
		id1.clear();
		id1.setEffect(null);
		name1.clear();
		name1.setEffect(null);
		deptype1.getSelectionModel().clearSelection();
		deptype1.getEditor().clear();
		deptype1.setValue(null);
		deptype1.setEffect(null);
		startyear1.clear();
		startyear1.setEffect(null);
		phone1.clear();
		phone1.setEffect(null);
		city1.getSelectionModel().clearSelection();
		city1.getEditor().clear();
		city1.setValue(null);
		city1.setEffect(null);
		street1.clear();
		street1.setEffect(null);
		house1.clear();
		house1.setEffect(null);
		name2.clear();
		name2.setEffect(null);
		deptype2.getSelectionModel().clearSelection();
		deptype2.getEditor().clear();
		deptype2.setValue(null);
		deptype2.setEffect(null);
		startyear2.clear();
		startyear2.setEffect(null);
		phone2.clear();
		phone2.setEffect(null);
		city2.getSelectionModel().clearSelection();
		city2.getEditor().clear();
		city2.setValue(null);
		city2.setEffect(null);
		street2.clear();
		street2.setEffect(null);
		house2.clear();
		house2.setEffect(null);
	}

	@Override
	public void bindDataSources(Map<Class<AbstractDAO>, ObservableList> sources)
	{
		ObservableList<Deptype> deptypes = sources.get(Deptype.class);
		deptype1.setItems(deptypes);
		deptype2.setItems(deptypes);

		ObservableList<City> cities = sources.get(City.class);
		city1.setItems(cities);
		city2.setItems(cities);
	}
}
