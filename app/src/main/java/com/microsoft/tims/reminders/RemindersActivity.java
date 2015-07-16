package com.microsoft.tims.reminders;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
            mDbAdapter.createReminder("Figure out why Grunt and Gulp are tools, not digestive problems", false);
        }
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
