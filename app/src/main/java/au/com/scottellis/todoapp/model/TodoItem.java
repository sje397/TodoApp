package au.com.scottellis.todoapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Model for a todo item
 */
@Table(name = "items")
public class TodoItem extends Model {
    public enum Priority {
        NOTIMPORTANT,
        NORMAL,
        IMPORTANT
    }

    @Column(name = "itemText")
    public String text;

    @Column(name = "deadline")
    public Date deadline;

    @Column(name = "priority")
    public Priority priority;

    public TodoItem() {
    }

    public TodoItem(final String text, final Date deadline, final Priority priority) {
        this.text = text;
        this.deadline = deadline;
        this.priority = priority;
    }

    public String toString() {
        return text;
    }
}
