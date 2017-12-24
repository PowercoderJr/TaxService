package TaxService.SceneControllers;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.CustomUI.EditorBoxes.*;
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
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;


public class MainController
{
	private static Map<Class<? extends AbstractDAO>, TableStaff> tableStaffs = new HashMap<>();

	private class TableStaff<T extends AbstractDAO>
	{
		private Class<T> clazz;
		private String niceName;
		private TableView<T> tableView;
		private AbstractEditorBox<T> editorBox;
		public int currPortion;

		public TableStaff(Class<T> clazz, String niceName, TableView<T> tableView, AbstractEditorBox<T> editorBox)
		{
			this.clazz = clazz;
			this.niceName = niceName;
			this.tableView = tableView;
			this.editorBox = editorBox;
			this.currPortion = 1;
		}

		public Class<T> getClazz()
		{
			return clazz;
		}

		public String getNiceName()
		{
			return niceName;
		}

		public TableView<T> getTableView()
		{
			return tableView;
		}

		public AbstractEditorBox<T> getEditorBox()
		{
			return editorBox;
		}

		public int getCurrPortion()
		{
			return currPortion;
		}
	}

	@FXML
	public BorderPane borderPane;
	@FXML
	public HBox editorBoxBox;
	@FXML
	public Label currTableLabel;
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
	private Callback onExceptionReceived;
	private Class<? extends AbstractDAO> currTable;

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

		onExceptionReceived = new Callback()
		{
			@Override
			public void callback(Object o)
			{
				Platform.runLater(() ->
				{
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Ошибка");
					alert.setHeaderText("При выполнении операции произошла ошибка");
					alert.setContentText((o).toString());
					alert.showAndWait();
				});
			}
		};
		ClientAgent.subscribeExceptionReceived(onExceptionReceived);

		onPortionReceived = new Callback()
		{
			@Override
			public void callback(Object o)
			{
				PortionDelivery<AbstractDAO> delivery = (PortionDelivery<AbstractDAO>) o;
				Platform.runLater(() ->
				{
					TableView tv = tableStaffs.get(currTable).getTableView();
					tv.getItems().clear();
					tv.getColumns().clear();
					tv.getColumns().addAll(TableColumnsBuilder.buildForDAO(delivery.getContentClazz()));
					tv.setItems(FXCollections.observableList(delivery.getContent()));
					statusLabel.setText("Отображены записи с " + delivery.getFirst() + " по " + delivery.getLast()
							+ " из " + delivery.getTotal());
					tableStaffs.get(currTable).currPortion = delivery.getFirst() / AbstractCRUD.PORTION_SIZE + 1;
					//tv.scrollTo(0);
				});
			}
		};
		ClientAgent.subscribePortionReceived(onPortionReceived);

		Platform.runLater(() ->
		{
			initTableStaff(Department.class, "Отделения налоговой инспекции");
			/*initTableStaff(Employee.class, "Сотрудники налоговой инспекции");
			initTableStaff(Company.class, "Предприятия-плательщики");
			initTableStaff(Payment.class, "Платежи");*/
			initTableStaff(Deptype.class, "Типы отделений");
			initTableStaff(Post.class, "Должности налоговой инспекции");
			initTableStaff(Education.class, "Степени образования");
			initTableStaff(Owntype.class, "Виды собственности");
			initTableStaff(Paytype.class, "Виды платежей");
			switchActiveTable(Department.class);
		});
	}

	private void initTableStaff(Class<? extends AbstractDAO> tableClazz, String niceName)
	{
		try
		{
			String clazzName = tableClazz.getSimpleName();

			//Init TableView
			TableView tv = new TableView();
			BorderPane.setMargin(tv, new Insets(0, 20, 0, 20));
			tv.setPlaceholder(new Label("НЕТ ЗАПИСЕЙ"));
			tv.getColumns().addAll(TableColumnsBuilder.buildForDAO(tableClazz));

			//Init editor box
			AbstractEditorBox eb = (AbstractEditorBox) Class.forName("TaxService.CustomUI.EditorBoxes." + clazzName + "EditorBox").getConstructor().newInstance();
			editorBoxBox.getChildren().add(eb);
			eb.setVisible(false);
			eb.setManaged(false);

			//Menu item
			MenuItem item = (MenuItem) getClass().getField("switchTo" + clazzName + "MenuItem").get(this);
			item.setText(niceName);
			item.setOnAction(e -> switchActiveTable(tableClazz));

			tableStaffs.put(tableClazz, new TableStaff(tableClazz, niceName, tv, eb));
		}
		catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException | NoSuchFieldException e)
		{
			e.printStackTrace();
		}
	}

	public void addBtnClicked(ActionEvent actionEvent)
	{
		tableStaffs.get(currTable).getEditorBox().add();
	}

	public void updateBtnClicked(ActionEvent actionEvent)
	{
		tableStaffs.get(currTable).getEditorBox().validatePrimary(false);
	}

	public void filterBtnClicked(ActionEvent actionEvent)
	{
		tableStaffs.get(currTable).getEditorBox().filter();
	}

	public void deleteBtnClicked(ActionEvent actionEvent)
	{
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

	public void switchActiveTable(Class<? extends AbstractDAO> tableClazz)
	{
		if (currTable != null)
		{
			AbstractEditorBox eb = tableStaffs.get(currTable).getEditorBox();
			eb.setVisible(false);
			eb.setManaged(false);
		}

		TableStaff staff = tableStaffs.get(tableClazz);
		ClientAgent.getInstance().send(new ReadPortionOrder(tableClazz, ClientAgent.getInstance().getLogin(), 1,
				false, staff.getEditorBox().getFilter()));

		staff.getEditorBox().setVisible(true);
		staff.getEditorBox().setManaged(true);
		borderPane.setCenter(staff.getTableView());

		currTable = tableClazz;
		currTableLabel.setText(staff.getNiceName());
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
		gotoPage(tableStaffs.get(currTable).currPortion - 1);
	}

	public void onPortionSpinnerKeyReleased(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
		{
			int portion = portionField.getText().isEmpty() ? tableStaffs.get(currTable).currPortion : Integer.parseInt(portionField.getText());
			if (portion < 1)
				portion = 1;
			gotoPage(portion);
		}
		else
			refreshSpecificPageLabel();
	}

	public void gotoNextPage(ActionEvent actionEvent)
	{
		gotoPage(tableStaffs.get(currTable).currPortion + 1);
	}

	public void gotoLastPage(ActionEvent actionEvent)
	{
		gotoPage(Integer.MAX_VALUE);
	}

	private void gotoPage(int page)
	{
		ReadPortionOrder order = new ReadPortionOrder<>(currTable, ClientAgent.getInstance().getLogin(), page, false,
				tableStaffs.get(currTable).getEditorBox().getFilter());
		ClientAgent.getInstance().send(order);
	}

	private void refreshSpecificPageLabel()
	{
		int portion = portionField.getText().isEmpty() ? tableStaffs.get(currTable).currPortion : Integer.parseInt(portionField.getText());
		if (portion < 1)
			portion = 1;
		specificPageLabel.setText("(" + ((portion - 1) * AbstractCRUD.PORTION_SIZE + 1) + " - " + (portion * AbstractCRUD.PORTION_SIZE) + ")");
	}
}
