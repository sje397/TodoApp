package au.com.scottellis.todoapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    private static final String SAVE_FILE = "todoItems.txt";
    private static final int EDIT_REQUEST_CODE = 1;

    private ArrayList<String> items;
    private ArrayAdapter<String> itemAdapter;
    private ListView lvItems;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readList();
        lvItems = (ListView)findViewById(R.id.listView);
        lvItems.setAdapter(itemAdapter);
        editText = (EditText)findViewById(R.id.editText);

        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                items.remove(position);
                itemAdapter.notifyDataSetChanged();
                saveList();
                return true;
            }
        });

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("position", position);
                i.putExtra("itemText", items.get(position));
                startActivityForResult(i, EDIT_REQUEST_CODE);
            }
        });
    }

    public void readList() {
        final File dir = getFilesDir();
        final File file = new File(dir, SAVE_FILE);
        try {
            items = new ArrayList<String>(FileUtils.readLines(file));
        } catch(final IOException ex) {
            items = new ArrayList<>();
        }
        itemAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }

    public void saveList() {
        final File dir = getFilesDir();
        final File file = new File(dir, SAVE_FILE);
        try {
            FileUtils.writeLines(file, items);
        } catch(final IOException ex) {
            Toast.makeText(getApplicationContext(), R.string.save_error, Toast.LENGTH_SHORT).show();
        }
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

    public void onClickAdd(View view) {
        String item = editText.getText().toString();
        itemAdapter.add(item);
        editText.setText("");
        saveList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_REQUEST_CODE) {
            String newText = data.getExtras().getString("newText");
            int position = data.getExtras().getInt("position");
            items.set(position, newText);
            itemAdapter.notifyDataSetChanged();
            saveList();
        }
    }
}
