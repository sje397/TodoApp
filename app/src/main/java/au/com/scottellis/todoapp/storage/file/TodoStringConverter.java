package au.com.scottellis.todoapp.storage.file;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import au.com.scottellis.todoapp.model.TodoItem;

/**
 * Convert TodoItem to string and back
 */
public class TodoStringConverter implements StringConverter<TodoItem> {
    private final static DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
    @Override
    public String toString(TodoItem item) throws Exception {
        JSONObject jobj = new JSONObject();
        jobj.put("text", item.text);
        final Date deadline = item.deadline;
        if(deadline != null) {
            jobj.put("deadline", dateFormat.format(deadline));
        }
        jobj.put("priority", item.priority.toString());
        return jobj.toString();
    }

    @Override
    public TodoItem fromString(String string) throws Exception {
        final JSONObject jobj = new JSONObject(string);

        final String text = jobj.getString("text");
        final Date deadline;
        if(jobj.has("deadline")) {
            deadline = dateFormat.parse(jobj.getString("deadline"));
        } else {
            deadline = null;
        }
        final TodoItem.Priority priority = TodoItem.Priority.valueOf(jobj.getString("priority"));

        return new TodoItem(text, deadline, priority);
    }
}
