package Controller_Pack;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ResourceBundle;

import Model_Pack.Library;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Model_Pack.ViewType;
import Model_Pack.Author;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MenuController implements Initializable {
	private static Logger logger = LogManager.getLogger();
	@FXML private MenuItem AuthorListView;
	@FXML private MenuItem AddAuthor;
	@FXML private MenuItem Quit;
	@FXML private MenuItem BookView;
	@FXML private MenuItem newBookView;
	@FXML private MenuItem libView;
	@FXML private MenuItem addLib;

	/**
	 * blank constructor
	 */
	public MenuController(){

	}

	/**
	 * Initz dawg
	 * @param location
	 * @param resources
	 */
	public void initialize(URL location, ResourceBundle resources) {

	}

	@FXML private void handleMenuItem(ActionEvent action) throws IOException, SQLException, ParseException {
		Object source = action.getSource();
		if(source == Quit){
			Platform.exit();
		}
		if(source == AuthorListView){
			MasterController.getInstance().changeView(ViewType.AUTHOR_LIST, new Author());
			return;
		}
		if(source == AddAuthor){
			MasterController.getInstance().changeView(ViewType.AUTHOR_DETAIL, new Author());
			return;
		}
		if(source == BookView){
			MasterController.getInstance().changeView(ViewType.BOOK_VIEW, new Author());
			return;
		}
		if(source == newBookView){
			MasterController.getInstance().changeView(ViewType.BOOK_VIEW_TOO, new Author());
			return;
		}
		if(source == libView){
			MasterController.getInstance().changeView(ViewType.LIBRARY_VIEW, new Author());
			return;
		}
		if(source == addLib){
			MasterController.getInstance().changeView(ViewType.NEW_LIBRARY, new Author());
		}
	}
}

