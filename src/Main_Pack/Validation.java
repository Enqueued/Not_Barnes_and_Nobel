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

    public Author authorValid(Author auth) throws SQLException{
        Author nonauth = auth;
        if(nonauth == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Author Error");
            alert.setContentText("Input Error: Please input an Author");
            alert.showAndWait();
            return null;
        }else{
            return nonauth;
        }
    }

    public boolean summValid(String sum){
        String summy = sum;
        // too long summary or was left empty
        if(summy.length() > 100 || summy.isEmpty()){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Summary Invalid");
            alert.setContentText("Input Error: Please input a valid Summary");
            return false;
        }else{
            return true;
        }
    }

    public boolean publishValid(String pub){
        String pubs = pub;
        if(pubs.length() > 100 || pubs.isEmpty()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Publisher Error");
            alert.setContentText("Input Error: Please input a valid publisher");
            return false;
        }else{
            return true;
        }
    }

    public boolean firstValid(String first){
        String fst = first;
        if(fst.length() > 100 || fst.isEmpty()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("First Name Error");
            alert.setContentText("Input Error: Please input a valid first name");
            alert.showAndWait();
            return false;
        }else{
            return true;
        }
    }

    public boolean lastValid(String last){
        String lst = last;
        if(lst.length() > 100 || lst.isEmpty()){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Last Name Error");
            alert.setContentText("Input Error: Please input a valid last name");
            alert.showAndWait();
            return false;
        }else{
            return true;
        }
    }
}
