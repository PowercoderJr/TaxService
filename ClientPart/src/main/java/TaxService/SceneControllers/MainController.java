package TaxService.SceneControllers;

import TaxService.CRUDs.AbstractCRUD;
import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.CustomUI.EditorBoxes.*;
import TaxService.CustomUI.MaskField;
import TaxService.DAOs.*;
import TaxService.Deliveries.AllDelivery;
import TaxService.Deliveries.PortionDelivery;
import TaxService.Deliveries.QueryResultDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadAllOrder;
import TaxService.Orders.ReadPortionOrder;
import TaxService.DAOs.AbstractDAO;
import TaxService.TableColumnsBuilder;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.UnaryOperator;

import static TaxService.PhraseBook.*;


public class MainController
{
	private static Map<Class<? extends AbstractDAO>, TableStaff> tableStaffs = new HashMap<>();

	private class TableStaff<T extends AbstractDAO>
	{
		private final Class<T> clazz;
		private final String niceName;
		private final TableView<T> tableView;
		private final AbstractEditorBox<T> editorBox;
		private final ObservableList<T> data;
		private int currPortion;
		private boolean isFiltered;

		TableStaff(Class<T> clazz, String niceName, TableView<T> tableView, AbstractEditorBox<T> editorBox, ObservableList<T> data)
		{
			this.clazz = clazz;
			this.niceName = niceName;
			this.tableView = tableView;
			this.editorBox = editorBox;
			this.data = data;
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
	private Callback onAllReceived;
	private Callback onExceptionReceived;
	private Callback onNotificationReceived;
	private Callback onConnectionLost;
	private Callback onQueryResultReceived;
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

		onAllReceived = o ->
		{
			AllDelivery<AbstractDAO> delivery = (AllDelivery<AbstractDAO>) o;
			Platform.runLater(() -> tableStaffs.get(delivery.getContentClazz()).data.setAll(delivery.getContent()));
		};
		ClientAgent.subscribeAllReceived(onAllReceived);

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

		onQueryResultReceived = o ->
		{
			QueryResultDelivery delivery = (QueryResultDelivery) o;
			Platform.runLater(() ->
			{
				HBox hbox = new HBox(new Label(ClientAgent.df.format(delivery.getDate()) + " - " + delivery.getHeader()));
				hbox.setPrefHeight(30);
				TableView tv = new TableView();
				tv.setPlaceholder(new Label("НЕТ ЗАПИСЕЙ"));
				List<ArrayList> rows = delivery.getContent();
				tv.getColumns().setAll(TableColumnsBuilder.buildForListOfStrings(rows.get(0)));
				rows.remove(0);
				tv.getItems().setAll(rows);

				try
				{
					FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/QueryResultScene/interface.fxml"));
					BorderPane root = loader.load();
					root.setTop(hbox);
					root.setCenter(tv);
					Stage stage = new Stage();
					stage.setTitle("Результат запроса");
					Scene scene = new Scene(root);
					scene.getStylesheets().add("/QueryResultScene/style.css");
					stage.setScene(scene);
					stage.initOwner(this.root.getScene().getWindow());
					stage.initModality(Modality.APPLICATION_MODAL);
					stage.showAndWait();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			});
		};
		ClientAgent.subscribeQueryResultReceived(onQueryResultReceived);

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

		HashMap<Class<? extends AbstractDAO>, ObservableList> dataSources = new HashMap<>();
		for (TableStaff staff : tableStaffs.values())
			dataSources.put(staff.clazz, staff.data);
		for (TableStaff staff : tableStaffs.values())
			staff.editorBox.bindDataSources(dataSources);

		switchActiveTable(Department.class);
		ClientAgent.getInstance().startPinging();
	}

	private <T extends AbstractDAO> void initTableStaff(Class<T> tableClazz, String niceName)
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

			List list = new ArrayList();
			ObservableList observableList = FXCollections.observableList(list);
			tableStaffs.put(tableClazz, new TableStaff(tableClazz, niceName, tv, eb, observableList));
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

	public <T extends AbstractDAO> void switchActiveTable(Class<T> tableClazz)
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
		ClientAgent.unsubscribeAllReceived(onAllReceived);
		ClientAgent.unsubscribeNotificationReceived(onNotificationReceived);
		ClientAgent.unsubscribeConnectionLost(onConnectionLost);
		ClientAgent.unsubscribeQueryResultReceived(onQueryResultReceived);
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
		ButtonType okBtn = new ButtonType("ОК", ButtonBar.ButtonData.OK_DONE);
		ButtonType cancelBtn = new ButtonType("Отмена", ButtonBar.ButtonData.CANCEL_CLOSE);
		String queryCode = ((MenuItem) actionEvent.getSource()).getUserData().toString();
		switch (queryCode)
		{
			case "_1_1":
			{
				ClientAgent.getInstance().send(new ReadAllOrder<Employee>(Employee.class, ClientAgent.getInstance()
						.getLogin(), true, null));
				Dialog<Employee> dialog = new Dialog<>();
				dialog.setTitle("Укажите значение");
				dialog.setHeaderText("Укажите значения для запроса");

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				Label label = new Label("Сотрудник:");
				label.setPrefWidth(150);
				grid.add(label, 0, 0);
				ComboBox<Employee> comboBox = new ComboBox<>();
				comboBox.setPrefWidth(300);
				comboBox.setItems(tableStaffs.get(Employee.class).data);
				grid.add(comboBox, 1, 0);

				dialog.getDialogPane().setContent(grid);
				Platform.runLater(() -> comboBox.requestFocus());
				dialog.getDialogPane().getButtonTypes().setAll(okBtn, cancelBtn);
				dialog.setResultConverter(btn -> btn == okBtn ? comboBox.getSelectionModel().getSelectedItem() : null);

				dialog.initOwner(root.getScene().getWindow());
				Optional<Employee> result = dialog.showAndWait();
				if (result.isPresent())
					ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance()
							.getLogin() + SEPARATOR + queryCode + SEPARATOR + result.get().id);
				break;
			}
			case "_1_2":
			{
				Dialog<Date> dialog = new Dialog<>();
				dialog.setTitle("Укажите значение");
				dialog.setHeaderText("Укажите значения для запроса");

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				Label label = new Label("Дата:");
				label.setPrefWidth(50);
				grid.add(label, 0, 0);
				DatePicker datePicker = new DatePicker(LocalDate.now());
				datePicker.setPrefWidth(150);
				grid.add(datePicker, 1, 0);

				dialog.getDialogPane().setContent(grid);
				Platform.runLater(() -> datePicker.requestFocus());
				dialog.getDialogPane().getButtonTypes().setAll(okBtn, cancelBtn);
				dialog.setResultConverter(btn -> btn == okBtn ? Date.valueOf(datePicker.getValue()) : null);

				dialog.initOwner(root.getScene().getWindow());
				Optional<Date> result = dialog.showAndWait();
				if (result.isPresent())
					ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance()
							.getLogin() + SEPARATOR + queryCode + SEPARATOR + result.get());
				break;
			}
			case "_1_3":
			{
				ClientAgent.getInstance().send(new ReadAllOrder<Employee>(Employee.class, ClientAgent.getInstance()
						.getLogin(), true, null));
				Dialog<Pair<Employee, Date>> dialog = new Dialog<>();
				dialog.setTitle("Укажите значение");
				dialog.setHeaderText("Укажите значения для запроса");

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				Label label1 = new Label("Сотрудник:");
				label1.setPrefWidth(150);
				grid.add(label1, 0, 0);
				ComboBox<Employee> comboBox = new ComboBox<>();
				comboBox.setPrefWidth(300);
				comboBox.setItems(tableStaffs.get(Employee.class).data);
				grid.add(comboBox, 1, 0);

				Label label2 = new Label("Дата:");
				label2.setPrefWidth(50);
				grid.add(label2, 0, 1);
				DatePicker datePicker = new DatePicker(LocalDate.now());
				datePicker.setPrefWidth(150);
				grid.add(datePicker, 1, 1);

				dialog.getDialogPane().setContent(grid);
				Platform.runLater(() -> comboBox.requestFocus());
				dialog.getDialogPane().getButtonTypes().setAll(okBtn, cancelBtn);
				dialog.setResultConverter(btn ->
				{
					Employee a = comboBox.getSelectionModel().getSelectedItem();
					Date b = datePicker.getValue() == null ? null : Date.valueOf(datePicker.getValue());
					return btn == okBtn && a != null && b != null ? new Pair<>(a, b) : null;
				});

				dialog.initOwner(root.getScene().getWindow());
				Optional<Pair<Employee, Date>> result = dialog.showAndWait();
				if (result.isPresent())
					ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance().getLogin() +
							SEPARATOR + queryCode + SEPARATOR + result.get().getKey().getId() + SEPARATOR + result.get().getValue());
				break;
			}
			case "_8_1":
			{
				ClientAgent.getInstance().send(new ReadAllOrder<Post>(Post.class, ClientAgent.getInstance()
						.getLogin(), true, null));
				Dialog<Post> dialog = new Dialog<>();
				dialog.setTitle("Укажите значение");
				dialog.setHeaderText("Укажите значения для запроса");

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				Label label = new Label("Должность:");
				label.setPrefWidth(150);
				grid.add(label, 0, 0);
				ComboBox<Post> comboBox = new ComboBox<>();
				comboBox.setPrefWidth(300);
				comboBox.setItems(tableStaffs.get(Post.class).data);
				grid.add(comboBox, 1, 0);

				dialog.getDialogPane().setContent(grid);
				Platform.runLater(() -> comboBox.requestFocus());
				dialog.getDialogPane().getButtonTypes().setAll(okBtn, cancelBtn);
				dialog.setResultConverter(btn -> btn == okBtn ? comboBox.getSelectionModel().getSelectedItem() : null);

				dialog.initOwner(root.getScene().getWindow());
				Optional<Post> result = dialog.showAndWait();
				if (result.isPresent())
					ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance()
							.getLogin() + SEPARATOR + queryCode + SEPARATOR + result.get().id);
				break;
			}
			case "_8_2":
			{
				Dialog<String> dialog = new Dialog<>();
				dialog.setTitle("Укажите значение");
				dialog.setHeaderText("Укажите значения для запроса");

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				Label label = new Label("Код оператра:");
				label.setPrefWidth(150);
				grid.add(label, 0, 0);
				MaskField maskField = new MaskField();
				maskField.setPrefWidth(150);
				maskField.setMask("0DD");
				maskField.setWhatMask("-##");
				maskField.setPlaceholder("0__");
				grid.add(maskField, 1, 0);

				dialog.getDialogPane().setContent(grid);
				Platform.runLater(() -> maskField.requestFocus());
				dialog.getDialogPane().getButtonTypes().setAll(okBtn, cancelBtn);
				dialog.setResultConverter(btn -> btn == okBtn ? maskField.getText() : null);

				dialog.initOwner(root.getScene().getWindow());
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent())
					ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance()
							.getLogin() + SEPARATOR + queryCode + SEPARATOR + result.get());
				break;
			}
			case "_9":
			{
				Dialog<Float> dialog = new Dialog<>();
				dialog.setTitle("Укажите значение");
				dialog.setHeaderText("Укажите значения для запроса");

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				Label label = new Label("Граница суммы оплат:");
				label.setPrefWidth(150);
				grid.add(label, 0, 0);
				TextField textField = new TextField();
				textField.setPrefWidth(250);
				grid.add(textField, 1, 0);

				dialog.getDialogPane().setContent(grid);
				Platform.runLater(() -> textField.requestFocus());
				dialog.getDialogPane().getButtonTypes().setAll(okBtn, cancelBtn);
				dialog.setResultConverter(btn ->
				{
					Float result = null;
					try
					{
						if (btn == okBtn)
							result = Float.parseFloat(textField.getText().replace(',', '.'));
					}
					catch (Exception e)
					{
						result = Float.NaN;
					}
					return result;
				});

				dialog.initOwner(root.getScene().getWindow());
				Optional<Float> result = dialog.showAndWait();
				if (result.isPresent())
					if (result.get().equals(Float.NaN))
					{
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Ошибка");
						alert.setHeaderText("Некорректный ввод");
						alert.setContentText("Ожидается действительное число");
						alert.initOwner(dialog.getOwner());
						alert.showAndWait();
					}
					else
						ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance()
								.getLogin() + SEPARATOR + queryCode + SEPARATOR + result.get());
				break;
			}
			case "_10":
			{
				ClientAgent.getInstance().send(new ReadAllOrder<Owntype>(Owntype.class, ClientAgent.getInstance()
						.getLogin(), true, null));
				Dialog<Pair<Owntype, Float>> dialog = new Dialog<>();
				dialog.setTitle("Укажите значение");
				dialog.setHeaderText("Укажите значения для запроса");

				GridPane grid = new GridPane();
				grid.setHgap(10);
				grid.setVgap(10);

				Label label1 = new Label("Тип собственности:");
				label1.setPrefWidth(250);
				grid.add(label1, 0, 0);
				ComboBox<Owntype> comboBox = new ComboBox<>();
				comboBox.setPrefWidth(300);
				comboBox.setItems(tableStaffs.get(Owntype.class).data);
				grid.add(comboBox, 1, 0);

				Label label2 = new Label("Граница суммы оплат:");
				label2.setPrefWidth(250);
				grid.add(label2, 0, 1);
				TextField textField = new TextField();
				textField.setPrefWidth(150);
				grid.add(textField, 1, 1);

				dialog.getDialogPane().setContent(grid);
				Platform.runLater(() -> comboBox.requestFocus());
				dialog.getDialogPane().getButtonTypes().setAll(okBtn, cancelBtn);
				dialog.setResultConverter(btn ->
				{
					Owntype a = comboBox.getSelectionModel().getSelectedItem();
					Float b = null;
					try
					{
						if (btn == okBtn)
							b = Float.parseFloat(textField.getText().replace(',', '.'));
					}
					catch (Exception e)
					{
						b = Float.NaN;
					}
					return btn == okBtn && a != null && b != null ? new Pair<>(a, b) : null;
				});

				dialog.initOwner(root.getScene().getWindow());
				Optional<Pair<Owntype, Float>> result = dialog.showAndWait();
				if (result.isPresent())
					if (result.get().getValue().equals(Float.NaN))
					{
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Ошибка");
						alert.setHeaderText("Некорректный ввод");
						alert.setContentText("Ожидается действительное число");
						alert.initOwner(dialog.getOwner());
						alert.showAndWait();
					}
					else
						ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance()
								.getLogin() + SEPARATOR + queryCode + SEPARATOR + result.get().getKey().getId()
								+ SEPARATOR + result.get().getValue());
				break;
			}
			case "_2_1":
			case "_3":
			case "_6":
			case "_7":
			{
				ClientAgent.getInstance().send(QUERY + SEPARATOR + ClientAgent.getInstance().getLogin() +
						SEPARATOR + queryCode);
				break;
			}
		}
	}
}
