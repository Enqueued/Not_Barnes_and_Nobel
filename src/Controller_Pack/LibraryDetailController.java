package Controller_Pack;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import Gate_Pack.BookTableGateway;
import Gate_Pack.LibraryTableGateway;
import Main_Pack.Validation;
import Model_Pack.*;
import com.itextpdf.text.DocumentException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.MouseEvent;
/**
 * Created by ultimaq on 4/6/17.
 */
public class LibraryDetailController {
    private static Logger logger = LogManager.getLogger();
    ObservableList<LibraryBook> Items;
    private Library library;
    private Library oldLibrary;
    private LibraryTableGateway LTG;
    private BookTableGateway BTG;
    private List<Book> Book;
    private List<LibraryBook> libraryBooks;
    private Validation validation;
    @FXML private Button DelBook;
    @FXML private Button DelLib;
    @FXML private Button AddBook;
    @FXML private Button Save;
    @FXML private Button Audit;
    @FXML private Button Report;
    @FXML private TextField Quantity;
    @FXML private TextField LibName;
    @FXML private TextField LibID;
    @FXML private ComboBox<Book> Books;
    @FXML private ListView<LibraryBook> listView;

    public LibraryDetailController(Library data, LibraryTableGateway LTG) {
        this.library=data;
        this.LTG = LTG;
        this.libraryBooks = data.getBooks();
    }

    public LibraryDetailController() {
        this.Book = null;
        this.libraryBooks = new ArrayList<LibraryBook>();
        this.validation = new Validation();
    }
    public LibraryDetailController(Library constant, LibraryTableGateway LTG, List<LibraryBook> libraryBooks) {
        // TODO Auto-generated constructor stub
        this.library=constant;
        this.LTG = LTG;
        this.libraryBooks = libraryBooks;
    }

    private boolean QuantityCheck(String string) {
        int check = Integer.valueOf(string);
        if(check >=0 && check <=100){
            return true;
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Quantity Error");
            alert.setContentText("Quantity can not be more than 100 \n And can not be less than 0");
            alert.showAndWait();
            return false;
        }
    }

    private boolean isLibName(String text) {
        if(!text.isEmpty() && text.length() <= 100){
            return true;
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Name Error");
            alert.setContentText("Name can not be more than 100 characters.\n And can not be blank");
            alert.showAndWait();
            return false;
        }
    }

    public void initialize()throws SQLException, ParseException{
        BTG = new BookTableGateway();
        LibName.setText(library.getLibraryName());
        LibID.setText(String.valueOf(library.getId()));
        Books.getItems().addAll(BTG.getBooks());
        //todo make the quantity var update with the book that is added
        Quantity.setText("00");
        oldLibrary = new Library(library.getId(),library.getLibraryName(),library.getBooks(),library.getLastModified());
        MasterController.getInstance().setLDC(this);
   //     this.libraryBooks = LTG.getLibraryBooks(library.getId());
        Items = listView.getItems();
        if(libraryBooks != null){
            for(LibraryBook book: libraryBooks){
                Items.add(book);
            }
            libraryBooks.clear();
        }
    }

    public Library getOldLibrary() {
        return oldLibrary;
    }
    public void setOldLibrary(Library oldLibrary) {
        this.oldLibrary = oldLibrary;
    }

    public int check(Library data) throws ParseException {
        if (data.getId() == 0) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Save Data?");
            alert.setContentText("yes = save, no = don't save, cancle = stay");

            ButtonType buttonTypeOne = new ButtonType("yes");
            ButtonType buttonTypeTwo = new ButtonType("no");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == buttonTypeOne) {
                MasterController.getInstance().setCheck(0);
                Save.fire();
                return 1;

            } else if (result.get() == buttonTypeTwo) {
                return 0;
            } else {
                return 1;
            }
        }
        LibraryDetailController LDC = MasterController.getInstance().getLDC();
        if (library.equals(data) && library.getId() > 0) {
            return (0);
        } else {
            if (!LibName.getText().equals(library.getLibraryName())) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Save Data?");
                alert.setContentText("yes = save, no = don't save, cancle = stay");

                ButtonType buttonTypeOne = new ButtonType("yes");
                ButtonType buttonTypeTwo = new ButtonType("no");
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == buttonTypeOne) {
                    MasterController.getInstance().setCheck(0);
                    Save.fire();
                    return 0;

                } else if (result.get() == buttonTypeTwo) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }
        return 0;
    }
    @FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
        Object source = action.getSource();
        if(source == null){
            return;
        }
        if(source == listView){
            LibraryBook book = listView.getSelectionModel().getSelectedItem();
            //logger.debug(book.getBook() + " was clicked clicked");
        }
    }

    @FXML private void onButtonPress(ActionEvent action) throws IOException, SQLException, ParseException, com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException {
        Object source = action.getSource();
        if(source == Save){
            try{
                if(library.getId() != 0){
                    oldLibrary = new Library(library.getId(), library.getLibraryName(), library.getBooks(), library.getLastModified());
                }
                MasterController.getInstance().setCheck(0);
                if(validation.LibValid(LibName.getText()) && validation.quanValid(Quantity.getText())){
                    logger.info("Library Name: "+LibName.getText()+" Quantity: "+Quantity.getText());
                    library.setLibraryName(LibName.getText());
                    library.setBooks(libraryBooks);
                    try {
                        if(library.getId() == 0){ //new library added to the list
                            LTG.insertLibrary(library);
                            MasterController.getInstance().changeView(ViewType.LIBRARY_VIEW, library);
                        }else{ //update an existing library
                            LTG.updateLibrary(library,oldLibrary);
                            MasterController.getInstance().changeView(ViewType.LIBRARY_VIEW, library);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    logger.info("nope");
                    return;
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }else if(source == DelLib){
            //should go about deleting the currently viewed library
            MasterController.getInstance().setCheck(0);
            if(library.getId() == 0){
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("DelLib Error");
                alert.setContentText("New Libraries cannot be deleted");
                alert.showAndWait();
            }else{
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("DelLib Library?");
                alert.setContentText("Are you sure you want to delete " + library.toString());
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    LTG.deleteLibrary(library);
                    MasterController.getInstance().changeView(ViewType.LIBRARY_VIEW, library);
                } else {
                    logger.info("Deletion Cancled");
                }
            }
        }else if(source == Audit){
            MasterController.getInstance().changeView(ViewType.LIBRARY_AUDIT_TRAIL, library);
            }else if(source == AddBook){
			library.setLibraryName(LibName.getText());
			if(Books.getSelectionModel().getSelectedItem() == null){
				return;
			}
			LibraryBook checks = new LibraryBook(Integer.parseInt(Quantity.getText()),Books.getSelectionModel().getSelectedItem(), false);
			if(libraryBooks == null){
				libraryBooks.add(checks);
			}
			if(libraryBooks.contains(checks)){
				int newCheck = libraryBooks.indexOf(checks);
				libraryBooks.get(newCheck).setQuantity(Integer.parseInt(Quantity.getText()));
			}else{
				libraryBooks.add(checks);
			}
			if(library.getId() != 0){
				LTG.updateLibrary(library, oldLibrary);
			}else{
				LTG.insertLibrary(library);
			}
			Items.clear();
			libraryBooks.clear();
			MasterController.getInstance().setCheck(0);
			logger.info(library.getLastModified());
			MasterController.getInstance().changeView(ViewType.LIBRARY_VIEW, library);
        }else if(source == Report){
            pdfGen p = new pdfGen();
            try {
                library.setBooks(Items.sorted());
                p.createPDF(library);
            } catch (DocumentException e) {
            }
        }else if(source == DelBook){
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("DelLib Library?");
            alert.setContentText("Are you sure you want to delete " +listView.getSelectionModel().getSelectedItem());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                LTG.deleteLibraryBook(library, listView.getSelectionModel().getSelectedItem());
                MasterController.getInstance().changeView(ViewType.LIBRARY_VIEW, library);
            } else {
                logger.info("Deletion Cancled");
            }
        }
    }
}
