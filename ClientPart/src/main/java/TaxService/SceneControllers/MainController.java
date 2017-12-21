package TaxService.SceneControllers;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.CustomUI.EditorBoxes.AbstractEditorBox;
import TaxService.CustomUI.EditorBoxes.DepartmentEditorBox;
import TaxService.DAOs.*;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadPortionOrder;
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
import javafx.scene.layout.HBox;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;


public class MainController
{
	private static Map<Class<? extends AbstractDAO>, TableStaff> niceTableNames = new HashMap<>();
	public HBox editorBoxBox;

	private class TableStaff
	{
		private Class<? extends AbstractDAO> clazz;
		private String niceName;
		private TableView tableView;
		private HBox editorBox;
	}

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
	public TextField portionField;

	private Callback onPortionReceived;
	private int currPortion;

	public void initialize()
	{
		ClientMain.sceneManager.getStage().setTitle("База данных налоговой инспекции");
		ClientMain.sceneManager.getStage().setResizable(true);
		ClientMain.sceneManager.getStage()
				.setX((Toolkit.getDefaultToolkit().getScreenSize().width - ClientMain.DEFAULT_WINDOW_WIDTH) / 2);
		ClientMain.sceneManager.getStage()
				.setY((Toolkit.getDefaultToolkit().getScreenSize().height - ClientMain.DEFAULT_WINDOW_HEIGHT) / 2);

		UnaryOperator<TextFormatter.Change> digitsFilter = change ->
		{
			String newText = change.getControlNewText();
			if (newText.matches("\\d*"))
				return change;
			else
				return null;
		};
		portionField.setTextFormatter(new TextFormatter<Integer>(digitsFilter));

		onPortionReceived = new Callback()
		{
			@Override
			public void callback(Object o)
			{
				PortionDelivery<AbstractDAO> delivery = (PortionDelivery<AbstractDAO>) o;
				Platform.runLater(() ->
				{
					actuallyTable.getItems().clear();
					actuallyTable.getColumns().clear();
					actuallyTable.getColumns().addAll(TableColumnsBuilder.buildForDAO(delivery.getContentClazz()));
					actuallyTable.setItems(FXCollections.observableList(delivery.getContent()));
					statusLabel.setText("Отображены записи с " + delivery.getFirst() + " по " + delivery.getLast()
							+ " из " + delivery.getTotal());
					currPortion = delivery.getFirst() / AbstractCRUD.PORTION_SIZE + 1;
					actuallyTable.scrollTo(0);
				});
			}
		};
		ClientAgent.subscribePortionReceived(onPortionReceived);

		Class[] tableClazzes = {Department.class, Employee.class, Company.class, Payment.class, Deptype.class,
								Post.class, Education.class, Owntype.class, Paytype.class};
		/*Platform.runLater(() ->
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
		});*/

		editorBoxBox.getChildren().add(0, new DepartmentEditorBox());
		switchActiveTable(Department.class);
	}


	private void initTableStaff(Class<? extends AbstractDAO> tableClazz)
	{
		String name = tableClazz.getSimpleName();
		Label placeholder = new Label("НЕТ ЗАПИСЕЙ");

		//Init TableView
		TableView tv = new TableView();
		tv.getColumns().addAll(TableColumnsBuilder.buildForDAO(tableClazz));

		//Getting editor box
	}

	private void initTableMenuItem(MenuItem item, Class<? extends AbstractDAO> tableClazz)
	{
		//item.setText(ClientMain.getNiceTableName(tableClazz));//TODO
		item.setOnAction(e -> switchActiveTable(tableClazz));
	}

	public void exit(ActionEvent actionEvent)
	{
		ClientAgent.unsubscribePortionReceived(onPortionReceived);
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
		ClientAgent.getInstance().send(new ReadPortionOrder(tableClazz, ClientAgent.getInstance().getLogin(), 1));
		ClientAgent.getInstance().setCurrTable(tableClazz);
		//currTableLabel.setText(ClientMain.getNiceTableName(tableClazz));//TODO
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
		gotoPage(currPortion - 1);
	}

	public void onPortionSpinnerKeyReleased(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
		{
			int portion = portionField.getText().isEmpty() ? currPortion : Integer.parseInt(portionField.getText());
			if (portion < 1)
				portion = 1;
			gotoPage(portion);
		}
		else
			refreshSpecificPageLabel();
	}

	public void gotoNextPage(ActionEvent actionEvent)
	{
		gotoPage(currPortion + 1);
	}

	public void gotoLastPage(ActionEvent actionEvent)
	{
		gotoPage(Integer.MAX_VALUE);
	}

	private void gotoPage(int page)
	{
		ClientAgent agent = ClientAgent.getInstance();
		ReadPortionOrder order = new ReadPortionOrder<>(agent.getCurrTable(), agent.getLogin(), page);
		ClientAgent.getInstance().send(order);
	}

	private void refreshSpecificPageLabel()
	{
		int portion = portionField.getText().isEmpty() ? currPortion : Integer.parseInt(portionField.getText());
		if (portion < 1)
			portion = 1;
		specificPageLabel.setText("(" + ((portion - 1) * AbstractCRUD.PORTION_SIZE + 1) + " - " + (portion * AbstractCRUD.PORTION_SIZE) + ")");
	}
}
