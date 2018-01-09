package TaxService.SceneControllers;

import TaxService.CRUDs.AccountCRUD;
import TaxService.Callback;
import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.Account;
import TaxService.DAOs.Employee;
import TaxService.Deliveries.AllDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.CreateOrder;
import TaxService.Orders.ReadAllOrder;
import TaxService.Orders.UpdateOrder;
import TaxService.PhraseBook;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import javafx.scene.layout.BorderPane;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class UserManagerController
{
	@FXML
	public BorderPane root;
	@FXML
	public Label statsLabel;
	@FXML
	public TableView tableView;
	@FXML
	public Button createBtn;
	@FXML
	public TextField loginCrField;
	@FXML
	public PasswordField pass1CrField;
	@FXML
	public PasswordField pass2CrField;
	@FXML
	public ComboBox<Employee> ownerCrCb;
	@FXML
	public ComboBox<Account.Roles> roleCrCb;
	@FXML
	public CheckBox blockedCrChb;
	@FXML
	public Button updateBtn;
	@FXML
	public TitledPane updatePane;
	@FXML
	public ComboBox<Account.Roles> roleUpdCb;
	@FXML
	public CheckBox blockedUpdChb;

	private static Effect invalidEffect = new ColorAdjust(0, 0.5, 0, 0);

	private Callback onAllReceived;
	private ObservableList employees;
	private FilteredList accounts;
	private boolean showBlocked;

	public void initialize()
	{
		showBlocked = true;

		//TableView
		TableColumn<Account, String> anotherColumn;
		List<TableColumn<Account, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		anotherColumn.setPrefWidth(50);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Логин");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getLogin())));
		anotherColumn.setPrefWidth(150);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Владелец");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEmployee())));
		anotherColumn.setPrefWidth(220);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Роль");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getRole())));
		anotherColumn.setPrefWidth(100);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Заблокирован");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isBlocked() ? "Да" : "Нет"));
		anotherColumn.setPrefWidth(140);
		list.add(anotherColumn);
		tableView.getColumns().setAll(list);

		accounts = new FilteredList(FXCollections.observableArrayList());
		tableView.setItems(accounts);

		//Формы создания и редактирования
		UnaryOperator<TextFormatter.Change> loginFilter = change ->
		{
			String newText = change.getControlNewText();
			if (newText.matches("^[a-zA-Z]+\\w*$|^$"))
				return change;
			else
				return null;
		};
		loginCrField.setTextFormatter(new TextFormatter<Integer>(loginFilter));
		employees = FXCollections.observableArrayList(new ArrayList<Employee>());
		ownerCrCb.setItems(employees);
		ownerCrCb.setOnShowing(event ->
		{
			ownerCrCb.getSelectionModel().clearSelection();
			ownerCrCb.getEditor().clear();
			ClientAgent.getInstance().send(new ReadAllOrder<Employee>(Employee.class, true, null));
		});
		roleCrCb.getItems().setAll(Account.Roles.values());

		setEffectsResetter(loginCrField);
		setEffectsResetter(pass1CrField);
		setEffectsResetter(pass2CrField);
		setEffectsResetter(ownerCrCb);
		setEffectsResetter(roleCrCb);

		roleUpdCb.getItems().setAll(Account.Roles.values());

		tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				boolean empty = newValue == null;
				updateBtn.setDisable(empty);

				if (empty)
				{
					updatePane.setText("Редактирование пользователя");
					blockedUpdChb.setSelected(false);
				}
				else
				{
					Account account = (Account) tableView.getSelectionModel().getSelectedItem();
					updatePane.setText("Редактирование: " + account);
					roleUpdCb.getSelectionModel().select(account.getRole());
					blockedUpdChb.setSelected(account.isBlocked());
				}
			}
		});

		//
		onAllReceived = o ->
		{
			AllDelivery<AbstractDAO> delivery = (AllDelivery<AbstractDAO>) o;
			Platform.runLater(() ->
			{
				if (delivery.getContentClazz().equals(Account.class))
				{
					Platform.runLater(() ->
					{
						accounts.getSource().setAll(delivery.getContent());
						updateAccountsFilter();
						tableView.refresh();
						refreshStatsLabel();
					});
				}
				else if (delivery.getContentClazz().equals(Employee.class))
					employees.setAll(delivery.getContent());
			});
		};
		ClientAgent.subscribeAllReceived(onAllReceived);
		ClientAgent.getInstance().send(new ReadAllOrder<Account>(Account.class, false, null));
	}

	public void onClose()
	{
		ClientAgent.unsubscribeAllReceived(onAllReceived);
	}

	public void onCreateClicked(ActionEvent actionEvent)
	{
		String trimmedLogin = loginCrField.getText().trim();
		if (validateTextField(loginCrField) & validateTextField(pass1CrField) & validateTextField(pass2CrField) &
				validateComboBox(ownerCrCb) & validateComboBox(roleCrCb))
		{
			if (pass1CrField.getText().equals(pass2CrField.getText()))
			{
				Account account = new Account(trimmedLogin, pass1CrField.getText(),
						ownerCrCb.getSelectionModel().getSelectedItem(), roleCrCb.getSelectionModel().getSelectedItem(),
						blockedCrChb.isSelected());
				ClientAgent.getInstance().send(new CreateOrder<>(Account.class, account));
				Platform.runLater(() ->
				{
					((FilteredList) tableView.getItems()).getSource().add(account);
					updateAccountsFilter();
					tableView.refresh();
					refreshStatsLabel();
					clearCreateForm();
				});
			}
			else
			{
				markAsInvalid(pass1CrField);
				markAsInvalid(pass2CrField);
			}
		}
	}

	public void onUpdateClicked(ActionEvent actionEvent)
	{
		Account account = (Account) tableView.getSelectionModel().getSelectedItem();
		ClientAgent.getInstance().send(new UpdateOrder<>(Account.class, "(login) = ('" + account.getLogin() + "')",
				"(role, blocked) = ('" + String.valueOf(roleUpdCb.getSelectionModel().getSelectedItem()) +
						"', " + String.valueOf(blockedUpdChb.isSelected()) + ")"));
		account.setRole(roleUpdCb.getSelectionModel().getSelectedItem());
		account.setBlocked(blockedUpdChb.isSelected());
		Platform.runLater(() ->
		{
			updateAccountsFilter();
			tableView.refresh();
			refreshStatsLabel();
			//clearUpdateForm();
		});
	}

	public void toggleShowBlocked(ActionEvent actionEvent)
	{
		showBlocked = !showBlocked;
		updateAccountsFilter();
		tableView.refresh();
	}

	public void refresh(ActionEvent actionEvent)
	{
		ClientAgent.getInstance().send(new ReadAllOrder<Account>(Account.class, false, null));
	}

	private void clearCreateForm()
	{
		loginCrField.clear();
		pass1CrField.clear();
		pass2CrField.clear();
		ownerCrCb.getSelectionModel().clearSelection();
		ownerCrCb.getEditor().clear();
		roleCrCb.getSelectionModel().clearSelection();
		roleCrCb.getEditor().clear();
		blockedCrChb.setSelected(false);
	}

	private void clearUpdateForm()
	{
		roleUpdCb.getSelectionModel().clearSelection();
		roleUpdCb.getEditor().clear();
		blockedUpdChb.setSelected(false);
	}

	private void updateAccountsFilter()
	{
		accounts.setPredicate(acc -> showBlocked ? true : !((Account)acc).isBlocked());
	}

	private void refreshStatsLabel()
	{
		int totalUsers = accounts.size(), blockedCount = 0;
		for (int i = 0; i < totalUsers; ++i)
		{
			if (((Account) accounts.get(i)).isBlocked())
				++blockedCount;
		}
		statsLabel.setText("Всего пользователей: " + accounts.size() +
				"\nАктивных: " + (totalUsers - blockedCount) +
				"\nЗаблокированных: " + blockedCount);
	}

	private static void markAsInvalid(Node field)
	{
		field.setEffect(invalidEffect);
	}

	private static void setEffectsResetter(Node node)
	{
		node.focusedProperty().addListener(new ChangeListener<Boolean>()
		{
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
			{
				if (newPropertyValue)
					node.setEffect(null);
			}
		});
	}

	private static boolean validateTextField(TextField field)
	{
		boolean isValid = !field.getText().trim().isEmpty();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}

	private static boolean validateComboBox(ComboBox field)
	{
		boolean isValid = !field.getSelectionModel().isEmpty();
		if (!isValid)
			markAsInvalid(field);
		return isValid;
	}
}
