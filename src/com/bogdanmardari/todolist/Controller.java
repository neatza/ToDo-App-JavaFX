package com.bogdanmardari.todolist;

import com.sun.media.jfxmediaimpl.platform.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import model.BaseToDo;
import model.ToDoData;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    private List<BaseToDo> toDoItems;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private ListView<BaseToDo> todoListView;
    @FXML
    private Label deadLineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ToggleButton filterToggleButton;
    private FilteredList<BaseToDo> filteredList;
    private Predicate<BaseToDo> wantAllItems;
    private Predicate<BaseToDo> wantTodaysItems;

    public void initialize() {
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                BaseToDo item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });
        listContextMenu.getItems().addAll(deleteMenuItem);
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<BaseToDo>() {
            @Override
            public void changed(ObservableValue<? extends BaseToDo> observableValue, BaseToDo baseToDo, BaseToDo t1) {
                if (t1 != null) {
                    BaseToDo item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDescription());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadLineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });
        wantAllItems = new Predicate<BaseToDo>() {
            @Override
            public boolean test(BaseToDo baseToDo) {
                return true;
            }
        };
        wantTodaysItems = new Predicate<BaseToDo>() {
            @Override
            public boolean test(BaseToDo baseToDo) {
                return (baseToDo.getDeadline().equals(LocalDate.now()));
            }
        };
        filteredList = new FilteredList<BaseToDo>(ToDoData.getInstance().getTodoItems(), wantAllItems);

        SortedList<BaseToDo> sortedList = new SortedList<BaseToDo>(filteredList, new Comparator<BaseToDo>() {
            @Override
            public int compare(BaseToDo o1, BaseToDo o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });
        //todoListView.setItems(ToDoData.getInstance().getTodoItems());
        todoListView.setItems(sortedList);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();
        todoListView.setCellFactory(new Callback<ListView<BaseToDo>, ListCell<BaseToDo>>() {
            @Override
            public ListCell<BaseToDo> call(ListView<BaseToDo> baseToDoListView) {
                ListCell<BaseToDo> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(BaseToDo baseToDo, boolean b) {
                        super.updateItem(baseToDo, b);
                        if (b) {
                            setText(null);
                        } else {
                            setText(baseToDo.getContent());
                            if (baseToDo.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (baseToDo.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.PURPLE);
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
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle(" Add new ToDo Item");
        dialog.setHeaderText(" Use this window to add a new To do Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try {

            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println(" Couldn't load the dialog");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            BaseToDo newItem = controller.processResults();
            todoListView.getSelectionModel().select(newItem);

        }
    }

    @FXML
    public void handleClickListView() {
        BaseToDo item = todoListView.getSelectionModel().getSelectedItem();
        itemDetailsTextArea.setText(item.getDescription());
        deadLineLabel.setText(item.getDeadline().toString());

    }

    public void deleteItem(BaseToDo item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(" Delete to do Item !");
        alert.setHeaderText("Delete item" + item.getContent());
        alert.setContentText(" Are you sure? Press OK to confirm or CANCEL to cancel it!");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ToDoData.getInstance().deleteTodoItem(item);
        }
    }

    @FXML
    public void handleFilterButton() {
        BaseToDo selectedItem = todoListView.getSelectionModel().getSelectedItem();

        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if (filteredList.isEmpty()) {
                itemDetailsTextArea.clear();
                deadLineLabel.setText("");
            } else if (filteredList.contains(selectedItem)) {
                todoListView.getSelectionModel().select(selectedItem);
            } else {
                todoListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantAllItems);
        }
    }

    @FXML
    public void handleExit() {
    }
}
