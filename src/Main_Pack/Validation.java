package Main_Pack;

import Model_Pack.Author;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;

/**
 * Created by ultimaq on 5/8/17.
 */
public class Validation {
    public boolean webValid(String promptText){
        //website cannot be more that 100 characters
        if(promptText.length()<=100){
            return true;
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Web Address Length Error");
            alert.setContentText("Input Error: Web URL cannot be greater than 100 characters");
            alert.showAndWait();
            return false;
        }
    }

    public boolean authorValid(Author auth) throws SQLException{
        Author nonauth = auth;
        if(nonauth = null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Author Error");
            alert.setContentText("Input Error: Please input an Author");
            alert.showAndWait();
            return null;
        }else{
            return nonauth;
        }


    }
}
