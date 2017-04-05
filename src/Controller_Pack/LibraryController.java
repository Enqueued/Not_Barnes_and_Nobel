package Controller_Pack;

import Gate_Pack.LibraryTableGateway;
import Model_Pack.Library;
import Model_Pack.LibraryBook;
import Model_Pack.ViewType;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.ParseException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by ultimaq on 4/4/17.
 */
public class LibraryController {
    private static Logger logger = LogManager.getLogger();
    private List<Library> libraryList;
    @FXML private ListView<Library> menuLibrary;
    @FXML private Button delBtn;
    private LibraryTableGateway libraryTableGateway;

    public LibraryController(){

    }

    public LibraryController(List<Library> libraryList){
        this.libraryList=libraryList;
    }

    @FXML
    private void onButtonPress(ActionEvent action) throws IOException, SQLException, ParseException{
		Object source = action.getSource();
		if(source == delBtn){
			logger.debug("Delete Library Btn clicked");
			Library selected = menuLibrary.getSelectionModel().getSelectedItem();
			if(selected == null){
				return;
			}else{
				libraryTableGateway = new LibraryTableGateway();
				for(LibraryBook lb: selected.getBooks()){
					libraryTableGateway.deleteLibraryBook(lb, selected.getId());
				}
				libraryTableGateway.deleteLibrary(selected.getId());
			}
		}
	}

	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		if(action.getClickCount() == 2){
			Library selected = menuLibrary.getSelectionModel().getSelectedItem();
			if(selected == null){
				return;
			}
			Object source = action.getSource();
			if(source == menuLibrary){
				try {
					MasterController.getInstance().changeView(ViewType.LIBRARY_DETAIL, selected);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void initialize(){
		ObservableList<Library> libraryItems = menuLibrary.getItems();
		for(Library lib : this.libraryList){
			libraryItems.add(lib);
		}
		libraryList.clear();
	}

}
    }
}
