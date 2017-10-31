package TaxService.SceneControllers;

import TaxService.Dictionary;
import TaxService.Netty.ClientAgent;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.codec.digest.DigestUtils;

public class AuthController
{
	public TextField loginField;
	public PasswordField passField;
	public Label statusLabel;

	public void initialize()
	{
	}

	public void signin(ActionEvent actionEvent)
	{
		String msg = Dictionary.SIGN_IN + Dictionary.SEPARATOR;
		msg += loginField.getText() + Dictionary.SEPARATOR;
		msg += DigestUtils.sha256Hex(DigestUtils.sha256Hex(passField.getText()));
		ClientAgent.getInstance().send(msg);
	}
}
