package TaxService.SceneControllers;

import TaxService.CRUDs.DepartmentCRUD;
import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.DAOs.*;
import TaxService.Deliveries.TableContentDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateOrder;
import TaxService.Orders.ReadHundredOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.TableColumnsBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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
				TableContentDelivery<AbstractDAO> delivery = (TableContentDelivery<AbstractDAO>) o;
				List<AbstractDAO> content = delivery.getContent();
				Platform.runLater(() ->
				{
					actuallyTable.getItems().clear();
					actuallyTable.getColumns().clear();
					actuallyTable.getColumns().addAll(TableColumnsBuilder.buildForDAO(delivery.getContentClazz()));
					actuallyTable.setItems(FXCollections.observableList(content));
				});
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
		CreateOrder<Deptype> order = new CreateOrder<Deptype>(Deptype.class, ClientAgent.getInstance().getLogin(), deptype);
		ClientAgent.getInstance().send(order);
	}

	public void testCreateDepartment(ActionEvent actionEvent)
	{
		Deptype deptype = new Deptype("Осторожно, оно работает!2");
		CreateOrder<Deptype> order = new CreateOrder<Deptype>(Deptype.class, ClientAgent.getInstance().getLogin(), deptype);
		ClientAgent.getInstance().send(order);
		Department department = new Department("Тест", deptype, new BigDecimal(1999), "+380664564565", "Neverland", "Pushkina", "H1/N1");
		CreateOrder<Department> order2 = new CreateOrder<Department>(Department.class, ClientAgent.getInstance().getLogin(), department);
		ClientAgent.getInstance().send(order2);
	}

	public void switchActiveTable(Class<? extends AbstractDAO> clazz)
	{
		ClientAgent.getInstance().send(new ReadHundredOrder(clazz, ClientAgent.getInstance().getLogin(), 0));
	}

	public void fsMode(ActionEvent actionEvent)
	{
		ClientMain.sceneManager.getStage().setFullScreen(true);
	}
}
