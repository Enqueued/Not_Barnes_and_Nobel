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
            alert.showAndWait();
            return false;
        }else{
            return true;
        }
    }

    private boolean summaryValid(String text) {
        if (!text.isEmpty() && text.length() <= 100) {
            return true;
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Summary Error");
            alert.setContentText("Summary can not be more than 100 characters.");
            alert.showAndWait();
            return false;
        }
    }

    public boolean publishValid(String pub){
        String pubs = pub;
        if(pubs.length() > 100){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Publisher Error");
            alert.setContentText("Input Error: Please input a valid publisher");
            alert.showAndWait();
            return false;
        }else{
            return true;
        }
    }

    	public boolean publisherValid(String text) {
		if(text == null){
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Publisher Error");
			alert.setContentText("Publisher must not be empty.");
			alert.showAndWait();
			return false;
		}
		if(!text.isEmpty() && text.length()<=100 ){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Publisher Error");
			alert.setContentText("Publisher can not be more than 100 characters.");
			alert.showAndWait();
			return false;
		}
	}

    public boolean firstValid(String first){
        String fst = first;
        if(fst.length() > 100 || fst.isEmpty()){
            Alert alert = new Alert(AlertType.WARNING);
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
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Last Name Error");
            alert.setContentText("Input Error: Please input a valid last name");
            alert.showAndWait();
            return false;
        }else{
            return true;
        }
    }

    public boolean genValid(String gender){
        if(gender.toUpperCase().equals("M") || gender.toUpperCase().equals("U") || gender.toUpperCase().equals("G")){
            return true;
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Gender Error");
            alert.setContentText("Input Error: Please input a valid Gender");
            alert.showAndWait();
            return false;
        }
    }

    public boolean quanValid(String quantity){
        int check=Integer.valueOf(quantity);
        if(check >= 0 && check <=100){
            return true;
        }else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Quantity Error");
            alert.setContentText("Input Error: Please input a valid Quantity");
            alert.showAndWait();
            return false;
        }
    }

    public boolean LibValid(String libName){
        if(!libName.isEmpty() && libName.length()<=100){
            return true;
        }else{
            Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Library Name Error");
			alert.setContentText("Input Error: Please input a valid Library Name");
			alert.showAndWait();
			return false;
        }
    }

    public boolean dateValid(String date){
        if(date.matches("[0-2][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]") ){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Date Error");
			alert.setContentText("Please enter a valid date\n form of yyyy-MM-dd");
			alert.showAndWait();
			return false;
		}
    }

    public boolean dateValidation(String text) {
		if(text.matches("[0-2][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]") || text.isEmpty()){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Date Error");
			alert.setContentText("Please enter a valid date\n form of yyyy-MM-dd");
			alert.showAndWait();
			return false;
		}
	}


    public boolean authorValidation(String author) throws SQLException {
		String auth = author;
		if(auth.length()<=100){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Author Error");
			alert.setContentText("Please enter an Author name");
			alert.showAndWait();
			return false;
		}
	}


	public boolean titleValid(String text) {
		if(text.length()<=100 ){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Title Error");
			alert.setContentText("Title must not be empty \n and can not be more than 100 characters.");
			alert.showAndWait();
			return false;
		}
	}

	public boolean titleValidation(String title){
        if(title.length()<=100 && !title.isEmpty()){
			return true;
		}else{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Title Error");
			alert.setContentText("Input Error: Please input a valid Title");
			alert.showAndWait();
			return false;
		}
    }
}
