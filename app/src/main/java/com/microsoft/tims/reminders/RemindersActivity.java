package com.microsoft.tims.reminders;

import android.app.AlertDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
//import android.view.View;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class RemindersActivity extends ActionBarActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);

        mDbAdapter = new RemindersDbAdapter(this);
        try
        {
            mDbAdapter.open();
        }
        catch(SQLException sqlEx)
        {
            System.out.println("RemindersActivity.onCreate threw SQLException " + sqlEx.toString());
        }

        if (savedInstanceState == null) {
            mDbAdapter.deleteAllReminders();

            mDbAdapter.createReminder("Finish reading Android Studio book", false);
            mDbAdapter.createReminder("Prepare for Android demo", true);
            mDbAdapter.createReminder("Figure out what a PM actually does during the day", false);
            mDbAdapter.createReminder("Implement amazing extension for Visual Studio", true);
            mDbAdapter.createReminder("Don't upset anyone important", true);
            mDbAdapter.createReminder("Learn to speak English without an accent", false);
            mDbAdapter.createReminder("Keep hydrated - drink more water", false);
            mDbAdapter.createReminder("Figure out why Grunt and Gulp are tools, not digestive " +
                    "problems", false);
        }

        Cursor cursor = mDbAdapter.fetchAllReminders();

        String[] from = new String[]{
                RemindersDbAdapter.COL_CONTENT
        };

        int[] to = new int[]{
                R.id.row_text
        };

        mCursorAdapter = new RemindersSimpleCursorAdapter(
                RemindersActivity.this,
                R.layout.reminders_row,
                cursor,
                from,
                to,
                0
        );

        mListView.setAdapter(mCursorAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    final int masterListPosition,
                                    long id)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersActivity.this);
                ListView modeListView = new ListView(RemindersActivity.this);
                String[] modes = new String[] { "Edit Reminder", "Delete Reminder" };
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<String>(RemindersActivity.this,
                        android.R.layout.simple_list_item_1, android.R.id.text1, modes);

                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);

                final Dialog dialog = builder.create();
                dialog.show();

                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        if (position == 0) {
                            int nId = getIdFromPosition(masterListPosition);
                            Reminder reminder = mDbAdapter.fetchReminderById(nId);
                            fireCustomDialog(reminder);

                        } else {
                            mDbAdapter.deleteReminderById(getIdFromPosition(masterListPosition));
                            mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                        }

                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private int getIdFromPosition(int nC) {
        return (int)mCursorAdapter.getItemId(nC);
    }

    private void fireCustomDialog(final Reminder reminder) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_custom);

        TextView titleView = (TextView)dialog.findViewById(R.id.custom_title);
        final EditText editCustom = (EditText)dialog.findViewById(R.id.custom_edit_reminder);
        final CheckBox checkBox = (CheckBox)dialog.findViewById(R.id.custom_check_box);
        LinearLayout rootLayout = (LinearLayout)dialog.findViewById(R.id.custom_root_layout);
        Button commitButton = (Button)dialog.findViewById(R.id.custom_button_commit);
        Button cancelButton = (Button)dialog.findViewById(R.id.custom_button_cancel);
        final boolean isEditOperation = (reminder != null);

        if (isEditOperation) {
            titleView.setText("Edit Reminder");
            checkBox.setChecked(reminder.getImportant() == 1);
            editCustom.setText(reminder.getContent());
            rootLayout.setBackgroundColor(getResources().getColor(R.color.blue));
        }

        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reminderText = editCustom.getText().toString();

                if (isEditOperation) {
                    Reminder reminderEdited = new Reminder(reminder.getId(),
                            reminderText, checkBox.isChecked() ? 1 : 0);
                    mDbAdapter.updateReminder(reminderEdited);
                } else {
                    mDbAdapter.createReminder(reminderText, checkBox.isChecked());
                }

                mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                dialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.action_new:
                Log.d(getLocalClassName(), "create new Reminder");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return true;
        }
    }
}
