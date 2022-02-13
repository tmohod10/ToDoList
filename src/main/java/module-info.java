module com.example.todolist {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.tusharmohod.todolist to javafx.fxml;
    exports com.tusharmohod.todolist;
}