package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.AbstractDAO;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateOrder;
import TaxService.Orders.DeleteOrder;
import TaxService.Orders.UpdateOrder;
import TaxService.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public abstract class AbstractEditorBox<T extends AbstractDAO> extends ScrollPane
{
	private static final double SPACING = 5.0;
	private static Effect invalidEffect = new ColorAdjust(0, 0.5, 0, 0);
	private static Tooltip autofillableTooltip = new Tooltip("При добавлении записи это поле генерируется автоматически");

	protected Class<T> clazz;

	private HBox primaryFieldsBox, secondaryFieldsBox;
	private VBox fieldsBox;
	private HBox longBox;
	protected TextField id1, id2;
	protected String filter;

	public AbstractEditorBox(Class<T> clazz)
	{
		this.clazz = clazz;

		//From deepest to general

		primaryFieldsBox = new HBox(SPACING);
		primaryFieldsBox.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(primaryFieldsBox, Priority.ALWAYS);
		secondaryFieldsBox = new HBox(SPACING);
		secondaryFieldsBox.setAlignment(Pos.CENTER_LEFT);
		secondaryFieldsBox.setVisible(false);
		HBox.setHgrow(secondaryFieldsBox, Priority.ALWAYS);

		fieldsBox = new VBox(SPACING);
		fieldsBox.getChildren().addAll(primaryFieldsBox, secondaryFieldsBox);

		longBox = new HBox(SPACING);
		longBox.setPadding(new Insets(SPACING));
		longBox.getChildren().add(fieldsBox);

		this.setContent(longBox);
		setVbarPolicy(ScrollBarPolicy.NEVER);
		HBox.setHgrow(this, Priority.ALWAYS);

		//

		id1 = new TextField();
		id1.setPrefWidth(80);
		id2 = new TextField();
		id2.setPrefWidth(80);
		id2.setVisible(false);
		addField("ID", id1, id2, true);


		setFitToHeight(true);
	}

	protected void addField(String name, Node primary, Node secondary, boolean autofillable)
	{
		VBox primaryBox = new VBox(SPACING);
		Label label = new Label(name);
		if (autofillable)
		{
			label.setTextFill(Color.web("#42A642"));
			label.setTooltip(autofillableTooltip);
		}
		primaryBox.getChildren().addAll(label, primary);
		primaryFieldsBox.getChildren().add(primaryBox);
		primary.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
				{
					primary.setEffect(null);
				}
			}
		});

		secondaryFieldsBox.getChildren().add(secondary);
		secondary.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
				{
					secondary.setEffect(null);
				}
			}
		});
	}

	public void setSecondaryVisible(boolean value)
	{
		secondaryFieldsBox.setVisible(value);
	}

	static boolean validateTextField(TextField field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getText().trim().isEmpty() || !field.getText().trim().isEmpty();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	static boolean validateTextPositiveIntField(TextField field, boolean isRequired)
	{
		boolean isValid;
		try
		{
			isValid = !isRequired && field.getText().trim().isEmpty() || !field.getText().trim().isEmpty() &&
					Integer.parseInt(field.getText()) > 0;
		}
		catch (Exception e)
		{
			isValid = false;
		}
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	static boolean validateTextPositiveFloatField(TextField field, boolean isRequired)
	{
		boolean isValid;
		try
		{
			isValid = !isRequired && field.getText().trim().isEmpty() || !field.getText().trim().isEmpty() &&
					Float.parseFloat(field.getText().replace(',', '.')) > 0;
		}
		catch (Exception e)
		{
			isValid = false;
		}
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	static boolean validateMaskField(MaskField field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getPlainText().trim().isEmpty() || field.getPlainText()
				.length() == StringUtils.countMatches(field.getWhatMask(), MaskField.WHAT_MASK_CHAR);
		field.getWhatMask();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	static boolean validateComboBox(ComboBox field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getSelectionModel().isEmpty() || !field.getSelectionModel().isEmpty();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	static void setLengthLimit(TextField textField, int limit)
	{
		UnaryOperator<TextFormatter.Change> modifyChange = c ->
		{
			if (c.isContentChange())
			{
				int newLength = c.getControlNewText().length();
				if (newLength > limit)
				{
					int oldLength = c.getControlText().length();
					if (oldLength == limit)
					{
						int rangeStart = c.getRangeStart();
						c.setText("");
						c.setRange(rangeStart, rangeStart);
						c.setCaretPosition(rangeStart);
						c.selectRange(rangeStart, rangeStart);
					}
					else
					{
						c.setText(c.getControlNewText().substring(0, limit));
						c.setRange(0, oldLength);
					}
				}
			}
			return c;
		};
		textField.setTextFormatter(new TextFormatter(modifyChange));
	}

	public boolean validateId1(boolean isRequired)
	{
		return validateTextPositiveIntField(id1, isRequired);
	}

	protected static void markAsInvalid(Node field)
	{
		field.setEffect(invalidEffect);
	}

	public boolean create()
	{
		T dao = withdrawPrimaryAll();
		ClientAgent.getInstance().send(new CreateOrder<>(clazz, dao));
		return true;
	}

	public boolean update()
	{
		Pair<T, List<Field>> primaryPair = withdrawPrimaryFilled();
		String localFilter;
		if (primaryPair.getValue().isEmpty())
			localFilter = "";
		else
		{
			String colNames = Utils.fieldNamesToString(primaryPair.getValue().stream());
			String values = Utils.fieldValuesToString(primaryPair.getValue().stream(), primaryPair.getKey());
			localFilter = " WHERE (" + colNames + ") = (" + values + ")";
		}

		Pair<T, List<Field>> secondaryPair = withdrawSecondaryFilled();
		String newValues;
		String colNames = Utils.fieldNamesToString(secondaryPair.getValue().stream());
		String values = Utils.fieldValuesToString(secondaryPair.getValue().stream(), secondaryPair.getKey());
		newValues = "(" + colNames + ") = (" + values + ")";
		ClientAgent.getInstance().send(new UpdateOrder<>(clazz, localFilter, newValues));
		return true;
	}

	//TODO: ignore case?
	public boolean setFilter()
	{
		String oldFilter = filter;
		Pair<T, List<Field>> pair = withdrawPrimaryFilled();
		if (pair.getValue().isEmpty())
			filter = "";
		else
		{
			String colNames = Utils.fieldNamesToString(pair.getValue().stream());
			String values = Utils.fieldValuesToString(pair.getValue().stream(), pair.getKey());
			filter = " WHERE (" + colNames + ") = (" + values + ")";
		}
		return !filter.equals(oldFilter);
	}

	public boolean delete()
	{
		Pair<T, List<Field>> pair = withdrawPrimaryFilled();
		String localFilter;
		if (pair.getValue().isEmpty())
			localFilter = "";
		else
		{
			String colNames = Utils.fieldNamesToString(pair.getValue().stream());
			String values = Utils.fieldValuesToString(pair.getValue().stream(), pair.getKey());
			localFilter = " WHERE (" + colNames + ") = (" + values + ")";
		}
		ClientAgent.getInstance().send(new DeleteOrder<>(clazz, localFilter));
		return true;
	}

	//public abstract void depositPrimary(T dao);

	public abstract T withdrawPrimaryAll();

	//public abstract T withdrawSecondaryAll();

	public abstract Pair<T, List<Field>> withdrawPrimaryFilled();

	public abstract Pair<T, List<Field>> withdrawSecondaryFilled();

	public abstract boolean validatePrimary(boolean allRequired);

	public abstract boolean validateSecondary(boolean allRequired);

	public abstract int countFilledPrimary();

	public abstract int countFilledSecondary();

	public abstract void clearAll();

	public String getFilter()
	{
		return filter;
	}

	public abstract void bindDataSources(Map<Class<AbstractDAO>, ObservableList> sources);
}
