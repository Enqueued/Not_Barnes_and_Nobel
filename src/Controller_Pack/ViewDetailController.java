package Controller_Pack;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Model_Pack.ViewType;
import Model_Pack.Author;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

public class ViewDetailController {
	private static Logger logger = LogManager.getLogger();
	private List<Author> authors;
	@FXML private ListView<Author> listView;

	/**
	 * used to actuate the buttons
	 * @param action
	 * @throws IOException
	 * @throws SQLException
	 * @throws ParseException
	 */
	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		if(action.getClickCount() == 2){
			Author author = listView.getSelectionModel().getSelectedItem();
			Object source = action.getSource();
			if(author == null){
				return;
			}
			if(source == listView){
				logger.info("clicked on " + author);
				MasterController.getInstance().changeView(ViewType.AUTHOR_DETAIL, author);
			}
		}
	}

	/**
	 * initz dawg
	 * @throws SQLException
	 */
	public void initialize() throws SQLException{
		ObservableList<Author> items = listView.getItems();
		for(Author c : authors) {
			items.add(c);
		}
		authors.clear();
	}

	/**
	 * setting the author lists
	 * @param authors
	 */
	public ViewDetailController(List<Author> authors){
		this.authors = authors;
	}
}
