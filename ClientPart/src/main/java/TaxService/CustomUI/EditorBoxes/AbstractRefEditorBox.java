package TaxService.CustomUI.EditorBoxes;

import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.AbstractRefDAO;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractRefEditorBox<T extends AbstractRefDAO> extends AbstractEditorBox<T>
{
	private TextField name1, name2;

	public AbstractRefEditorBox(Class<T> clazz)
	{
		super(clazz);

		name1 = new TextField();
		name1.setPrefWidth(200);
		setLengthLimit(name1, 100);
		name2 = new TextField();
		name2.setPrefWidth(200);
		setLengthLimit(name2, 100);
		addField("Наименование", name1, name2, false);
	}

	@Override
	public T withdrawPrimaryAll()
	{
		String name = name1.getText().trim();

		try
		{
			return clazz.getDeclaredConstructor(String.class).newInstance(name);
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/*@Override
	public T withdrawSecondaryAll()
	{
		String name = name2.getText().trim();

		try
		{
			return clazz.getDeclaredConstructor(String.class).newInstance(name);
		}
		catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}*/

	@Override
	public Pair<T, List<Field>> withdrawPrimaryFilled()
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

			T dao = clazz.getDeclaredConstructor(String.class).newInstance(name);
			dao.id = id;
			return new Pair<>(dao, filledFields);
		}
		catch (NoSuchFieldException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Pair<T, List<Field>> withdrawSecondaryFilled()
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

			return new Pair<>(clazz.getDeclaredConstructor(String.class).newInstance(name), filledFields);
		}
		catch (NoSuchFieldException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean validatePrimary(boolean allRequired)
	{
		return validateTextField(name1, allRequired);
	}

	@Override
	public boolean validateSecondary(boolean allRequired)
	{
		return validateTextField(name2, allRequired);
	}

	@Override
	public int countFilledPrimary()
	{
		int count = 0;
		if (!id1.getText().trim().isEmpty())
			++count;
		if (!name1.getText().trim().isEmpty())
			++count;
		return count;
	}

	@Override
	public int countFilledSecondary()
	{
		int count = 0;
		if (!name2.getText().trim().isEmpty())
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
		name2.clear();
		name2.setEffect(null);
	}

	@Override
	public void bindDataSources(Map<Class<AbstractDAO>, ObservableList> sources){}
}
