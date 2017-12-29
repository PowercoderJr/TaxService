package TaxService.CustomUI.EditorBoxes;

import TaxService.Callback;
import TaxService.CustomUI.MaskField;
import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.Company;
import TaxService.DAOs.Owntype;
import TaxService.Deliveries.AllDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadAllOrder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompanyEditorBox extends AbstractEditorBox<Company>
{
	private TextField name1, name2;
	private ComboBox<Owntype> owntype1, owntype2;
	private MaskField phone1, phone2;
	private MaskField startyear1, startyear2;
	private TextField statesize1, statesize2;
	
	public CompanyEditorBox()
	{
		super(Company.class);

		name1 = new TextField();
		name1.setPrefWidth(200);
		name2 = new TextField();
		name2.setPrefWidth(200);
		addField("Название", name1, name2, false);
		
		owntype1 = new ComboBox<>();
		owntype1.setPrefWidth(170);
		owntype1.setOnShowing(event ->
		{
			owntype1.getSelectionModel().clearSelection();
			owntype1.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Owntype>(Owntype.class, ClientAgent.getInstance().getLogin(),
					true, ReadAllOrder.Purposes.REFRESH_CB, null));
		});
		owntype2 = new ComboBox<>();
		owntype2.setPrefWidth(170);
		owntype2.setOnShowing(event ->
		{
			owntype2.getSelectionModel().clearSelection();
			owntype2.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Owntype>(Owntype.class, ClientAgent.getInstance().getLogin(),
					true, ReadAllOrder.Purposes.REFRESH_CB, null));
		});
		addField("Форма собственности", owntype1, owntype2, false);

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

		statesize1 = new TextField();
		statesize1.setPrefWidth(100);
		statesize2 = new TextField();
		statesize2.setPrefWidth(100);
		addField("Штат", statesize1, statesize2, false);
	}

	@Override
	public Company withdrawPrimaryAll()
	{
		String name = name1.getText().trim();
		Owntype owntype = owntype1.getSelectionModel().getSelectedItem();
		String phone = phone1.getText();
		BigDecimal startyear = new BigDecimal(startyear1.getText());
		int statesize = Integer.parseInt(statesize1.getText());
		return new Company(name, owntype, phone, startyear, statesize);
	}

	@Override
	public Pair<Company, List<Field>> withdrawPrimaryFilled()
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

			Owntype owntype;
			if (owntype1.getSelectionModel().isEmpty())
				owntype = null;
			else
			{
				owntype = owntype1.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("owntype"));
			}

			String phone;
			if (phone1.getPlainText().isEmpty())
				phone = null;
			else
			{
				phone = phone1.getText();
				filledFields.add(clazz.getField("phone"));
			}

			BigDecimal startyear;
			if (startyear1.getPlainText().isEmpty())
				startyear = null;
			else
			{
				startyear = new BigDecimal(startyear1.getText());
				filledFields.add(clazz.getField("startyear"));
			}

			int statesize;
			if (statesize1.getText().trim().isEmpty())
				statesize = 0;
			else
			{
				statesize = Integer.parseInt(statesize1.getText().trim());
				filledFields.add(clazz.getField("statesize"));
			}

			Company dao = new Company(name, owntype, phone, startyear, statesize);
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
	public Pair<Company, List<Field>> withdrawSecondaryFilled()
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

			Owntype owntype;
			if (owntype2.getSelectionModel().isEmpty())
				owntype = null;
			else
			{
				owntype = owntype2.getSelectionModel().getSelectedItem();
				filledFields.add(clazz.getField("owntype"));
			}

			String phone;
			if (phone2.getPlainText().isEmpty())
				phone = null;
			else
			{
				phone = phone2.getText();
				filledFields.add(clazz.getField("phone"));
			}

			BigDecimal startyear;
			if (startyear2.getPlainText().isEmpty())
				startyear = null;
			else
			{
				startyear = new BigDecimal(startyear2.getText());
				filledFields.add(clazz.getField("startyear"));
			}

			int statesize;
			if (statesize2.getText().trim().isEmpty())
				statesize = 0;
			else
			{
				statesize = Integer.parseInt(statesize2.getText().trim());
				filledFields.add(clazz.getField("statesize"));
			}

			return new Pair<>(new Company(name, owntype, phone, startyear, statesize), filledFields);
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
				validateComboBox(owntype1, allRequired) &
				validateMaskField(phone1, allRequired) &
				validateStartyear(startyear1, allRequired) &
				validateTextPositiveIntField(statesize1, allRequired);
	}

	@Override
	public boolean validateSecondary(boolean allRequired)
	{
		return validateTextField(name2, allRequired) &
				validateComboBox(owntype2, allRequired) &
				validateMaskField(phone2, allRequired) &
				validateStartyear(startyear2, allRequired) &
				validateTextPositiveIntField(statesize2, allRequired);
	}

	@Override
	public int countFilledPrimary()
	{
		int count = 0;
		if (!id1.getText().trim().isEmpty())
			++count;
		if (!name1.getText().trim().isEmpty())
			++count;
		if (!owntype1.getSelectionModel().isEmpty())
			++count;
		if (!phone1.getPlainText().isEmpty())
			++count;
		if (!startyear1.getPlainText().isEmpty())
			++count;
		if (!statesize1.getText().trim().isEmpty())
			++count;
		return count;
	}

	@Override
	public int countFilledSecondary()
	{
		int count = 0;
		if (!id2.getText().trim().isEmpty())
			++count;
		if (!name2.getText().trim().isEmpty())
			++count;
		if (!owntype2.getSelectionModel().isEmpty())
			++count;
		if (!phone2.getPlainText().isEmpty())
			++count;
		if (!startyear2.getPlainText().isEmpty())
			++count;
		if (!statesize2.getText().trim().isEmpty())
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
		owntype1.getSelectionModel().clearSelection();
		owntype1.getEditor().clear();
		owntype1.setValue(null);
		owntype1.setEffect(null);
		phone1.clear();
		phone1.setEffect(null);
		startyear1.clear();
		startyear1.setEffect(null);
		statesize1.clear();
		statesize1.setEffect(null);
		name2.clear();
		name2.setEffect(null);
		owntype2.getSelectionModel().clearSelection();
		owntype2.getEditor().clear();
		owntype2.setValue(null);
		owntype2.setEffect(null);
		phone2.clear();
		phone2.setEffect(null);
		startyear2.clear();
		startyear2.setEffect(null);
		statesize2.clear();
		statesize2.setEffect(null);
	}

	@Override
	public void bindDataSources(Map<Class<AbstractDAO>, ObservableList> sources)
	{
		ObservableList<Owntype> owntypes = sources.get(Owntype.class);
		owntype1.setItems(owntypes);
		owntype2.setItems(owntypes);
	}
}
