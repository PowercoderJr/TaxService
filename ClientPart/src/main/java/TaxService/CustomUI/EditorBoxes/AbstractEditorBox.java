package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.Deptype;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateOrder;
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
import javafx.scene.effect.Effect;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class AbstractEditorBox<T extends AbstractDAO> extends ScrollPane
{
	private static final double SPACING = 5.0;
	protected enum FieldType {PRIMARY, SECONDARY}

	private Class<T> clazz;

	private HBox primaryFieldsBox, secondaryFieldsBox;
	private VBox fieldsBox;
	private HBox longBox;
	protected TextField id1, id2;

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
		boolean isValid = !isRequired && field.getText().isEmpty() || !field.getText().isEmpty();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	protected boolean validateMaskField(MaskField field, boolean isRequired)
	{
		boolean isValid = !isRequired && field.getPlainText().isEmpty() || !field.getPlainText().isEmpty();
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

	protected void markAsInvalid(Node field)
	{
		field.setEffect(new ColorAdjust(0, 0.5, 0, 0));
	}

	public void add()
	{
		if (validatePrimary(true))
		{
			T dao = withdrawFields();
			ClientAgent.getInstance().send(new CreateOrder<T>(clazz, ClientAgent.getInstance().getLogin(), dao));
		}
	}

	public int update()
	{
		return 0;
	}

	public int filter()
	{
		return 0;
	}

	public int remove()
	{
		return 0;
	}

	public void depositFields(T dao)
	{
	}

	public T withdrawFields()
	{
		return null;
	}

	public boolean validatePrimary(boolean isRequired)
	{
		return isRequired;
	}

	public boolean validateSecondary(boolean isRequired)
	{
		return isRequired;
	}

	public void clearAll()
	{
	}


}
