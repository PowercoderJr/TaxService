package TaxService.SceneControllers;

import TaxService.*;
import TaxService.Netty.ClientAgent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;

import static TaxService.PhraseBook.PORT;

public class AuthController
{
	public TextField loginField;
	public PasswordField passField;
	public Label statusLabel;

	private Callback onAuth;
	private Callback onExceptionReceived;

	public void initialize()
	{
		ClientMain.sceneManager.getStage().setTitle("Авторизация");
		ClientMain.sceneManager.getStage().setResizable(false);

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
					alert.setContentText(o.toString());
					alert.showAndWait();
				});
			}
		};
		ClientAgent.subscribeExceptionReceived(onExceptionReceived);

		onAuth = new Callback()
		{
			@Override
			public void callback(Object o)
			{
				final boolean accessed = (boolean) o;
				Platform.runLater(() ->
				{
					if (accessed)
					{
						try
						{
							ClientAgent.unsubscribeAuth(onAuth);
							ClientAgent.unsubscribeExceptionReceived(onExceptionReceived);
							Parent mainSceneFXML = FXMLLoader.load(ClientMain.class.getResource("/MainScene/interface.fxml"));
							ManagedScene mainScene = new ManagedScene(mainSceneFXML, ClientMain.DEFAULT_WINDOW_WIDTH, ClientMain.DEFAULT_WINDOW_HEIGHT, ClientMain.sceneManager);
							mainScene.getStylesheets().add("/MainScene/style.css");
							ClientMain.sceneManager.pushScene(mainScene);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						statusLabel.setText("Доступ запрещён");
						statusLabel.setVisible(true);
						ClientAgent.getInstance().close();
					}
				});
			}
		};
		ClientAgent.subscribeAuth(onAuth);
	}

	public void auth(ActionEvent actionEvent) throws IOException
	{
		//if (ClientAgent.getInstance() != null)
		{
			statusLabel.setVisible(false);
			String msg = PhraseBook.AUTH + PhraseBook.SEPARATOR +
					loginField.getText() + PhraseBook.SEPARATOR +
					passField.getText();
				//DigestUtils.sha256Hex(DigestUtils.sha256Hex(passField.getText()));
			try
			{
				ClientAgent.buildInstance(InetAddress.getLocalHost(), PORT);
				ClientAgent.getInstance().setLogin(loginField.getText());
				ClientAgent.getInstance().send(msg);
			}
			catch (InvocationTargetException e)
			{
				statusLabel.setText("Соединение не установлено");
				statusLabel.setVisible(true);
			}
		}
	}
}
