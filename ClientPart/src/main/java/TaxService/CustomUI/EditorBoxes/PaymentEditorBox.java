package TaxService.CustomUI.EditorBoxes;

import TaxService.CustomUI.MaskField;
import TaxService.DAOs.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.sql.Date;

public class PaymentEditorBox //extends AbstractEditorBox<Payment>
{
	private ComboBox<Paytype> paytype1, paytype2;
	private DatePicker date1, date2;
	private TextField amount1, amount2;
	private ComboBox<Employee> employee1, employee2;
	private ComboBox<Department> department1, department2;
	private ComboBox<Company> company1, company2;
	
	/*public PaymentEditorBox()
	{
		super(Payment.class);

		paytype1 = new ComboBox<>();
		paytype1.setPrefWidth(170);
		paytype2 = new ComboBox<>();
		paytype2.setPrefWidth(170);
		addField("Тип платежа", paytype1, paytype2);

		date1 = new DatePicker();
		date1.setPrefWidth(120);
		date2 = new DatePicker();
		date2.setPrefWidth(120);
		date2.setVisible(false); //Будет генерироваться автоматически при добавлении записи, изменить нельзя
		addField("Дата", date1, date2);

		amount1 = new TextField();
		amount1.setPrefWidth(100);
		amount2 = new TextField();
		amount2.setPrefWidth(100);
		addField("Сумма", amount1, amount2);

		employee1 = new ComboBox<>();
		employee1.setPrefWidth(200);
		employee2 = new ComboBox<>();
		employee2.setPrefWidth(200);
		addField("Сотрудник-оформитель", employee1, employee2);

		department1 = new ComboBox<>();
		department1.setPrefWidth(200);
		department2 = new ComboBox<>();
		department2.setPrefWidth(200);
		addField("Отделение-оформитель", department1, department2);

		company1 = new ComboBox<>();
		company1.setPrefWidth(200);
		company2 = new ComboBox<>();
		company2.setPrefWidth(200);
		addField("Компания-плательщик", company1, company2);
	}*/
}
