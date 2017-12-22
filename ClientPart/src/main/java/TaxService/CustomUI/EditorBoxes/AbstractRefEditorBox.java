package TaxService.CustomUI.EditorBoxes;

import TaxService.DAOs.AbstractRefDAO;
import javafx.scene.control.TextField;

public class AbstractRefEditorBox<T extends AbstractRefDAO> extends AbstractEditorBox<T>
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
}
