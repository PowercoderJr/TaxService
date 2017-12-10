package TaxService.SceneControllers;

import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.DAOs.*;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateOrder;
import TaxService.Orders.ReadHundredOrder;
import TaxService.DAOs.AbstractDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class MainController
{
	@FXML
	public GridPane tableGrid;
	@FXML
	public TableView actuallyTable;
	public MenuItem switchToDepartmentMenuItem;
	public MenuItem switchToEmployeeMenuItem;
	public MenuItem switchToCompanyMenuItem;
	public MenuItem switchToPaymentMenuItem;
	public MenuItem switchToDeptypeMenuItem;
	public MenuItem switchToEducationMenuItem;
	public MenuItem switchToOwntypeMenuItem;
	public MenuItem switchToPaytypeMenuItem;

	private Callback onTableContentReceived;

	public void initialize()
	{
		Label ph = new Label("НЕТ ЗАПИСЕЙ");
		actuallyTable.setPlaceholder(ph);

		onTableContentReceived = new Callback()
		{
			@Override
			public void callback(Object o)
			{

				//https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TableView.html
				//https://stackoverflow.com/questions/2126714/java-get-all-variable-names-in-a-class

				/*ObservableList<Person> teamMembers = FXCollections.observableList( (List) o);
				table.setItems(teamMembers);

				TableColumn<Person,String> firstNameCol = new TableColumn<Person,String>("First Name");
				firstNameCol.setCellValueFactory(new Callback<CellDataFeatures<Person, String>, ObservableValue<String>>()
				{
					public ObservableValue<String> call(CellDataFeatures<Person, String> p)
					{
						// p.getValue() returns the Person instance for a particular TableView row
						return p.getValue().firstNameProperty();
					}
				});*/

				actuallyTable.setItems(FXCollections.observableList( (List) o));
			}
		};
		ClientAgent.subscribeTableContentReceived(onTableContentReceived);

		switchToDepartmentMenuItem.setOnAction(e -> switchActiveTable(Department.class));
		switchToEmployeeMenuItem.setOnAction(e -> switchActiveTable(Employee.class));
		switchToCompanyMenuItem.setOnAction(e -> switchActiveTable(Company.class));
		switchToPaymentMenuItem.setOnAction(e -> switchActiveTable(Payment.class));
		switchToDeptypeMenuItem.setOnAction(e -> switchActiveTable(Deptype.class));
		switchToEducationMenuItem.setOnAction(e -> switchActiveTable(Education.class));
		switchToOwntypeMenuItem.setOnAction(e -> switchActiveTable(Owntype.class));
		switchToPaytypeMenuItem.setOnAction(e -> switchActiveTable(Paytype.class));
	}

	public void exit(ActionEvent actionEvent)
	{
		ClientAgent.unsubscribeTableContentReceived(onTableContentReceived);
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
		CreateOrder<Deptype> order = new CreateOrder<Deptype>(Deptype.class, deptype);
		ClientAgent.getInstance().send(order);
	}

	public void switchActiveTable(Class<? extends AbstractDAO> clazz)
	{
		ClientAgent.getInstance().send(new ReadHundredOrder(clazz, 0));
	}
}
