package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.datamodel.TodoData;
import sample.datamodel.TodoItem;
import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsField;
    @FXML
    private DatePicker deadlinePicker;
    @FXML
    public ChoiceBox<String> choiceBox;

    LocalDate deadlineValue = null;

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

        TodoItem item = new TodoItem(shortDescription, details, deadlineValue, priority);
        TodoData.getInstance().addTodoItem(item);
        return item;
    }
}
