package Model_Pack;

import Controller_Pack.MasterController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

/**
 * Created by ultimaq on 4/3/17.
 */
public class LibraryListView {
    private static Logger logger = LogManager.getLogger();
    private List<Library> libs;
    @FXML
    private ListView<Library> listView;

    public LibraryListView(List<Library>libs){
        //todo: check if this is what i need to do
        this.libs=libs;
    }

    @FXML private void onMouseClick(MouseEvent action) throws IOException, SQLException, ParseException{
        if(action.getClickCount()==2){
            Library lib = listView.getSelectionModel().getSelectedItem();
            if(lib == null){
                return;
            }
            Object source = action.getSource();
            if(source == listView){
                logger.info("clicked on " + lib);
                MasterController.getInstance().changeView(ViewType.LIBRARY_VIEW, lib);
            }
        }
    }

    public void initialize() throws SQLException{
        ObservableList<Library> items = listView.getItems();
        for(Library L : libs){
            items.add(L);
        }
        libs.clear();
    }
}
