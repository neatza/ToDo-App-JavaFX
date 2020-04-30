package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

public class ToDoData {

    private static ToDoData instance = new ToDoData();
    private static String filename = "TodoListItems.txt";

    private ObservableList<BaseToDo> todoItems;
    private DateTimeFormatter formatter;

    // creeam un constructor privat pt a creea unSinglePattern Class
    private ToDoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public static ToDoData getInstance() {
        return instance;
    }

    public ObservableList<BaseToDo> getTodoItems() {
        return todoItems;
    }

    public void addTodoItem(BaseToDo item) {
        todoItems.add(item);
    }


    public void loadTodoItems() throws IOException {
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(filename);
        BufferedReader bf = Files.newBufferedReader(path);
        String input;
        try {
            while ((input = bf.readLine()) != null) {
                String[] itemPieces = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dataString = itemPieces[2];
                LocalDate date = LocalDate.parse(dataString, formatter);
                BaseToDo todoItem = new BaseToDo(shortDescription, details, date);
                todoItems.add(todoItem);
            }
        } finally {
            if (bf != null) {
                bf.close();
            }
        }

    }

    public void storeTodoItems() throws IOException {
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);

        try {
            Iterator<BaseToDo> iterator = todoItems.iterator();
            while (iterator.hasNext()) {
                BaseToDo item = iterator.next();
                bw.write(String.format("%s\t%s\t%s",
                        item.getContent(),
                        item.getDescription(),
                        item.getDeadline().format(formatter)));
                bw.newLine();
            }


        } finally {
            if (bw != null) {
                bw.close();
            }
        }

    }

    public void deleteTodoItem(BaseToDo item) {
        todoItems.remove(item);
    }
}
