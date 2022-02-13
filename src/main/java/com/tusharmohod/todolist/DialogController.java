package com.tusharmohod.todolist;

import com.tusharmohod.todolist.datamodel.ToDoData;
import com.tusharmohod.todolist.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadlinePicker;

    @FXML
    public void defaultDeadLine() {
        System.out.println(LocalDate.now());
        deadlinePicker.setValue(LocalDate.now());
    }

    @FXML
    public ToDoItem processResult() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadlineValue = deadlinePicker.getValue();

        ToDoItem newToDoItem = new ToDoItem(shortDescription, details, deadlineValue);
        ToDoData.getInstance().addToDoItem(newToDoItem);
        return newToDoItem;
    }
}
