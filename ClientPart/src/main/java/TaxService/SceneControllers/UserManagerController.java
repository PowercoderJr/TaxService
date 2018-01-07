package TaxService.SceneControllers;

import TaxService.Callback;
import TaxService.DAOs.AbstractDAO;
import TaxService.DAOs.Account;
import TaxService.Deliveries.AllDelivery;
import TaxService.Netty.ClientAgent;
import TaxService.Orders.ReadAllOrder;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class UserManagerController
{
	@FXML
	public TableView tableView;
	@FXML
	public Button createBtn;
	@FXML
	public Button editBtn;
	@FXML
	public Button toggleBlockedBtn;

	private Callback onAllReceived;

	public void initialize()
	{
		TableColumn<Account, String> anotherColumn;
		List<TableColumn<Account, String>> list = new ArrayList<>();

		anotherColumn = new TableColumn<>("ID");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getId())));
		anotherColumn.setPrefWidth(50);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Логин");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getLogin())));
		anotherColumn.setPrefWidth(200);
		list.add(anotherColumn);
		anotherColumn = new TableColumn<>("Владелец");
		anotherColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getEmployee())));
		anotherColumn.setPrefWidth(300);
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

		tableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener()
		{
			@Override
			public void changed(ObservableValue observable, Object oldValue, Object newValue)
			{
				boolean empty = newValue == null;
				editBtn.setDisable(empty);
				toggleBlockedBtn.setDisable(empty);
			}
		});

		onAllReceived = o ->
		{
			AllDelivery<AbstractDAO> delivery = (AllDelivery<AbstractDAO>) o;
			Platform.runLater(() ->
			{
				if (delivery.getContentClazz().equals(Account.class))
					tableView.setItems(FXCollections.observableList(delivery.getContent()));
			});
		};
		ClientAgent.subscribeAllReceived(onAllReceived);
		ClientAgent.getInstance().send(new ReadAllOrder<Account>(Account.class, false, null));
	}

	public void onClose()
	{
		ClientAgent.unsubscribeAllReceived(onAllReceived);
	}
}
