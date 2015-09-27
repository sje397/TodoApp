package au.com.scottellis.todoapp.storage.file;

import android.content.Context;
import android.widget.ArrayAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import au.com.scottellis.todoapp.storage.Storage;

/**
 * Persist items in a file
 */
public class FileStorage<T> implements Storage<T> {
    private static final String SAVE_FILE = "todoItems.txt";

    private ArrayList<T> items;
    private ArrayAdapter<T> adapter;

    private File storageDir;
    private StringConverter<T> converter;

    public FileStorage(final File storageDir, final Context context, final StringConverter<T> converter) {
        this.storageDir = storageDir;
        this.converter = converter;
        items = new ArrayList<>();
        adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1,
                items);
    }

    @Override
    public T getItem(int position) {
        return items.get(position);
    }

    @Override
    public void addItem(final T item) throws IOException {
        items.add(item);
        saveAndNotify();
    }

    @Override
    public void updateItem(final T item, int position) throws IOException {
        items.set(position, item);
        saveAndNotify();
    }

    @Override
    public void deleteItem(final int position) throws IOException {
        items.remove(position);
        saveAndNotify();
    }

    @Override
    public ArrayAdapter<T> getAdapter() {
        return adapter;
    }

    @Override
    public void load() throws IOException {
        final File file = new File(storageDir, SAVE_FILE);
        if(file.exists()) {
            items.clear();

            final ArrayList<String> stringItems = new ArrayList<>(FileUtils.readLines(file));
            for (final String itemString : stringItems) {
                try {
                    final T item = converter.fromString(itemString);
                    items.add(item);
                } catch(final Exception ex) {
                    throw new IOException("Conversion error loading", ex);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    private void saveAndNotify() throws IOException {
        final File file = new File(storageDir, SAVE_FILE);
        final ArrayList<String> stringItems = new ArrayList<>();
        for(final T item: items) {
            try {
                stringItems.add(converter.toString(item));
            } catch(final Exception ex) {
                throw new IOException("Conversion error saving", ex);
            }
        }
        FileUtils.writeLines(file, stringItems);

        if(adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
