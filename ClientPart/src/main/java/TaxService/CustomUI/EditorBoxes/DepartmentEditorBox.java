package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.Department;
import TaxService.DAOs.Deptype;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class DepartmentEditorBox extends AbstractEditorBox<Department>
{
	private TextField id1, id2;
	private ComboBox<Deptype> deptype1, deptype2;
	private TextField name1, name2;
	private TextField startyear1, startyear2;
	private MaskField phone1, phone2;
	private TextField city1, city2;
	private TextField street1, street2;
	private TextField house1, house2;

	public DepartmentEditorBox()
	{
		super(Department.class);

		id1 = new TextField();
		id1.setPrefWidth(80);
		id2 = new TextField();
		id2.setPrefWidth(80);
		addField("ID", id1, id2);

		deptype1 = new ComboBox<>();
		deptype1.setPrefWidth(150);
		deptype1.setEditable(true);
		deptype2 = new ComboBox<>();
		deptype2.setPrefWidth(150);
		deptype2.setEditable(true);
		addField("Тип отделения", deptype1, deptype2);

		name1 = new TextField();
		name1.setPrefWidth(200);
		name2 = new TextField();
		name2.setPrefWidth(200);
		addField("Название", name1, name2);

		startyear1 = new TextField();
		startyear1.setPrefWidth(100);
		startyear2 = new TextField();
		startyear2.setPrefWidth(100);
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
}
