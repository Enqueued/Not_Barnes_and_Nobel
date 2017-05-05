package Controller_Pack;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import Gate_Pack.AuthorTableGateway;
import Gate_Pack.BookTableGateway;
import Model_Pack.Author;
import Model_Pack.ViewType;
import Model_Pack.Book;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class BookController {
	private AuthorTableGateway adb;
	private static Logger logger = LogManager.getLogger();
	private List<Book> books;
	private BookTableGateway bookTableGateway;
	ObservableList<Book> items;
	@FXML private ListView<Book> listView;
	@FXML private Button filter;
	@FXML private TextField authField;
	@FXML private TextField titleField;
	@FXML private TextField dateField;

	public BookController(List<Book> books) throws SQLException {
		// TODO Auto-generated constructor stub
		this.books = books;
		this.bookTableGateway = new BookTableGateway();
	}
	
	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		if(action.getClickCount() == 2){
			Book book = listView.getSelectionModel().getSelectedItem();
			if(book == null){
				return;
			}
			Object source = action.getSource();
			if(source == listView){
				logger.info("clicked on " + book);
            	MasterController.getInstance().changeView(ViewType.BOOK_DETAIL, book);

			}
		}
	}

	@FXML private void onButtonPress(ActionEvent action) throws IOException, SQLException, ParseException{
	    String title = "", date = "", author = "";
		Object source = action.getSource();
		adb = new AuthorTableGateway();
		//todo filter is going to have things correctly filter based on the text input
		if(source == filter){
			if (!authField.getText().isEmpty()){
				logger.info(authField.getText());
				author = authField.getText();
			}
			if(!dateField.getText().isEmpty()){
				date = dateField.getText();
			}
			if(!titleField.getText().isEmpty()){
				title = titleField.getText();
			}

			books = bookTableGateway.filter(title, author, date);
			//if(!items.isEmpty()) {
			//    logger.info(items.toString());
			//	items.clear();
			//}
			items = listView.getItems();
			logger.info(listView.getItems());
			items.clear();
			logger.info(items.toString() );
			for(Book book: books){
				items.add(book);
			}
			books.clear();
        }

	}


	public void initialize() throws SQLException{
		ObservableList<Book> items = listView.getItems();
		for(Book c : books) {
			items.add(c);
		}
		books.clear();
		
	}
}
