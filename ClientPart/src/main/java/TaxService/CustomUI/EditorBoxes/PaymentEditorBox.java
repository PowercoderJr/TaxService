package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.Date;

public class PaymentEditorBox extends AbstractEditorBox<Payment>
{
	private ComboBox<Paytype> paytype1, paytype2;
	private MaskField date1, date2;
	private TextField amount1, amount2;
	private ComboBox<Employee> employee1, employee2;
	private ComboBox<Department> department1, department2;
	private ComboBox<Company> company1, company2;
	
	public PaymentEditorBox()
	{
		super(Payment.class);

		paytype1 = new ComboBox<>();
		paytype1.setPrefWidth(170);
		paytype1.setEditable(true);
		paytype2 = new ComboBox<>();
		paytype2.setPrefWidth(170);
		paytype2.setEditable(true);
		addField("Тип платежа", paytype1, paytype2);

		date1 = new MaskField();
		date1.setPrefWidth(120);
		date1.setMask("DD.DD.DDDD");
		date1.setWhatMask("##-##-####");
		date1.setPlaceholder("__.__.____");
		date2 = new MaskField();
		date2.setPrefWidth(120);
		/*date2.setMask("DD.DD.DDDD");
		date2.setWhatMask("##-##-####");
		date2.setPlaceholder("__.__.____");*/
		date2.setVisible(false); //Будет генерироваться автоматически при добавлении записи, изменить нельзя
		addField("Дата", date1, date2);

		amount1 = new TextField();
		amount1.setPrefWidth(100);
		amount2 = new TextField();
		amount2.setPrefWidth(100);
		addField("Сумма", amount1, amount2);

		employee1 = new ComboBox<>();
		employee1.setPrefWidth(200);
		employee1.setEditable(true);
		employee2 = new ComboBox<>();
		employee2.setPrefWidth(200);
		employee2.setEditable(true);
		addField("Сотрудник-оформитель", employee1, employee2);

		department1 = new ComboBox<>();
		department1.setPrefWidth(200);
		department1.setEditable(true);
		department2 = new ComboBox<>();
		department2.setPrefWidth(200);
		department2.setEditable(true);
		addField("Отделение-оформитель", department1, department2);

		company1 = new ComboBox<>();
		company1.setPrefWidth(200);
		company1.setEditable(true);
		company2 = new ComboBox<>();
		company2.setPrefWidth(200);
		company2.setEditable(true);
		addField("Компания-плательщик", company1, company2);		
	}
}
