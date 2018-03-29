package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import sample.datamodel.TodoData;
import sample.datamodel.TodoItem;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

public class Controller {
    @FXML
    private TextField filterField = new TextField();
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private ListView<TodoItem> toDoListView;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    NotifyConstructor notifyConstructor = new NotifyConstructor();
    public void initialize() {
        // delete
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = toDoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        //edit
        MenuItem editMenuItem = new MenuItem("Edit");
        editMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCodeCombination.SHIFT_DOWN));
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = toDoListView.getSelectionModel().getSelectedItem();
                editItemDialog();
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem, editMenuItem);
        // change
        toDoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
            @Override
            public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
                if (newValue != null) {
                    TodoItem item = toDoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("d MMMM, yyyy");
                    deadlineLabel.setText(item.getDeadline().format(df));
                }
            }
        });
        //sort
        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(TodoData.getInstance().getTodoItems(), new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem o1, TodoItem o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });

        updateCell(sortedList);
        notifyConstructor.showNotification(TodoData.getInstance().getTodoItemsforNotify());

        itemDetailsTextArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                // this will run whenever text is changed
                toDoListView.getSelectionModel().getSelectedItem().setDetails(newValue);
            }
        });
    }

    public void updateCell(SortedList<TodoItem> sortedList) {
        toDoListView.setItems(sortedList);
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();

        toDoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<TodoItem>() {
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setId("empty");
                        } else {
                            setText(item.getShortDescription()); ////////////////
                            if (item.getPriority().equals("Low Priority")) {
                                if (LocalDate.now().isAfter(item.getDeadline())) {
                                    setId("LowDone");
                                } else {
                                    setId("Low");
                                }
                            } else if (item.getPriority().equals("Normal Priority")) {
                                if (LocalDate.now().isAfter(item.getDeadline())) {
                                    setId("NormalDone");
                                } else {
                                    setId("Normal");
                                }
                            } else if (item.getPriority().equals("High Priority")) {
                                if (LocalDate.now().isAfter(item.getDeadline())) {
                                    setId("HighDone");
                                } else {
                                    setId("High");
                                }
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty) {
                                cell.setContextMenu(null);
                            } else {
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;
            }

        });
    }

    @FXML
    public void sortByPriority() {
        SortedList<TodoItem> sortedListPrio = new SortedList<TodoItem>(TodoData.getInstance().getTodoItems(),
                Comparator.comparing(TodoItem::getPriority));
        updateCell(sortedListPrio);
    }

    @FXML
    public void sortByAlphabet() {
        SortedList<TodoItem> sortedListDesc = new SortedList<TodoItem>(TodoData.getInstance().getTodoItems(),
                Comparator.comparing(TodoItem::getShortDescription));
        updateCell(sortedListDesc);
    }

    @FXML
    public void sortByDate() {
        updateCell(new SortedList<TodoItem>(TodoData.getInstance().getTodoItems(),
                Comparator.comparing(TodoItem::getDeadline)));
    }

    @FXML
    public void textWatcher() {
        FilteredList<TodoItem> filteredData = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(), p -> true);
        filterField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                filteredData.setPredicate(new Predicate<TodoItem>() {
                    @Override
                    public boolean test(TodoItem item) {
                        if (newValue == null || newValue.isEmpty()) {
                            return true;
                        }
                        if (item.getShortDescription().toLowerCase().contains(newValue.toLowerCase())) {
                            return true;
                        }
                        return false;
                    }
                });
            }
        });
        toDoListView.setItems(new SortedList<TodoItem>(filteredData,
                Comparator.comparing(TodoItem::getDeadline)));
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new Todo item?");
        dialog.setHeaderText("Use this dialog to add header text.");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            TodoItem newItem = controller.processResults();
            toDoListView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    public void editItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Todo item?");
        dialog.setHeaderText("Use this dialog to edit header text.");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("editItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        EditDialogController controller = fxmlLoader.getController();
        controller.setFields(toDoListView.getSelectionModel().getSelectedItem());
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.getInstance().deleteTodoItem(toDoListView.getSelectionModel().getSelectedItem());
            controller.processResults();
            toDoListView.getSelectionModel().select(toDoListView.getSelectionModel().getSelectedItem());
        }
    }


    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete TodoItem?");
        alert.setHeaderText("You are trying to delete " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm and cancel to back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void aboutDialog() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("About To-do app");
        alert.setHeaderText("This app was created by Tony Darko student of 559M group");
        alert.setContentText("Simple app for desktop wrote in JavaFx for Mac, Windows and Linux");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && (result.get() == ButtonType.OK)) {
        }
    }

    @FXML
    public void exitMethod() {
        System.exit(0);
    }

    @FXML
    public void handleKeyPressed(KeyEvent event) {
        TodoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            if (event.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            } else if (event.getCode().equals(KeyCode.SHIFT) && event.getCode().equals(KeyCode.E)) {
                editItemDialog();
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                System.exit(0);
            }
        }
    }
}