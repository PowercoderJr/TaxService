package TaxService.SceneControllers;

import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.DAOs.*;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.AbstractOrder;
import TaxService.Orders.CreateOrder;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

public class MainController
{
	public GridPane tableGrid;
	public TableView actuallyTable;

	private Callback onSelect;

	public void initialize()
	{
		Label ph = new Label("НЕТ ЗАПИСЕЙ");
		actuallyTable.setPlaceholder(ph);

		onSelect = new Callback()
		{
			@Override
			public void callback(Object o)
			{

			}
		};
		ClientAgent.subscribeSelect(onSelect);
	}

	public void exit(ActionEvent actionEvent)
	{
		try
		{
			ClientMain.sceneManager.popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	public void testCreateDepType(ActionEvent actionEvent)
	{
		Deptype deptype = new Deptype("Осторожно, оно работает!");
		/*BigDecimal bigDecimal = new BigDecimal(2000);
		Department department = new Department("Кековское", deptype, bigDecimal, "+380914564564", "Danger", "Синяя", "13");
		CreateOrder<Department> order = new CreateOrder<Department>(department, Department.class);*/
		CreateOrder<Deptype> order = new CreateOrder<Deptype>(deptype, Deptype.class);
		ClientAgent.getInstance().send(order);
	}
}
