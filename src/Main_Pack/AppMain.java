package Main_Pack;

import Controller_Pack.MasterController;
import Controller_Pack.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * CS ASSIGNMENT
 * Created by George Wilborn
 * Yes i know that i didnt turn in the previous homeworks.
 * Life has been kind of hectic at the moment.
 * Still that is no excuse.
 * Sorry about that...(o3o)/
 * */
public class AppMain extends Application{
	
	public static BorderPane rootPane;

	/**
	 * Super Blank
	 */
	public AppMain(){
	}

	/**
	 *
	 * @param args
	 */
	public static void main(String [] args){
		launch(args);
	}

	/**
	 *
	 * @param stage
	 * @throws Exception
	 */
	@Override
	public void start(Stage stage) throws Exception{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML_Pack/menu.fxml"));
		MenuController controller = new MenuController();
		loader.setController(controller);
		Parent view = loader.load();
		MasterController.getInstance().setRootPane((BorderPane) view);
		Scene scene = new Scene(view);
		stage.setScene(scene);
		stage.setTitle("Not Barnes and Noble");
		stage.show();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Override
	public void init() throws Exception {
		super.init();
		MasterController.getInstance();
	}

	/**
	 *
	 * @throws Exception
	 */
	@Override
	public void stop() throws Exception {
		super.stop();
		//close gateway
		MasterController.getInstance().close();
	}
}