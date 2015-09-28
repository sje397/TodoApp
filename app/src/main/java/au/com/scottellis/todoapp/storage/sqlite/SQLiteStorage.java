package au.com.scottellis.todoapp.storage.sqlite;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.activeandroid.query.Select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.scottellis.todoapp.TodoItemAdapter;
import au.com.scottellis.todoapp.model.TodoItem;
import au.com.scottellis.todoapp.storage.Storage;

/**
 * Implementation of SQLite storage
 */
public class SQLiteStorage implements Storage<TodoItem> {
    private ArrayList<TodoItem> items;
    private TodoItemAdapter adapter;

    public SQLiteStorage(final Context context, TodoItemAdapter.DoneButtonListener listener) {
        items = new ArrayList<>();
        this.adapter = new TodoItemAdapter(context, items, listener);
    }

    @Override
    public TodoItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public void addItem(TodoItem item) throws IOException {
        items.add(item);
        writeToSql(item);
        load();
    }

    @Override
    public void updateItem(TodoItem item, int position) throws IOException {
        items.set(position, item);
        updateToSql(item);
        load();
    }

    @Override
    public void deleteItem(int position) throws IOException {
        final TodoItem item = items.remove(position);
        deleteFromSql(item);
        load();
    }

    @Override
    public ArrayAdapter<TodoItem> getAdapter() {
        return adapter;
    }

    @Override
    public void load() throws IOException {
        final List<TodoItem> itemsList = new Select()
                .from(TodoItem.class)
                .orderBy("priority")
                .execute();
        items.clear();
        items.addAll(itemsList);
        adapter.notifyDataSetChanged();
    }

    private void writeToSql(final TodoItem item) {
        item.save();
    }

    private void updateToSql(final TodoItem item) {
        item.save();
    }

    private void deleteFromSql(final TodoItem item) {
        item.delete();
    }
}
