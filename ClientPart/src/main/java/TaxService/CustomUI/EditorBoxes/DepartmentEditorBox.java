package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.Department;
import TaxService.DAOs.Deptype;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.Year;

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
		deptype1.setEditable(true);
		deptype2 = new ComboBox<>();
		deptype2.setPrefWidth(150);
		deptype2.setEditable(true);
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
	}

	@Override
	public void depositFields(Department dao)
	{

	}

	@Override
	public Department withdrawFields()
	{
		String name = name1.getText();
		Deptype deptype = deptype1.getSelectionModel().getSelectedItem();
		BigDecimal startyear = new BigDecimal(startyear1.getPlainText());
		String phone = phone1.getText();
		String city = city1.getText();
		String street = street1.getText();
		String house = house1.getText();
		return new Department(name, deptype, startyear, phone, city, street, house);
	}

	private boolean validateStartyear(MaskField field, boolean isRequired)
	{
		boolean isValid;
		try
		{
			isValid = !isRequired && field.getPlainText().isEmpty() ||
					!field.getPlainText().isEmpty() && Integer.parseInt(field.getPlainText()) <= Year.now().getValue();
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
	public boolean validatePrimary(boolean isRequired)
	{
		return validateComboBox(deptype1, isRequired) &
				validateTextField(name1, isRequired) &
				validateStartyear(startyear1, isRequired) &
				validateMaskField(phone1, isRequired) &
				validateTextField(city1, isRequired) &
				validateTextField(street1, isRequired) &
				validateTextField(house1, isRequired);
	}

	@Override
	public boolean validateSecondary(boolean isRequired)
	{
		return validateComboBox(deptype2, isRequired) &
				validateTextField(name2, isRequired) &
				validateStartyear(startyear2, isRequired) &
				validateMaskField(phone2, isRequired) &
				validateTextField(city2, isRequired) &
				validateTextField(street2, isRequired) &
				validateTextField(house2, isRequired);
	}

	@Override
	public void clearAll()
	{

	}
}
