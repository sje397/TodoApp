package au.com.scottellis.todoapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

import java.util.Date;

import au.com.scottellis.todoapp.model.TodoItem;
import au.com.scottellis.todoapp.storage.Storage;
import au.com.scottellis.todoapp.storage.sqlite.SQLiteStorage;

public class MainActivity extends ActionBarActivity implements TodoItemAdapter.DoneButtonListener {
    private static final String SAVE_FILE = "todoItems.txt";
    private static final int EDIT_REQUEST_CODE = 1;
    private static final int ADD_REQUEST_CODE = 2;

    private Storage<TodoItem> storage;
    private ListView lvItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActiveAndroid.initialize(this);
        setContentView(R.layout.activity_main);

        //storage = new FileStorage<>(getFilesDir(), this, new TodoStringConverter());
        storage = new SQLiteStorage(this, this);
        lvItems = (ListView)findViewById(R.id.listView);
        lvItems.setAdapter(storage.getAdapter());
        try {
            storage.load();
        } catch(final Exception ex) {
            showError("Failed to load items", ex);
        }

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final TodoItem item = storage.getItem(position);
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.setAction("Edit");
                i.putExtra("position", position);
                i.putExtra("itemText", item.text);
                if (item.deadline != null) {
                    i.putExtra("deadline", item.deadline.getTime());
                }
                i.putExtra("priority", item.priority.toString());
                startActivityForResult(i, EDIT_REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == EDIT_REQUEST_CODE) {
                int position = data.getExtras().getInt("position");

                String newText = data.getExtras().getString("newText");
                TodoItem.Priority priority = TodoItem.Priority.valueOf(data.getExtras().getString("priority"));
                Date newDate = null;
                if(data.getExtras().containsKey("deadline")) {
                    newDate = new Date(data.getExtras().getLong("deadline"));
                }

                TodoItem item = storage.getItem(position);
                item.text = newText;
                item.priority = priority;
                item.deadline = newDate;

                try {
                    storage.updateItem(item, position);
                } catch(final Exception ex) {
                    showError("Error saving item", ex);
                }
            } else if (requestCode == ADD_REQUEST_CODE) {
                String newText = data.getExtras().getString("newText");
                TodoItem.Priority priority = TodoItem.Priority.valueOf(data.getExtras().getString("priority"));
                Date newDate = null;
                if(data.getExtras().containsKey("deadline")) {
                    newDate = new Date(data.getExtras().getLong("deadline"));
                }

                final TodoItem item = new TodoItem(newText, newDate, priority);
                try {
                    storage.addItem(item);
                } catch(final Exception ex) {
                    showError("Error adding item", ex);
                }
            }
        }
    }

    public void onClickAdd(View view) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.setAction("Add");
        startActivityForResult(i, ADD_REQUEST_CODE);
    }

    private void showError(final String message, final Throwable throwable) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();

        throwable.printStackTrace();
    }

    @Override
    public void doneButtonClicked(int position) {
        try {
            storage.deleteItem(position);
        } catch(final Exception ex) {
            showError("Error deleting item", ex);
        }
    }
}
