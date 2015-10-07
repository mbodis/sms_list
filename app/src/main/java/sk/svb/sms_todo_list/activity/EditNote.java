package sk.svb.sms_todo_list.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.db_models.MyServiceHelper;
import sk.svb.sms_todo_list.logic.Note;

public class EditNote extends AppCompatActivity {

    private static final String TAG = EditNote.class.getName();

    private Note mNote;
    private EditText titleET, messageET;
    private TextView dateTV;
    private boolean newNote = false;
    private boolean alreadyDeletedNote = false;

    private DatePickerDialog mDatePickerDialog;

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            selectedMonth ++;
            String m = (selectedMonth>9)? String.valueOf(selectedMonth) : String.valueOf("0"+(selectedMonth));
            String d = (selectedDay>9)? String.valueOf(selectedDay) : String.valueOf("0"+(selectedDay));

            // set selected date into textview
            dateTV.setText(new StringBuilder().append(selectedYear)
                            .append("-").append(m).append("-").append(d)
            );
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate " + getIntent().getExtras());

        if (getIntent() != null && getIntent().hasExtra("id")){
            Log.d(TAG, "edit");
            mNote = Note.findById(Note.class, getIntent().getLongExtra("id", -1));
        }else{
//            if (getIntent() != null && getIntent().hasExtra("new")){
                Log.d(TAG, "new");
                mNote = new Note();
                newNote = true;
//            }
        }

        if (getIntent() != null && getIntent().hasExtra("deleted_note")){
            alreadyDeletedNote = true;
        }

        if (mNote == null){
            Log.d(TAG, "exiting");
            finish();
            return;
        }

        setContentView(R.layout.activity_edit_note);

        if (newNote){
            setTitle(R.string.title_activity_new_note);
        }
        setupLayout();
        reloadContent();
    }

    private void setupLayout(){
        titleET = (EditText) findViewById(R.id.note_title);
        messageET = (EditText) findViewById(R.id.note_message);
        dateTV = (TextView) findViewById(R.id.date);

    }

    private void reloadContent(){
        if (mNote.getTitle() != null){
            titleET.setText(mNote.getTitle());
        }

        if (mNote.getMessage() != null) {
            messageET.setText(mNote.getMessage());
        }

        if (mNote.getDate() != null){
            dateTV.setText(mNote.getDate());
        }else{
            int m = (Calendar.getInstance().get(Calendar.MONTH) + 1);
            int d = (Calendar.getInstance().get(Calendar.DAY_OF_MONTH) );

            dateTV.setText(Calendar.getInstance().get(Calendar.YEAR)
                    + "-"
                    + (m>9 ? m : "0"+m)
                    + "-"
                    + (d>9 ? d : "0"+d ));
        }

        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = Calendar.getInstance().get(Calendar.YEAR);
                int month = Calendar.getInstance().get(Calendar.MONTH);
                int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

                if (dateTV.getText() != null && dateTV.getText().toString().contains("-")) {
                    String[] arr = dateTV.getText().toString().split("-");
                    year = Integer.parseInt(arr[0]);
                    month = Integer.parseInt(arr[1]) - 1;
                    day = Integer.parseInt(arr[2]);
                }

                mDatePickerDialog = new DatePickerDialog(EditNote.this, datePickerListener,
                        year, month, day);
                mDatePickerDialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mNote != null){

            // new note
            if (newNote){
                if (titleET.getText().toString().length() > 0 || messageET.getText().toString().length() > 0){
                    saveNote();
                    MyServiceHelper.refreshWidgets(getApplicationContext());
                }

            // edit note
            }else{
                saveNote();
                MyServiceHelper.refreshWidgets(getApplicationContext());
            }
        }

        super.onBackPressed();
    }

    private void saveNote(){
        mNote.setTitle(titleET.getText().toString());
        mNote.setMessage(messageET.getText().toString());
        mNote.setDate(dateTV.getText().toString());
        mNote.save();
        Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_note, menu);
        if (!alreadyDeletedNote){
            ((MenuItem)menu.findItem(R.id.action_return)).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch(item.getItemId()){
            case R.id.action_delete:
                if (mNote != null && mNote.getId() != null) {
                    if (alreadyDeletedNote){
                        mNote.delete();
                    }else{
                        mNote.setDeleted(true);
                        mNote.save();
                    }
                    mNote = null;
                    MyServiceHelper.refreshWidgets(getApplicationContext());
                    Toast.makeText(getApplicationContext(), getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;

            case R.id.action_return:
                if (mNote != null && mNote.getId() != null) {
                    mNote.setDeleted(false);
                    mNote.save();
                    MyServiceHelper.refreshWidgets(getApplicationContext());
                    Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
                }
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
