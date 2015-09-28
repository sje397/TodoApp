package au.com.scottellis.todoapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import au.com.scottellis.todoapp.model.TodoItem;

public class EditItemActivity extends ActionBarActivity
        implements AdapterView.OnItemSelectedListener,
            DatePickerDialog.OnDateSetListener,
            TimePickerDialog.OnTimeSetListener
{
    private static DateFormat dateFormat = SimpleDateFormat.getDateInstance();
    private static DateFormat timeFormat = SimpleDateFormat.getTimeInstance();
    private static DateFormat dateTimeFormat = SimpleDateFormat.getDateTimeInstance();

    private EditText editText;
    private EditText editDateText;
    private EditText editTimeText;
    private Button editDateBtn;
    private Button editTimeBtn;
    private CheckBox hasDeadlineChk;
    private Spinner prioSpn;

    private int position;
    private Date date;
    private TodoItem.Priority priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editText = (EditText)findViewById(R.id.editItemText);
        editDateText = (EditText)findViewById(R.id.editDateText);
        editTimeText = (EditText)findViewById(R.id.editTimeText);
        editDateBtn = (Button)findViewById(R.id.editDateBtn);
        editTimeBtn = (Button)findViewById(R.id.editTimeBtn);
        hasDeadlineChk = (CheckBox)findViewById(R.id.deadlineChk);
        prioSpn = (Spinner)findViewById(R.id.prioSpn);
        prioSpn.setOnItemSelectedListener(this);

        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add(getText(R.string.hi_prio_label).toString());
        spinnerItems.add(getText(R.string.med_prio_label).toString());
        spinnerItems.add(getText(R.string.lo_prio_label).toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioSpn.setAdapter(adapter);

        if(getIntent().getAction().equals("Edit")) {
            editText.setText(getIntent().getExtras().getString("itemText"));
            position = getIntent().getExtras().getInt("position");
            if(getIntent().getExtras().containsKey("deadline")) {
                date = new Date(getIntent().getExtras().getLong("deadline"));
                setDateTimeFields(true);
            } else {
                date = new Date();
                setDateTimeFields(false);
            }
            priority = TodoItem.Priority.valueOf(getIntent().getExtras().getString("priority"));
            setPriority();
        } else {
            editText.setHint("New Todo");
            date = new Date();
            setDateTimeFields(false);

            priority = TodoItem.Priority.NORMAL;
            setPriority();
        }
    }

    public void onDeadlineChkClicked(View view) {
        setDateTimeFields(hasDeadlineChk.isChecked());
    }

    public void onEditDate(View view) {
        DatePickerFragment f = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putLong("date", date.getTime());
        f.setArguments(args);
        f.show(getFragmentManager(), "datePicker");
    }

    public void onEditTime(View view) {
        TimePickerFragment f = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putLong("date", date.getTime());
        f.setArguments(args);
        f.show(getFragmentManager(), "timePicker");
    }

    public void onSubmit(View view) {
        Intent data = new Intent();

        data.putExtra("newText", editText.getText().toString());
        if(hasDeadlineChk.isChecked()) {
            data.putExtra("deadline", date.getTime());
        }
        data.putExtra("priority", priority.toString());
        data.putExtra("position", position);

        setResult(RESULT_OK, data);
        finish();
    }

    public void onCancel(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void setDateTimeFields(final boolean enableEdit) {
        editDateText.setText(dateFormat.format(date));
        editTimeText.setText(timeFormat.format(date));

        hasDeadlineChk.setChecked(enableEdit);
        editDateBtn.setEnabled(enableEdit);
        editTimeBtn.setEnabled(enableEdit);
        editDateText.setEnabled(enableEdit);
        editTimeText.setEnabled(enableEdit);
    }

    private void setPriority() {
        switch(priority) {
            case IMPORTANT: prioSpn.setSelection(0); break;
            case NORMAL: prioSpn.setSelection(1); break;
            case NOTIMPORTANT: prioSpn.setSelection(2); break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position) {
            case 0: priority = TodoItem.Priority.IMPORTANT; break;
            case 2: priority = TodoItem.Priority.NOTIMPORTANT; break;
            default: priority = TodoItem.Priority.NORMAL;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        date.setYear(year - 1900);
        date.setMonth(monthOfYear);
        date.setDate(dayOfMonth);
        setDateTimeFields(true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        date.setHours(hourOfDay);
        date.setMinutes(minute);
        setDateTimeFields(true);
    }

    public static class DatePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            final Date date = new Date(args.getLong("date"));

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(),
                    date.getYear() + 1900, date.getMonth(), date.getDay() + 1);
        }

    }

    public static class TimePickerFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Bundle args = getArguments();
            final Date date = new Date(args.getLong("date"));

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), (TimePickerDialog.OnTimeSetListener)getActivity(),
                    date.getHours(), date.getMinutes(), false);
        }
    }
}
