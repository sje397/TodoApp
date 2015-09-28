package au.com.scottellis.todoapp;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import au.com.scottellis.todoapp.model.TodoItem;

public class TodoItemAdapter extends ArrayAdapter<TodoItem> {
    private static DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
    private DoneButtonListener listener;

    public interface DoneButtonListener {
        void doneButtonClicked(int position);
    }

    // View lookup cache
    private static class ViewHolder {
        TextView text;
        ImageButton delete;
        TextView date;
        RelativeLayout topStrip;
    }

    public TodoItemAdapter(Context context, ArrayList<TodoItem> items, DoneButtonListener listener) {
        super(context, R.layout.item_todo, items);
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TodoItem item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_todo, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.itemText);
            viewHolder.date = (TextView) convertView.findViewById(R.id.dateText);
            viewHolder.delete = (ImageButton) convertView.findViewById(R.id.doneBtn);
            viewHolder.topStrip = (RelativeLayout) convertView.findViewById(R.id.topStrip);
            viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.doneButtonClicked((int)v.getTag());
                }
            });
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.text.setText(item.text);
        if(item.deadline != null) {
            viewHolder.date.setText(dateFormat.format(item.deadline));
        } else {
            viewHolder.date.setText("");
        }
        viewHolder.delete.setTag(position);

        //viewHolder.text.setBackgroundColor(colorForPriority(item.priority));
        viewHolder.topStrip.setBackground(ContextCompat.getDrawable(getContext(),
                backgroundForPriority(item.priority)));
        // Return the completed view to render on screen
        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

    private int backgroundForPriority(TodoItem.Priority priority) {
        switch(priority) {
            case IMPORTANT: return R.drawable.roundshape_red;
            case NOTIMPORTANT: return R.drawable.roundshape_green;
            default: return R.drawable.roundshape_yellow;
        }
    }
}