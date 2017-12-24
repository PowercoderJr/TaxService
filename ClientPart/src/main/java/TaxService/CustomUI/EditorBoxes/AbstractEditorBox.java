package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.AbstractDAO;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateOrder;
import TaxService.Orders.ReadPortionOrder;
import TaxService.Orders.DeleteOrder;
import TaxService.Utils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;

public abstract class AbstractEditorBox<T extends AbstractDAO> extends ScrollPane
{
	private static final double SPACING = 5.0;

	//protected enum FieldType {PRIMARY, SECONDARY}

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
		addField("ID", id1, id2);


		setFitToHeight(true);
	}

	protected void addField(String name, Node primary, Node secondary)
	{
		VBox primaryBox = new VBox(SPACING);
		primaryBox.getChildren().addAll(new Label(name), primary);
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

	protected boolean validateTextField(TextField field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getText().trim().isEmpty() || !field.getText().trim().isEmpty();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	protected boolean validateMaskField(MaskField field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getPlainText().trim().isEmpty() || field.getPlainText()
				.length() == StringUtils.countMatches(field.getWhatMask(), MaskField.WHAT_MASK_CHAR);
		field.getWhatMask();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	protected boolean validateComboBox(ComboBox field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getSelectionModel().isEmpty() || !field.getSelectionModel().isEmpty();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	private boolean validateId1(boolean isRequired)
	{
		boolean isValid;
		try
		{
			isValid = !isRequired && id1.getText().isEmpty() || !id1.getText().isEmpty() &&
					Integer.parseInt(id1.getText()) > 0;
		}
		catch (Exception e)
		{
			isValid = false;
		}
		if (!isValid)
			markAsInvalid(id1);
		return isValid;
	}

	protected void markAsInvalid(Node field)
	{
		field.setEffect(new ColorAdjust(0, 0.5, 0, 0));
	}

	public void create()
	{
		if (validatePrimary(true))
		{
			T dao = withdrawPrimaryAll();
			ClientAgent.getInstance().send(new CreateOrder<>(clazz, ClientAgent.getInstance().getLogin(), dao));
		}
	}

	public void update()
	{
	}

	//TODO: ignore case?
	public void filter()
	{
		if (validatePrimary(false) & validateId1(false))
		{
			Pair<T, List<Field>> pair = withdrawPrimaryFilled();
			if (pair.getValue().isEmpty())
				filter = "";
			else
			{
				String colNames = Utils.fieldNamesToString(pair.getValue().stream());
				String values = Utils.fieldValuesToString(pair.getValue().stream(), pair.getKey());
				filter = " WHERE (" + colNames + ") = (" + values + ")";
			}
			ClientAgent.getInstance().send(new ReadPortionOrder<>(clazz, ClientAgent.getInstance().getLogin(), 1, false, filter));
		}
	}

	public void delete()
	{
		if (validatePrimary(false) & validateId1(false))
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
			ClientAgent.getInstance().send(new DeleteOrder<>(clazz, ClientAgent.getInstance().getLogin(), localFilter));
		}
	}

	public abstract void depositPrimary(T dao);

	public abstract T withdrawPrimaryAll();

	public abstract T withdrawSecondaryAll();

	public abstract Pair<T, List<Field>> withdrawPrimaryFilled();

	public abstract Pair<T, List<Field>> withdrawSecondaryFilled();

	public abstract boolean validatePrimary(boolean allRequired);

	public abstract boolean validateSecondary(boolean allRequired);

	public abstract void clearAll();

	public String getFilter()
	{
		return filter;
	}
}
