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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

//import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.UnaryOperator;

import static TaxService.PhraseBook.*;


public class MainController
{
	private static Map<Class<? extends AbstractDAO>, TableStaff> tableStaffs = new HashMap<>();

	private class TableStaff<T extends AbstractDAO>
	{
		private Class<T> clazz;
		private String niceName;
		private TableView<T> tableView;
		private AbstractEditorBox<T> editorBox;
		private int currPortion;
		private boolean isFiltered;

		TableStaff(Class<T> clazz, String niceName, TableView<T> tableView, AbstractEditorBox<T> editorBox)
		{
			this.clazz = clazz;
			this.niceName = niceName;
			this.tableView = tableView;
			this.editorBox = editorBox;
			this.currPortion = 1;
			this.isFiltered = false;
		}
	}

	@FXML
	public GridPane root;
	@FXML
	public MenuBar menuBar;
	@FXML
	public BorderPane borderPane;
	@FXML
	public HBox editorBoxBox;
	@FXML
	public Label currTableLabel;
	@FXML
	public Label filterIndicatorLabel;
	@FXML
	public Label notificationLabel;
	@FXML
	public Button createBtn;
	@FXML
	public Button updateBtn;
	@FXML
	public Button filterBtn;
	@FXML
	public Button deleteBtn;
	@FXML
	public Button clearBtn;
	@FXML
	public Button refreshBtn;
	@FXML
	public GridPane updateConfirmPane;
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
	public MenuItem switchToCityMenuItem;
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
	private Callback onNotificationReceived;
	private Callback onConnectionLost;
	private Callback onQueryResultReceived;
	private boolean querySent;
	private Class<? extends AbstractDAO> currTable;
	private static final int NOTIFICATION_DURATION = 3000;
	private final ScheduledExecutorService notificationsScheduler = Executors.newSingleThreadScheduledExecutor();
	private ScheduledFuture<?> hideNotificationFuture;

	public void initialize()
	{
		UnaryOperator<TextFormatter.Change> digitsFilter = change ->
		{
			String newText = change.getControlNewText();
			if (newText.matches("\\d*"))
				return change;
			else
				return null;
		};
		portionField.setTextFormatter(new TextFormatter<Integer>(digitsFilter));
		setVisibleAndManaged(updateConfirmPane, false);
		setVisibleAndManaged(notificationLabel, false);
		setVisibleAndManaged(filterIndicatorLabel, false);

		onPortionReceived = o ->
		{
			PortionDelivery<AbstractDAO> delivery = (PortionDelivery<AbstractDAO>) o;
			Platform.runLater(() ->
			{
				TableView tv = tableStaffs.get(currTable).tableView;
				tv.getItems().clear();
				tv.getColumns().clear();
				tv.getColumns().addAll(TableColumnsBuilder.buildForDAO(delivery.getContentClazz()));
				tv.setItems(FXCollections.observableList(delivery.getContent()));
				statusLabel.setText("Отображены записи с " + delivery.getFirst() + " по " + delivery.getLast()
						+ " из " + delivery.getTotal());
				tableStaffs.get(currTable).currPortion = delivery.getFirst() / AbstractCRUD.PORTION_SIZE + 1;
				tv.scrollTo(0);
			});
		};
		ClientAgent.subscribePortionReceived(onPortionReceived);

		onExceptionReceived = o -> Platform.runLater(() ->
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.initOwner(root.getScene().getWindow());
			alert.setTitle("Ошибка");
			alert.setHeaderText("При выполнении операции произошла ошибка");
			alert.setContentText((o).toString());
			alert.showAndWait();
		});
		ClientAgent.subscribeExceptionReceived(onExceptionReceived);

		onNotificationReceived = o ->
		{
			Platform.runLater(() ->
			{
				notificationLabel.setText(o.toString());
				setVisibleAndManaged(notificationLabel, true);
			});

			if (hideNotificationFuture != null && !hideNotificationFuture.isDone())
				hideNotificationFuture.cancel(true);
			hideNotificationFuture = notificationsScheduler.schedule(() ->
							Platform.runLater(() -> setVisibleAndManaged(notificationLabel, false)),
					NOTIFICATION_DURATION, TimeUnit.MILLISECONDS);
		};
		ClientAgent.subscribeNotificationReceived(onNotificationReceived);

		onConnectionLost = o -> Platform.runLater(() ->
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.initOwner(root.getScene().getWindow());
			alert.setTitle("Потеряно соединение");
			alert.setHeaderText("Потеряно соединение");
			alert.setContentText("Превышен интервал ожидания ответа от сервера.");
			alert.showAndWait();
			disconnect(null);
		});
		ClientAgent.subscribeConnectionLost(onConnectionLost);

		querySent = false;
		onQueryResultReceived = o ->
		{
			Platform.runLater(() ->
			{
				querySent = false;
				ClientAgent.unsubscribeQueryResultReceived(onQueryResultReceived);
				for (Object al : (List)o)
					System.out.println(al);
			});
		};

		initTableStaff(Department.class, "Отделения налоговой инспекции");
		initTableStaff(Employee.class, "Сотрудники налоговой инспекции");
		initTableStaff(Company.class, "Предприятия-плательщики");
		initTableStaff(Payment.class, "Платежи");
		initTableStaff(Deptype.class, "Типы отделений");
		initTableStaff(City.class, "Города");
		initTableStaff(Post.class, "Должности налоговой инспекции");
		initTableStaff(Education.class, "Степени образования");
		initTableStaff(Owntype.class, "Виды собственности");
		initTableStaff(Paytype.class, "Виды платежей");
		switchActiveTable(Department.class);

		ClientAgent.getInstance().startPinging();
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
			editorBoxBox.getChildren().add(0, eb);
			setVisibleAndManaged(eb, false);

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

	public void createBtnClicked(ActionEvent actionEvent)
	{
		AbstractEditorBox eb = tableStaffs.get(currTable).editorBox;
		if (eb.validatePrimary(true))
			if (eb.create())
				refreshCurrTable();
	}

	public void updateBtnClicked(ActionEvent actionEvent)
	{
		switchUpdateMode(true);
	}

	public void updateOkBtnClicked(ActionEvent actionEvent)
	{
		AbstractEditorBox eb = tableStaffs.get(currTable).editorBox;
		if (eb.validatePrimary(false) & eb.validateId1(false) & eb.validateSecondary(false))
		{
			if (eb.countFilledSecondary() > 0)
			{
				Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
				alert.initOwner(root.getScene().getWindow());
				alert.setTitle("Подтвердите действие");
				alert.setHeaderText("Подтвердите действие");
				alert.setContentText("Вы действительно хотите изменить строки таблицы \"" + tableStaffs.get(currTable).niceName + "\" по заданному шаблону?");
				ButtonType myYes = new ButtonType("Да", ButtonBar.ButtonData.YES);
				ButtonType myNo = new ButtonType("Нет", ButtonBar.ButtonData.NO);
				alert.getButtonTypes().setAll(myYes, myNo);
				if (alert.showAndWait().get() == myYes)
				{
					boolean alright = true;
					if (eb.countFilledPrimary() == 0)
					{
						Alert alert2 = new Alert(Alert.AlertType.WARNING);
						alert2.initOwner(root.getScene().getWindow());
						alert2.setTitle("Подтвердите действие");
						alert2.setHeaderText("Вы собираетесь изменить ВСЕ записи таблицы");
						alert2.setContentText("Изменение по пустому шаблону приведёт к полной перезаписи таблицы. Вы точно хотите продолжить?");

						//Фокус в ответ на то, что JavaFX требует много кода для изменения порядка и приоритета кнопок
						ButtonType myNoButYes = new ButtonType("Нет", ButtonBar.ButtonData.YES);
						ButtonType myYesButNo = new ButtonType("Да", ButtonBar.ButtonData.NO);
						alert2.getButtonTypes().setAll(myNoButYes, myYesButNo);
						alright = alert2.showAndWait().get() == myYesButNo;
					}

					if (alright && eb.update())
						refreshCurrTable();
				}
				switchUpdateMode(false);
			}
			else
			{
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.initOwner(root.getScene().getWindow());
				alert.setTitle("Укажите новые значения");
				alert.setHeaderText("Не указано ни одно значение для замены");
				alert.setContentText("Укажите как минимум одно новое значение в появившейся строке для выполнения операции.");
				alert.showAndWait();
			}
		}
	}

	public void updateCancelBtnClicked(ActionEvent actionEvent)
	{
		switchUpdateMode(false);
	}

	public void filterBtnClicked(ActionEvent actionEvent)
	{
		TableStaff staff = tableStaffs.get(currTable);
		AbstractEditorBox eb = staff.editorBox;
		if (eb.validatePrimary(false) & eb.validateId1(false))
			if (eb.setFilter())
			{
				refreshCurrTable();
				staff.isFiltered = eb.countFilledPrimary() > 0;
				setVisibleAndManaged(filterIndicatorLabel, staff.isFiltered);
			}
	}

	public void deleteBtnClicked(ActionEvent actionEvent)
	{
		AbstractEditorBox eb = tableStaffs.get(currTable).editorBox;
		if (eb.validatePrimary(false) & eb.validateId1(false))
		{
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.initOwner(root.getScene().getWindow());
			alert.setTitle("Подтвердите действие");
			alert.setHeaderText("Подтвердите действие");
			alert.setContentText("Вы действительно хотите удалить строки из таблицы \"" + tableStaffs.get(currTable).niceName + "\" по заданному шаблону?");
			ButtonType myYes = new ButtonType("Да", ButtonBar.ButtonData.YES);
			ButtonType myNo = new ButtonType("Нет", ButtonBar.ButtonData.NO);
			alert.getButtonTypes().setAll(myYes, myNo);

			if (alert.showAndWait().get() == myYes)
			{
				boolean alright = true;
				if (eb.countFilledPrimary() == 0)
				{
					Alert alert2 = new Alert(Alert.AlertType.WARNING);
					alert2.initOwner(root.getScene().getWindow());
					alert2.setTitle("Подтвердите действие");
					alert2.setHeaderText("Вы собираетесь удалить ВСЕ записи таблицы");
					alert2.setContentText("Удаление по пустому шаблону приведёт к полной очистке таблицы. Вы точно хотите продолжить?");

					//Фокус в ответ на то, что JavaFX требует много кода для изменения порядка и приоритета кнопок
					ButtonType myNoButYes = new ButtonType("Нет", ButtonBar.ButtonData.YES);
					ButtonType myYesButNo = new ButtonType("Да", ButtonBar.ButtonData.NO);
					alert2.getButtonTypes().setAll(myNoButYes, myYesButNo);
					alright = alert2.showAndWait().get() == myYesButNo;
				}

				if (alright && eb.delete())
					refreshCurrTable();
			}
		}
	}

	public void refreshBtnClicked(ActionEvent actionEvent)
	{
		refreshCurrTable();
	}

	public void clearBtnClicked(ActionEvent actionEvent)
	{
		tableStaffs.get(currTable).editorBox.clearAll();
	}

	private void refreshCurrTable()
	{
		TableStaff staff = tableStaffs.get(currTable);
		ClientAgent.getInstance().send(new ReadPortionOrder(currTable, ClientAgent.getInstance().getLogin(),
				staff.currPortion, false, staff.editorBox.getFilter()));
	}

	public void switchActiveTable(Class<? extends AbstractDAO> tableClazz)
	{
		if (currTable != null)
		{
			TableStaff oldStaff = tableStaffs.get(currTable);
			setVisibleAndManaged(oldStaff.editorBox, false);
			oldStaff.tableView.getItems().clear();
		}

		currTable = tableClazz;
		TableStaff staff = tableStaffs.get(currTable);
		refreshCurrTable();

		setVisibleAndManaged(staff.editorBox, true);
		setVisibleAndManaged(filterIndicatorLabel, staff.isFiltered);
		currTableLabel.setText(staff.niceName);
		portionField.setText(String.valueOf(staff.currPortion));
		borderPane.setCenter(staff.tableView);
	}

	public void fsMode(ActionEvent actionEvent)
	{
		((Stage)root.getScene().getWindow()).setFullScreen(true);
	}

	public void gotoFirstPage(ActionEvent actionEvent)
	{
		gotoPage(0);
	}

	public void gotoPrevPage(ActionEvent actionEvent)
	{
		gotoPage(tableStaffs.get(currTable).currPortion - 1);
	}

	public void onPortionFieldKeyReleased(KeyEvent keyEvent)
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
				tableStaffs.get(currTable).editorBox.getFilter());
		ClientAgent.getInstance().send(order);
	}

	private void refreshSpecificPageLabel()
	{
		int portion = portionField.getText().isEmpty() ? tableStaffs.get(currTable).currPortion : Integer.parseInt(portionField.getText());
		if (portion < 1)
			portion = 1;
		specificPageLabel.setText("(" + ((portion - 1) * AbstractCRUD.PORTION_SIZE + 1) + " - " + (portion * AbstractCRUD.PORTION_SIZE) + ")");
	}

	private void switchUpdateMode(boolean value)
	{
		menuBar.setDisable(value);
		createBtn.setDisable(value);
		filterBtn.setDisable(value);
		deleteBtn.setDisable(value);
		setVisibleAndManaged(updateBtn, !value);
		setVisibleAndManaged(updateConfirmPane, value);
		tableStaffs.get(currTable).editorBox.setSecondaryVisible(value);
	}

	private void setVisibleAndManaged(Node node, boolean value)
	{
		node.setVisible(value);
		node.setManaged(value);
	}

	public void showAuthor(ActionEvent actionEvent)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Об авторе");
		alert.setHeaderText("Автор: Комаричев Р. Е.");
		alert.setContentText("Такие дела");
		alert.initOwner(root.getScene().getWindow());
		alert.showAndWait();
	}

	public void shutdown()
	{
		System.out.println("Shutting down");
		notificationsScheduler.shutdown();
		if (ClientAgent.doesInstanceExist())
		{
			ClientAgent.getInstance().stopPinging();
			ClientAgent.getInstance().send(BYE + SEPARATOR + ClientAgent.getInstance().getLogin());
			ClientAgent.getInstance().close();
		}
		ClientAgent.unsubscribeExceptionReceived(onExceptionReceived);
		ClientAgent.unsubscribePortionReceived(onPortionReceived);
		ClientAgent.unsubscribeNotificationReceived(onNotificationReceived);
		ClientAgent.unsubscribeConnectionLost(onConnectionLost);
		ClientAgent.unsubscribeQueryResultReceived(onQueryResultReceived);
		ClientAgent.clearAllReceivedSubs();
	}

	public void disconnect(ActionEvent actionEvent)
	{
		try
		{
			Parent authSceneFXML = FXMLLoader.load(ClientMain.class.getResource("/AuthScene/interface.fxml"));
			Stage authStage = new Stage();
			authStage.setTitle("Авторизация");
			Scene authScene = new Scene(authSceneFXML);
			authScene.getStylesheets().add("/AuthScene/style.css");
			authStage.setScene(authScene);
			((Stage) root.getScene().getWindow()).close();
			root.getScene().getWindow().getOnCloseRequest().handle(null);
			authStage.show();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void exit(ActionEvent actionEvent)
	{
		((Stage)root.getScene().getWindow()).close();
		root.getScene().getWindow().getOnCloseRequest().handle(null);
	}

	public void executeQuery(ActionEvent actionEvent)
	{
		if (!querySent)
		{
			querySent = true;
			ClientAgent.subscribeQueryResultReceived(onQueryResultReceived);

			String queryCode = ((MenuItem) actionEvent.getSource()).getUserData().toString();
			switch (queryCode)
			{
				case "_1_1":
					/*boolean valid = false;
					TextInputDialog dialog = new TextInputDialog();
					dialog.setTitle("Укажите значение");
					dialog.setHeaderText("Укажите значения для запроса");
					dialog.setContentText(":");

					while (!valid)
					{
						try
						{
							Optional<String> result = dialog.showAndWait();
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}*/

					ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance().getLogin() +
							SEPARATOR + queryCode +SEPARATOR + "49");
					break;

			}
		}
		else
			System.out.println("Nope"); //TODO
	}
}
