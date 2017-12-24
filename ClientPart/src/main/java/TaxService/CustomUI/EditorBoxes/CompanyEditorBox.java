package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.Company;
import TaxService.DAOs.Owntype;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.math.BigDecimal;

public class CompanyEditorBox //extends AbstractEditorBox<Company>
{
	private TextField name1, name2;
	private ComboBox<Owntype> owntype1, owntype2;
	private MaskField phone1, phone2;
	private MaskField startyear1, startyear2;
	private TextField statesize1, statesize2;
	
	/*public CompanyEditorBox()
	{
		super(Company.class);

		name1 = new TextField();
		name1.setPrefWidth(200);
		name2 = new TextField();
		name2.setPrefWidth(200);
		addField("Название", name1, name2);
		
		owntype1 = new ComboBox<>();
		owntype1.setPrefWidth(170);
		owntype1.setEditable(true);
		owntype2 = new ComboBox<>();
		owntype2.setPrefWidth(170);
		owntype2.setEditable(true);
		addField("Форма собственности", owntype1, owntype2);

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

		statesize1 = new TextField();
		statesize1.setPrefWidth(100);
		statesize2 = new TextField();
		statesize2.setPrefWidth(100);
		addField("Штат", statesize1, statesize2);
	}*/
}
