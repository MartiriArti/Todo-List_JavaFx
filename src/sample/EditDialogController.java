package sample;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import sample.datamodel.TodoData;
import sample.datamodel.TodoItem;

import java.time.LocalDate;

public class EditDialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsField;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    public ChoiceBox<String> choiceBox;
    TodoItem item;
    LocalDate deadlineValue = null;

    public void setFields(TodoItem item){
        shortDescriptionField.setText(item.getShortDescription());
        detailsField.setText(item.getDetails());
        deadlinePicker.setValue(item.getDeadline());
        choiceBox.setValue(item.getPriority());
    }

    public TodoItem processResults() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsField.getText().trim();
        String priority = choiceBox.getValue();

        if (deadlinePicker.getValue() == null) {
            deadlineValue = LocalDate.now().plusDays(1);
        } else {
            deadlineValue = deadlinePicker.getValue();
        }

        if (shortDescription.equals("")){
            shortDescription = "Task on: " + deadlineValue;
        }

        item = new TodoItem(shortDescription, details, deadlineValue, priority);
        TodoData.getInstance().addTodoItem(item);
        return item;
    }
}
