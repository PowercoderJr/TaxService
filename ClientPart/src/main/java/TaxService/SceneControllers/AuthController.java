package TaxService.SceneControllers;

import TaxService.Callback;
import TaxService.Dictionary;
import TaxService.Netty.ClientAgent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.codec.digest.DigestUtils;

import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static TaxService.Dictionary.PORT;

public class AuthController
{
	public TextField loginField;
	public PasswordField passField;
	public Label statusLabel;

	private Callback onAuth;

	public void initialize()
	{
		onAuth = new Callback()
		{
			@Override
			public void callback(Object o)
			{
				final boolean accessed = (boolean) o;
				statusLabel.setVisible(true);
				Platform.runLater(() -> statusLabel.setText(accessed ? "Доступ разрешён" : "Доступ запрещён"));
			}
		};
		ClientAgent.subscribeAuth(onAuth);
	}

	public void signin(ActionEvent actionEvent)
	{
		statusLabel.setVisible(false);
		String msg = Dictionary.SIGN_IN + Dictionary.SEPARATOR;
		msg += loginField.getText() + Dictionary.SEPARATOR;
		msg += DigestUtils.sha256Hex(DigestUtils.sha256Hex(passField.getText()));
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
