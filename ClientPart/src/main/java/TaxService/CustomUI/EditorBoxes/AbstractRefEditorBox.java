package TaxService.CustomUI.EditorBoxes;

import TaxService.DAOs.AbstractRefDAO;
import javafx.scene.control.TextField;

public abstract class AbstractRefEditorBox<T extends AbstractRefDAO> extends AbstractEditorBox<T>
{
	private TextField name1, name2;

	public AbstractRefEditorBox(Class<T> clazz)
	{
		super(clazz);

		name1 = new TextField();
		name1.setPrefWidth(200);
		name2 = new TextField();
		name2.setPrefWidth(200);
		addField("Наименование", name1, name2);
	}

	@Override
	public boolean validatePrimary(boolean isRequired)
	{
		return validateTextField(name1, isRequired);
	}

	@Override
	public boolean validateSecondary(boolean isRequired)
	{
		return validateTextField(name2, isRequired);
	}
}
