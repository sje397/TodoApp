package au.com.scottellis.todoapp.storage;

import android.widget.ArrayAdapter;

import java.io.IOException;

/**
 * Interface to storage
 */
public interface Storage<T> {
    T getItem(int position);
    void addItem(T item) throws IOException;
    void updateItem(T item, int position) throws IOException;
    void deleteItem(final int position) throws IOException;
    ArrayAdapter<T> getAdapter();
    void load() throws IOException;
}
