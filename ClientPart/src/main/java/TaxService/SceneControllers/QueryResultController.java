package TaxService.SceneControllers;

import TaxService.Netty.ClientAgent;
import TaxService.TableColumnsBuilder;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class QueryResultController
{
	public BorderPane root;
	public TableView tableView;
	public Label headerLabel;
	public Label savingStatusLabel;

	private List<ArrayList> rows;
	private List<String> columnHeaders;
	private String queryHeader;
	private Date date;

	public void initialize()
	{
		
	}

	public void setData(List<ArrayList> rows, String queryHeader, Date date)
	{
		columnHeaders = new ArrayList<>(rows.get(0));
		this.rows = new ArrayList<>(rows);
		this.rows.remove(0);
		this.queryHeader = queryHeader;
		this.date = date;

		Platform.runLater(() ->
		{
			headerLabel.setText(ClientAgent.df.format(date) + " - " + queryHeader);
			tableView.getColumns().setAll(TableColumnsBuilder.buildForListOfStrings(columnHeaders));
			tableView.getItems().setAll(this.rows);
		});
	}

	public void onExportClicked(ActionEvent actionEvent)
	{
		savingStatusLabel.setText("");
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Сохранить результат запроса");
		fileChooser.getExtensionFilters()
				.addAll(new FileChooser.ExtensionFilter("Книга Excel", "*.xls"),
						new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showSaveDialog(root.getScene().getWindow());

		if (selectedFile != null)
			savingStatusLabel.setText("Сохранение...");
			new Thread(() ->
			{
				try
				{
					export(selectedFile);
					Platform.runLater(() -> savingStatusLabel.setText("Сохранено"));
				}
				catch (IOException e)
				{
					Platform.runLater(() ->
					{
						savingStatusLabel.setText("Ошибка записи");
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Ошибка");
						alert.setHeaderText("Не удалось сохранить");
						alert.setContentText("При записи файла произошла ошибка");
						alert.initOwner(root.getScene().getWindow());
						alert.showAndWait();
					});
				}
			}).start();
	}

	//https://tproger.ru/translations/how-to-read-write-excel-file-java-poi-example/
	public void export(File file) throws IOException
	{
		Workbook book = new HSSFWorkbook();
		Sheet sheet = book.createSheet("Результат запроса");

		Row firstRow = sheet.createRow(0);
		for (int j = 0; j < columnHeaders.size(); ++j)
		{
			Cell cell = firstRow.createCell(j);
			cell.setCellValue(columnHeaders.get(j).toString());
		}
		firstRow.createCell(columnHeaders.size() + 1).setCellValue(ClientAgent.df.format(date) + " - " + queryHeader);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, columnHeaders.size() + 1, columnHeaders.size() + 11));

		for (int i = 0; i < rows.size(); ++i)
		{
			Row row = sheet.createRow(i + 1);
			for (int j = 0; j < columnHeaders.size(); ++j)
				row.createCell(j).setCellValue(rows.get(i).get(j).toString());
		}

		for (int i = 0; i < columnHeaders.size(); ++i)
			sheet.autoSizeColumn(i);
		book.write(new FileOutputStream(file));
		book.close();
	}
}
