package TaxService.CustomUI.EditorBoxes;

import TaxService.DAOs.AbstractDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public abstract class AbstractEditorBox<T extends AbstractDAO> extends ScrollPane
{
	private Class<T> clazz;

	private HBox primaryFieldsBox, secondaryFieldsBox;
	private VBox fieldsBox;
	private HBox longBox;

	public AbstractEditorBox(Class<T> clazz)
	{
		this.clazz = clazz;

		//From deepest to general

		primaryFieldsBox = new HBox(5.0);
		primaryFieldsBox.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(primaryFieldsBox, Priority.ALWAYS);
		secondaryFieldsBox = new HBox(5.0);
		secondaryFieldsBox.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(secondaryFieldsBox, Priority.ALWAYS);

		fieldsBox = new VBox(5.0);
		fieldsBox.getChildren().addAll(primaryFieldsBox, secondaryFieldsBox);

		longBox = new HBox(5.0);
		longBox.setPadding(new Insets(5.0));
		longBox.getChildren().add(fieldsBox);

		this.setContent(longBox);
		setFitToHeight(true);
		setVbarPolicy(ScrollBarPolicy.NEVER);
		HBox.setHgrow(this, Priority.ALWAYS);
	}

	protected void addField(String name, Node primary, Node secondary)
	{
		VBox primaryBox = new VBox(5.0);
		primaryBox.getChildren().addAll(new Label(name), primary);
		primaryFieldsBox.getChildren().add(primaryBox);
		secondaryFieldsBox.getChildren().add(secondary);
	}
}
