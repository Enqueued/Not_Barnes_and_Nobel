package Controller_Pack;

import java.sql.SQLException;
import java.util.List;

import Model_Pack.Library;
import Model_Pack.auditTrailEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import Model_Pack.Author;
import Model_Pack.Book;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class AuditTrailController {
	private static Logger logger = LogManager.getLogger();
	@FXML private Label authorName;
	@FXML private ListView<auditTrailEntry> auditView;
	private Author author = null;
	private Book book = null;
	private Library library = null;
	private List<auditTrailEntry> trails;

	public AuditTrailController(Author author, List<auditTrailEntry> trails){
		this.author = author;
		this.trails = trails;
	}
	
	
	public AuditTrailController(Book book, List<auditTrailEntry> trails2) {
		this.trails = trails2;
		this.book = book;
	}

	public AuditTrailController(Library library, List<auditTrailEntry> trails2) {
		this.trails = trails2;
		this.library = library;
	}


	public void initialize() throws SQLException{
		if(book == null && library == null){
			authorName.setText(author.toString());
		}else if(author == null && library == null){
			authorName.setText(book.toString());
		}else {
			authorName.setText(library.toString());
		}
			
		ObservableList<auditTrailEntry> items = auditView.getItems();
		for(auditTrailEntry c : trails) {
			items.add(c);
		}
	}
}
