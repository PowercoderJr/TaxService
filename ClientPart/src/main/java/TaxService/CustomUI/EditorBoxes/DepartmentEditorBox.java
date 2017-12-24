package TaxService.CustomUI.EditorBoxes;

import TaxService.Callback;
import TaxService.CustomUI.MaskField;
import TaxService.DAOs.Department;
import TaxService.DAOs.Deptype;
import TaxService.Deliveries.AllDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadAllOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

public class DepartmentEditorBox extends AbstractEditorBox<Department>
{
	private TextField name1, name2;
	private ComboBox<Deptype> deptype1, deptype2;
	private MaskField startyear1, startyear2;
	private MaskField phone1, phone2;
	private TextField city1, city2;
	private TextField street1, street2;
	private TextField house1, house2;

	public DepartmentEditorBox()
	{
		super(Department.class);

		name1 = new TextField();
		name1.setPrefWidth(200);
		name2 = new TextField();
		name2.setPrefWidth(200);
		addField("Название", name1, name2);

		deptype1 = new ComboBox<>();
		deptype1.setPrefWidth(150);
		//deptype1.setEditable(true);
		deptype1.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Deptype>(Deptype.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		deptype2 = new ComboBox<>();
		deptype2.setPrefWidth(150);
		//deptype2.setEditable(true);
		deptype2.setOnShowing(event -> ClientAgent.getInstance()
				.send(new ReadAllOrder<Deptype>(Deptype.class, ClientAgent.getInstance()
						.getLogin(), true, ReadAllOrder.Purposes.REFRESH_CB, null)));
		addField("Тип отделения", deptype1, deptype2);

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
		addField("Год открытия", startyear1, startyear2);

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
		addField("Телефон", phone1, phone2);

		city1 = new TextField();
		city1.setPrefWidth(150);
		city2 = new TextField();
		city2.setPrefWidth(150);
		addField("Город", city1, city2);

		street1 = new TextField();
		street1.setPrefWidth(150);
		street2 = new TextField();
		street2.setPrefWidth(150);
		addField("Улица", street1, street2);

		house1 = new TextField();
		house1.setPrefWidth(70);
		house2 = new TextField();
		house2.setPrefWidth(70);
		addField("Дом", house1, house2);

		ClientAgent.subscribeAllReceived(new Callback()
		{
			@Override
			public void callback(Object o)
			{
				AllDelivery delivery = (AllDelivery) o;

				if (delivery.getPurpose() == ReadAllOrder.Purposes.REFRESH_CB && delivery.getContentClazz() == Deptype.class)
				{
					Platform.runLater(() ->
					{
						deptype1.setItems(FXCollections.observableList(delivery.getContent()));
						deptype2.setItems(FXCollections.observableList(delivery.getContent()));
					});
				}
			}
		});
	}

	@Override
	public void depositPrimary(Department dao)
	{

	}

	@Override
	public Department withdrawPrimaryAll()
	{
		String name = name1.getText().trim();
		Deptype deptype = deptype1.getSelectionModel().getSelectedItem();
		BigDecimal startyear = new BigDecimal(startyear1.getText());
		String phone = phone1.getText();
		String city = city1.getText().trim();
		String street = street1.getText().trim();
		String house = house1.getText().trim();
		return new Department(name, deptype, startyear, phone, city, street, house);
	}

	@Override
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
	}

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
				filledFields.add(Department.class.getField("id"));
			}

			String name;
			if (name1.getText().trim().isEmpty())
				name = null;
			else
			{
				name = name1.getText().trim();
				filledFields.add(Department.class.getField("name"));
			}

			Deptype deptype;
			if (deptype1.getSelectionModel().isEmpty())
				deptype = null;
			else
			{
				deptype = deptype1.getSelectionModel().getSelectedItem();
				filledFields.add(Department.class.getField("deptype"));
			}

			BigDecimal startyear;
			if (startyear1.getPlainText().isEmpty())
				startyear = null;
			else
			{
				startyear = new BigDecimal(startyear1.getText());
				filledFields.add(Department.class.getField("startyear"));
			}

			String phone;
			if (phone1.getPlainText().isEmpty())
				phone = null;
			else
			{
				phone = phone1.getText();
				filledFields.add(Department.class.getField("phone"));
			}

			String city;
			if (city1.getText().trim().isEmpty())
				city = null;
			else
			{
				city = city1.getText().trim();
				filledFields.add(Department.class.getField("city"));
			}

			String street;
			if (street1.getText().trim().isEmpty())
				street = null;
			else
			{
				street = street1.getText().trim();
				filledFields.add(Department.class.getField("street"));
			}

			String house;
			if (house1.getText().trim().isEmpty())
				house = null;
			else
			{
				house = house1.getText().trim();
				filledFields.add(Department.class.getField("house"));
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
				filledFields.add(Department.class.getField("name"));
			}

			Deptype deptype;
			if (deptype2.getSelectionModel().isEmpty())
				deptype = null;
			else
			{
				deptype = deptype2.getSelectionModel().getSelectedItem();
				filledFields.add(Department.class.getField("deptype"));
			}

			BigDecimal startyear;
			if (startyear2.getPlainText().isEmpty())
				startyear = null;
			else
			{
				startyear = new BigDecimal(startyear2.getText());
				filledFields.add(Department.class.getField("startyear"));
			}

			String phone;
			if (phone2.getPlainText().isEmpty())
				phone = null;
			else
			{
				phone = phone2.getText();
				filledFields.add(Department.class.getField("phone"));
			}

			String city;
			if (city2.getText().trim().isEmpty())
				city = null;
			else
			{
				city = city2.getText().trim();
				filledFields.add(Department.class.getField("city"));
			}

			String street;
			if (street2.getText().trim().isEmpty())
				street = null;
			else
			{
				street = street2.getText().trim();
				filledFields.add(Department.class.getField("street"));
			}

			String house;
			if (house2.getText().trim().isEmpty())
				house = null;
			else
			{
				house = house2.getText().trim();
				filledFields.add(Department.class.getField("house"));
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
		return validateComboBox(deptype1, allRequired) &
				validateTextField(name1, allRequired) &
				validateStartyear(startyear1, allRequired) &
				validateMaskField(phone1, allRequired) &
				validateTextField(city1, allRequired) &
				validateTextField(street1, allRequired) &
				validateTextField(house1, allRequired);
	}

	@Override
	public boolean validateSecondary(boolean allRequired)
	{
		return validateComboBox(deptype2, allRequired) &
				validateTextField(name2, allRequired) &
				validateStartyear(startyear2, allRequired) &
				validateMaskField(phone2, allRequired) &
				validateTextField(city2, allRequired) &
				validateTextField(street2, allRequired) &
				validateTextField(house2, allRequired);
	}

	@Override
	public void clearAll()
	{

	}
}
