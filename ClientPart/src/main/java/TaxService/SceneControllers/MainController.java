package TaxService.SceneControllers;

import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.DAOs.*;
import TaxService.Deliveries.HundredDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateOrder;
import TaxService.Orders.ReadHundredOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.TableColumnsBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.function.UnaryOperator;

public class MainController
{
	@FXML
	public Label currTableLabel;
	@FXML
	public TableView actuallyTable;
	@FXML
	public MenuItem switchToDepartmentMenuItem;
	@FXML
	public MenuItem switchToEmployeeMenuItem;
	@FXML
	public MenuItem switchToCompanyMenuItem;
	@FXML
	public MenuItem switchToPaymentMenuItem;
	@FXML
	public MenuItem switchToDeptypeMenuItem;
	@FXML
	public MenuItem switchToPostMenuItem;
	@FXML
	public MenuItem switchToEducationMenuItem;
	@FXML
	public MenuItem switchToOwntypeMenuItem;
	@FXML
	public MenuItem switchToPaytypeMenuItem;
	@FXML
	public Label statusLabel;
	@FXML
	public Label specificPageLabel;
	@FXML
	public TextField hundredField;

	private Callback onHundredReceived;
	private int currHundred;

	public void initialize()
	{
		ClientMain.sceneManager.getStage().setTitle("База данных налоговой инспекции");
		ClientMain.sceneManager.getStage().setResizable(true);
		ClientMain.sceneManager.getStage().setX((Toolkit.getDefaultToolkit().getScreenSize().width - ClientMain.DEFAULT_WINDOW_WIDTH) / 2);
		ClientMain.sceneManager.getStage().setY((Toolkit.getDefaultToolkit().getScreenSize().height - ClientMain.DEFAULT_WINDOW_HEIGHT) / 2);

		//https://stackoverflow.com/questions/40472668/numeric-textfield-for-integers-in-javafx-8-with-textformatter-and-or-unaryoperat
		UnaryOperator<TextFormatter.Change> digitsFilter = change ->
		{
			String newText = change.getControlNewText();
			if (newText.matches("[0-9]*"))
				return change;
			else
				return null;
		};
		hundredField.setTextFormatter(new TextFormatter<Integer>(digitsFilter));

		onHundredReceived = new Callback()
		{
			@Override
			public void callback(Object o)
			{
				HundredDelivery<AbstractDAO> delivery = (HundredDelivery<AbstractDAO>) o;
				Platform.runLater(() ->
				{
					actuallyTable.getItems().clear();
					actuallyTable.getColumns().clear();
					actuallyTable.getColumns().addAll(TableColumnsBuilder.buildForDAO(delivery.getContentClazz()));
					actuallyTable.setItems(FXCollections.observableList(delivery.getContent()));
					statusLabel.setText("Отображены записи с " + delivery.getFirst() + " по " + delivery.getLast() +
							" из " + delivery.getTotal());
					currHundred = delivery.getFirst() / 100 + 1;
				});
			}
		};
		ClientAgent.subscribeHundredReceived(onHundredReceived);

		Platform.runLater(() ->
		{
			initTableMenuItem(switchToDepartmentMenuItem, Department.class);
			initTableMenuItem(switchToEmployeeMenuItem, Employee.class);
			initTableMenuItem(switchToCompanyMenuItem, Company.class);
			initTableMenuItem(switchToPaymentMenuItem, Payment.class);
			initTableMenuItem(switchToDeptypeMenuItem, Deptype.class);
			initTableMenuItem(switchToPostMenuItem, Education.class);
			initTableMenuItem(switchToEducationMenuItem, Education.class);
			initTableMenuItem(switchToOwntypeMenuItem, Owntype.class);
			initTableMenuItem(switchToPaytypeMenuItem, Paytype.class);
		});

		switchActiveTable(Department.class);
	}

	private void initTableMenuItem(MenuItem item, Class<? extends AbstractDAO> tableClazz)
	{
		item.setOnAction(e -> switchActiveTable(tableClazz));
		item.setText(ClientMain.getNiceTableName(tableClazz));
	}

	public void exit(ActionEvent actionEvent)
	{
		ClientAgent.unsubscribeHundredReceived(onHundredReceived);
		try
		{
			ClientMain.sceneManager.popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}

	/*public void testCreateDepType(ActionEvent actionEvent)
	{
		Deptype deptype = new Deptype("Осторожно, оно работает!");
		CreateOrder<Deptype> order = new CreateOrder<Deptype>(Deptype.class, ClientAgent.getInstance()
				.getLogin(), deptype);
		ClientAgent.getInstance().send(order);
	}

	public void testCreateDepartment(ActionEvent actionEvent)
	{
		Deptype deptype = new Deptype("Осторожно, оно работает!2");
		CreateOrder<Deptype> order = new CreateOrder<Deptype>(Deptype.class, ClientAgent.getInstance()
				.getLogin(), deptype);
		ClientAgent.getInstance().send(order);
		Department department = new Department("Тест", deptype, new BigDecimal(1999), "+380664564565", "Neverland", "Pushkina", "H1/N1");
		CreateOrder<Department> order2 = new CreateOrder<Department>(Department.class, ClientAgent.getInstance()
				.getLogin(), department);
		ClientAgent.getInstance().send(order2);
	}*/

	public void switchActiveTable(Class<? extends AbstractDAO> tableClazz)
	{
		ClientAgent.getInstance().send(new ReadHundredOrder(tableClazz, ClientAgent.getInstance().getLogin(), 1));
		ClientAgent.getInstance().setCurrTable(tableClazz);
		currTableLabel.setText(ClientMain.getNiceTableName(tableClazz));
	}

	public void fsMode(ActionEvent actionEvent)
	{
		ClientMain.sceneManager.getStage().setFullScreen(true);
	}

	public void gotoFirstPage(ActionEvent actionEvent)
	{
		gotoPage(0);
	}

	public void gotoPrevPage(ActionEvent actionEvent)
	{
		gotoPage(currHundred - 1);
	}

	public void onHundredSpinnerKeyReleased(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
		{
			int hundred = hundredField.getText().isEmpty() ? currHundred : Integer.parseInt(hundredField.getText());
			if (hundred < 1)
				hundred = 1;
			gotoPage(hundred);
		}
		else
			refreshSpecificPageLabel();
	}

	public void gotoNextPage(ActionEvent actionEvent)
	{
		gotoPage(currHundred + 1);
	}

	public void gotoLastPage(ActionEvent actionEvent)
	{
		gotoPage(Integer.MAX_VALUE);
	}

	private void gotoPage(int page)
	{
		ClientAgent agent = ClientAgent.getInstance();
		ReadHundredOrder order = new ReadHundredOrder<>(agent.getCurrTable(), agent.getLogin(), page);
		ClientAgent.getInstance().send(order);
	}

	private void refreshSpecificPageLabel()
	{
		int hundred = hundredField.getText().isEmpty() ? currHundred : Integer.parseInt(hundredField.getText());
		if (hundred < 1)
			hundred = 1;
		specificPageLabel.setText("(" + ((hundred - 1) * 100 + 1) + " - " + (hundred * 100) + ")");
	}
}
