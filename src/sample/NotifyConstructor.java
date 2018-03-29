package sample;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import sample.datamodel.TodoItem;

import java.util.Iterator;

public class NotifyConstructor {

    public void showNotification(ObservableList<TodoItem> list) {
        Iterator<TodoItem> iter = list.iterator();
        String descs = "Tasks: ";
        if (list.size() > 1) {
            while (iter.hasNext()) {
                TodoItem item = iter.next();
                descs = descs + "\n --" + item.getShortDescription();
            }
            make(descs);
        } else if (list.size() == 1) {
            make("Task: " + list.get(0).getShortDescription());
        }
    }

    public void make(String desc) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Time to do your task!");
        alert.setHeaderText(desc);
        alert.setContentText("Wants to be a done!");
        alert.showAndWait();
    }
}

