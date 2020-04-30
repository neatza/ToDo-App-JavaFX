package model;

import java.time.LocalDate;

public class BaseToDo {
    private String content;
    private String description;
    private LocalDate deadline;

    public BaseToDo(String content, String description, LocalDate deadline) {
        this.content = content;
        this.description = description;
        this.deadline = deadline;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

//    @Override
//    public String toString() {
//        return this.content;
//    }
}
