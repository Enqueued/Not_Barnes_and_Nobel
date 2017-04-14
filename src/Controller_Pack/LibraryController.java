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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by ultimaq on 4/4/17.
 */
public class LibraryController {
    private static Logger logger = LogManager.getLogger();
    private List<Library> libraries;
    @FXML private ListView<Library> listView;
    @FXML private Button delBtn;
    private LibraryTableGateway libraryTableGateway;

    public LibraryController(List<Library> libraries){
        this.libraries =libraries;
    }

    //todo remove this funciton and place it into the detail view:
    /*@FXML
    private void onButtonPress(ActionEvent action) throws IOException, SQLException, ParseException{
		Object source = action.getSource();
		if(source == delBtn){
			MasterController.getInstance().setCheck(0);
			logger.info("Delete Library Btn clicked");
			Library selected = listView.getSelectionModel().getSelectedItem();
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
	}*/

	@FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
		if(action.getClickCount() == 2){
			Library selected = listView.getSelectionModel().getSelectedItem();
			if(selected == null){
				return;
			}
			Object source = action.getSource();
			if(source == listView){
				logger.info("Clicked on the " + selected.getLibraryName() + " Library!");
				try {
					MasterController.getInstance().changeView(ViewType.LIBRARY_DETAIL, selected);
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void initialize() throws SQLException{
		ObservableList<Library> items = listView.getItems();
		logger.info("libraries is null" + libraries.toString());
		for(Library lib : libraries){
			logger.info(" in the for loop: "+ lib.toString());
			items.add(lib);
		}
		libraries.clear();
	}

}
