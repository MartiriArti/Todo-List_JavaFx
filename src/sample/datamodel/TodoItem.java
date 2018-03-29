package sample.datamodel;

import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.Comparator;

public class TodoItem {

    private String shortDescription;
    private String details;
    private LocalDate deadline;
    private String priority;

    public TodoItem(String shortDescription, String details, LocalDate deadline, String priority) {
        this.shortDescription = shortDescription;
        this.details = details;
        this.deadline = deadline;
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDetails() {
        return details;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }


}
