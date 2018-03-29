package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import sample.datamodel.TodoData;
import java.io.IOException;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
         primaryStage.getIcons().add(new Image("res/new-file.png"));
        primaryStage.setTitle("To-Do List");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.getScene().getStylesheets().add("css/styleByPriority.css");
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }


    @Override
    public void init() throws Exception {
        TodoData.getInstance().loadFromxml();
    }

    @Override
    public void stop() throws Exception {
        // TodoData.getInstance().storeTodoItems();
        TodoData.getInstance().saveToxml();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
