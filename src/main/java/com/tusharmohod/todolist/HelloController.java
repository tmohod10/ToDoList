package com.tusharmohod.todolist;

import com.tusharmohod.todolist.datamodel.ToDoData;
import com.tusharmohod.todolist.datamodel.ToDoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class HelloController {
    private List<ToDoItem> toDoItems;

    @FXML
    private ListView<ToDoItem> toDoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;

    @FXML
    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");

        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);

//        ToDoItem item1 = new ToDoItem("Mail Birthday Card",
//                "Buy a 30th birthday card for John",
//                LocalDate.of(2022, Month.APRIL, 25));
//        ToDoItem item2 = new ToDoItem("Doctor's appointment",
//                "See Doctor Smith at 123 Main Street",
//                LocalDate.of(2022, Month.MAY, 23));
//        ToDoItem item3 = new ToDoItem("Finish design proposal for the client",
//                "I promised Mike I'd email website mockups by Friday 22 April",
//                LocalDate.of(2022, Month.APRIL, 22));
//        ToDoItem item4 = new ToDoItem("Pickup Doud at the train station",
//                "Doug arriving on Jan 15",
//                LocalDate.of(2022, Month.JANUARY, 15));
//        ToDoItem item5 = new ToDoItem("Pickup dry cleaning",
//                "The clothes should be ready by wednesday",
//                LocalDate.of(2022, Month.FEBRUARY, 16));
//
//        toDoItems = new ArrayList<>();
//        toDoItems.add(item1);
//        toDoItems.add(item2);
//        toDoItems.add(item3);
//        toDoItems.add(item4);
//        toDoItems.add(item5);

//        ToDoData.getInstance().setToDoItems(toDoItems);

        toDoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem oldValue, ToDoItem newValue) {
                if(newValue != null) {
                    ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd MMM yyyy");
                    deadlineLabel.setText(df.format(item.getDeadLine()));
                }
            }
        });

//        toDoListView.getItems().setAll(ToDoData.getInstance().getToDoItems());
        toDoListView.setItems(ToDoData.getInstance().getToDoItems());
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE); // allow to select only one list at a time
        toDoListView.getSelectionModel().selectFirst();

        toDoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                ListCell<ToDoItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoItem item, boolean isEmpty) {
                        super.updateItem(item, isEmpty);
                        if(isEmpty) {
                            setText(null);
                        }
                        else {
                            setText(item.getShortDescription());
                            LocalDate localDate = item.getDeadLine();
                            if(localDate.equals(LocalDate.now())) {
                                setTextFill(Color.RED);
                            }
                            else if(localDate.equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.MAROON);
                            }
                            else if(localDate.isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.GRAY);
                            }
                        }
                    }
                };
                cell.emptyProperty().addListener(
                    (obs, wasEmpty, isNowEmpty) -> {
                        if(isNowEmpty) {
                            cell.setContextMenu(null);
                        }
                        else {
                            cell.setContextMenu(listContextMenu);
                        }
                    }
                );
                return cell;
            }
        });
    }

    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New To Do Item");
        dialog.setHeaderText("Create a new To Do Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("toDoItemDialog.fxml"));

        try{
            dialog.getDialogPane().setContent(fxmlLoader.load());
        }
        catch(IOException exception) {
            System.out.println("Couldn't load the dialog.");
            exception.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ToDoItem newToDoItem = controller.processResult();
            toDoListView.getSelectionModel().select(newToDoItem);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            if(keyEvent.getCode().equals(KeyCode.DELETE)) {
                deleteItem(selectedItem);
            }
        }
    }

    public void deleteItem(ToDoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("You are about to delete " + item.getShortDescription());
        alert.setContentText("Are your sure? Press OK to confirm or CANCEL to go back.");

        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            ToDoData.getInstance().deleteToDoItem(item);
        }
    }

    @FXML
    public void deleteByButton() {
        ToDoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null) {
            deleteItem(selectedItem);
        }
    }
}