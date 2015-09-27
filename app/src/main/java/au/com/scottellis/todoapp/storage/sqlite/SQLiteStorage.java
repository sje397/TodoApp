package au.com.scottellis.todoapp.storage.sqlite;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.activeandroid.query.Select;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.scottellis.todoapp.model.TodoItem;
import au.com.scottellis.todoapp.storage.Storage;

/**
 * Implementation of SQLite storage
 */
public class SQLiteStorage implements Storage<TodoItem> {
    private ArrayList<TodoItem> items;
    private ArrayAdapter<TodoItem> adapter;

    public SQLiteStorage(final Context context) {
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1,
                items);
    }

    @Override
    public TodoItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public void addItem(TodoItem item) throws IOException {
        items.add(item);
        adapter.notifyDataSetChanged();
        writeToSql(item);
    }

    @Override
    public void updateItem(TodoItem item, int position) throws IOException {
        items.set(position, item);
        adapter.notifyDataSetChanged();
        updateToSql(item);
    }

    @Override
    public void deleteItem(int position) throws IOException {
        final TodoItem item = items.remove(position);
        adapter.notifyDataSetChanged();
        deleteFromSql(item);
    }

    @Override
    public ArrayAdapter<TodoItem> getAdapter() {
        return adapter;
    }

    @Override
    public void load() throws IOException {
        final List<TodoItem> itemsList = new Select()
                .from(TodoItem.class)
                .execute();
        items.addAll(itemsList);
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
