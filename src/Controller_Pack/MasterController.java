package Controller_Pack;

import Model_Pack.auditTrailEntry;
import Model_Pack.Author;
import Gate_Pack.AuthorTableGateway;
import Model_Pack.ViewType;
import Model_Pack.Book;
import Gate_Pack.BookTableGateway;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Master Controller_Pack
 * this should control the changing of the initial menu and stuff like that.
 *
 */
public class MasterController {
	private static Logger logger = LogManager.getLogger();
	private static MasterController instance = null;
	private BorderPane rootPane;
	private DetailController DC = new DetailController();
	private BookDetailController BDC = new BookDetailController();
	private AuthorTableGateway DB;
	private BookTableGateway BDB;
	private List<Author> authors;
	private Author author;
	private int check = 0;

	public int getCheck() {
		return check;
	}


	private int complicated = 0;

	/**
	 * Constructor blank
	 */
	private MasterController() {
		//create gateways
		try {
			DB = new AuthorTableGateway();
			BDB = new BookTableGateway();
		} catch (Exception e) {
			logger.error(e);
			Platform.exit();
		}
	}

	/**
	 * changeView
	 * This should handle changing the views to and fro
	 * @param vType
	 * @param data
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public boolean changeView(ViewType vType, Object data) throws SQLException, ParseException {
		DetailController Dc = MasterController.getInstance().getDC();
		BookDetailController BDC = MasterController.getInstance().getBDC();


		if(check == 1){
			complicated = DC.check((Author)data);
			if(complicated == 1){
				return false;
			}
		}
		if(check == 2){
			Book book = BDC.getOldBook();
			complicated = BDC.check(book);
			if(complicated == 1){
				return false;
			}
		}
		FXMLLoader loader = null;
		if(vType == ViewType.AUTHOR_LIST) {
			check = 0;
			loader = new FXMLLoader(getClass().getResource("/FXML_Pack/firstView.fxml"));
			loader.setController(new ViewDetailController(DB.getAuthors()));

		} else if(vType == ViewType.AUTHOR_DETAIL) {
			check =1;
			loader = new FXMLLoader(getClass().getResource("/FXML_Pack/lastView.fxml"));
			loader.setController(new DetailController((Author) data, DB));

		} else if(vType == ViewType.BOOK_AUDIT_TRAIL) {
			check = 0;
			Book book = (Book) data;
			logger.info(book.getId());
			List<auditTrailEntry> trails = BDB.auditTrail(book);
			loader = new FXMLLoader(getClass().getResource("/FXML_Pack/audittrailview.fxml"));
			loader.setController(new AuditTrailController(book, trails));
		} else if(vType == ViewType.BOOK_VIEW) {
			check = 0;
			List<Book> books = BDB.getBooks();
			loader = new FXMLLoader(getClass().getResource("/FXML_Pack/bookview.fxml"));
			loader.setController(new BookController(books));
		} else if(vType == ViewType.BOOK_DETAIL) {
			check = 2;
			loader = new FXMLLoader(getClass().getResource("/FXML_Pack/bookdetailview.fxml"));
			loader.setController(new BookDetailController((Book) data, DB.getAuthors(), new BookTableGateway()));
		} else if(vType == ViewType.BOOK_VIEW_TOO) {
			check = 2;
			loader = new FXMLLoader(getClass().getResource("/FXML_Pack/bookdetailview.fxml"));
			loader.setController(new BookDetailController( new Book(), DB.getAuthors(), new BookTableGateway()));
		}else if(vType == ViewType.AUDIT_TRAIL) {
			check = 0;
			Author author = (Author) data;
			logger.info(author.getId());
			List<auditTrailEntry> trails = DB.auditTrail(author);
			loader = new FXMLLoader(getClass().getResource("/FXML_Pack/audittrailview.fxml"));
			loader.setController(new AuditTrailController(author, trails));
		}

		Parent view = null;
		try {
			view = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//attach view to application center of border pane
		rootPane.setCenter(view);
		return true;
	}

	/**
	 * Closes the section
	 * @throws SQLException
	 */
	public void close() throws SQLException {
		DB.close();
	}

	/**
	 * getInstance
	 * Should get the current instance or if the instance is filled/used then it'll return that.
	 * @return
	 */
	public static MasterController getInstance() {
		if(instance == null)
			instance = new MasterController();
		return instance;
	}

	//Setters
	public void setRootPane(BorderPane rootPane) {
		this.rootPane = rootPane;
	}

	public void setAuthorGateway(AuthorTableGateway carGateway) {
		this.DB = carGateway;
	}

	public void setDC(DetailController detailController) {
		DC = detailController;
	}

	public void setBDC(BookDetailController bDC) {
		BDC = bDC;
	}

	//Getters
	public AuthorTableGateway getAuthorGateway() {
		return DB;
	}

	public BorderPane getRootPane() {
		return rootPane;
	}

	public DetailController getDC() {
		return DC;
	}

	public void setCheck(int check) {
		this.check = check;
	}

	public BookDetailController getBDC() {
		return BDC;
	}

}
