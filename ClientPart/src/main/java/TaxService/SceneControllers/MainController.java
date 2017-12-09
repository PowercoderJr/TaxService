package TaxService.SceneControllers;

import TaxService.Callback;
import TaxService.ClientMain;
import TaxService.Netty.ClientAgent;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;

public class MainController
{
	public GridPane tableGrid;
	public TableView actuallyTable;

	private Callback onSelect;

	public void initialize()
	{
		Label ph = new Label("НЕТ ЗАПИСЕЙ");
		actuallyTable.setPlaceholder(ph);

		onSelect = new Callback()
		{
			@Override
			public void callback(Object o)
			{

			}
		};
		ClientAgent.subscribeSelect(onSelect);
	}

	public void exit(ActionEvent actionEvent)
	{
		try
		{
			ClientMain.sceneManager.popScene();
		}
		catch (InvocationTargetException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
