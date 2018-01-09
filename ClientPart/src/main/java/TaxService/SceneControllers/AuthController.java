package TaxService.SceneControllers;

import TaxService.*;
import TaxService.DAOs.Account;
import TaxService.Netty.ClientAgent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.commons.codec.digest.DigestUtils;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static TaxService.PhraseBook.PORT;

public class AuthController
{
	@FXML
	public GridPane root;
	@FXML
	public TextField loginField;
	@FXML
	public PasswordField passField;
	@FXML
	public Label statusLabel;

	private Callback onAuth;
	private Callback onExceptionReceived;

	public void initialize()
	{
		onExceptionReceived = o -> Platform.runLater(() ->
		{
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Ошибка");
			alert.setHeaderText("При выполнении операции произошла ошибка");
			alert.setContentText(o.toString());
			alert.showAndWait();
		});
		ClientAgent.subscribeExceptionReceived(onExceptionReceived);

		onAuth = o ->
		{
			final Pair<String, String> accessResult = (Pair<String, String>) o;
			Platform.runLater(() ->
			{
				String errMsg = "";
				switch (accessResult.getKey())
				{
					case PhraseBook.ACCESS_RESULT_SUCCESS:
						try
						{
							//https://stackoverflow.com/questions/13246211/javafx-how-to-get-stage-from-controller-during-initialization
							FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("/MainScene/interface.fxml"));
							Parent mainSceneFXML = loader.load();
							Stage mainStage = new Stage();
							mainStage.setTitle("База данных налоговой инспекции");
							mainStage.setX((Toolkit.getDefaultToolkit().getScreenSize().width - ClientMain.DEFAULT_WINDOW_WIDTH) / 2);
							mainStage.setY((Toolkit.getDefaultToolkit().getScreenSize().height - ClientMain.DEFAULT_WINDOW_HEIGHT) / 2);
							mainStage.setOnCloseRequest(event -> ((MainController)loader.getController()).shutdown());
							((MainController)loader.getController()).setUI(Enum.valueOf(Account.Roles.class, accessResult.getValue()));
							Scene mainScene = new Scene(mainSceneFXML);
							mainScene.getStylesheets().add("/MainScene/style.css");
							mainStage.setScene(mainScene);
							((Stage)root.getScene().getWindow()).close();
							mainStage.show();
							ClientAgent.unsubscribeAuth(onAuth);
							ClientAgent.unsubscribeExceptionReceived(onExceptionReceived);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
						break;
					case PhraseBook.ACCESS_RESULT_FORBIDDEN:
						errMsg = "Доступ запрещён";
						break;
					case PhraseBook.ACCESS_RESULT_ALREADY_LOGGED:
						errMsg = "Пользователь уже авторизован с другого устройства";
						break;
					default:
						errMsg = "Доступ запрещён";
						break;
				}
				if (!errMsg.isEmpty())
				{
					statusLabel.setText(errMsg);
					statusLabel.setVisible(true);
					ClientAgent.getInstance().close();
				}
			});
		};
		ClientAgent.subscribeAuth(onAuth);
	}

	public void auth(ActionEvent actionEvent)
	{
		if (!ClientAgent.doesInstanceExist())
		{
			statusLabel.setVisible(false);
			//Imagine it is encrypted!
			String msg = PhraseBook.AUTH + PhraseBook.SEPARATOR +
					loginField.getText() + PhraseBook.SEPARATOR +
					passField.getText();
			try
			{
				ClientAgent.buildInstance(InetAddress.getLocalHost(), PORT);
				ClientAgent.getInstance().send(msg);
			}
			catch (InvocationTargetException e)
			{
				statusLabel.setText("Соединение не установлено");
				statusLabel.setVisible(true);
			}
			catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void onKeyPressed(KeyEvent keyEvent)
	{
		if (keyEvent.getCode() == KeyCode.ENTER)
			auth(null);
	}
}
