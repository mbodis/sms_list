package sk.svb.sms_todo_list.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import sk.svb.sms_todo_list.R;
import sk.svb.sms_todo_list.fragment.NavigationDrawerFragment;
import sk.svb.sms_todo_list.fragment.NotesFragment;
import sk.svb.sms_todo_list.fragment.TrashBinFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG = MainActivity.class.getName();
    public int page = PAGE_0_NOTES;

    public static final int PAGE_0_NOTES = 0;
    public static final int PAGE_1_TRASH_BIN = 1;
    public static final int PAGE_2_SETTINGS = 2;

    public Toolbar mToolbar;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the menu_trash_bin content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        page = position;
        switch (position) {
            case PAGE_0_NOTES:
                fragmentManager.beginTransaction().replace(R.id.container, NotesFragment.newInstance()).commit();
                break;
            case PAGE_1_TRASH_BIN:
                fragmentManager.beginTransaction().replace(R.id.container, TrashBinFragment.newInstance()).commit();
                break;
            case PAGE_2_SETTINGS:
                Intent mIntent = new Intent(this, SettingsActivity.class);
                startActivity(mIntent);
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case (PAGE_0_NOTES + 1):
                mTitle = getString(R.string.title_section1);
                break;
            case (PAGE_1_TRASH_BIN + 1):
                mTitle = getString(R.string.title_section2);
                break;
            case (PAGE_2_SETTINGS + 1):
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

        //Set the custom toolbar
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
        return super.onOptionsItemSelected(item);
    }


}
