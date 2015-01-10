package android.nized.org.orgnized;

import android.support.v4.app.FragmentManager;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {
    private String[] mNavTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavTitles = getResources().getStringArray(R.array.nav_titles_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavTitles));
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                null,
                R.string.drawer_open,
                R.string.drawer_close
        );

        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();

        mDrawerLayout.closeDrawer(mDrawerList);

        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // call ActionBarDrawerToggle.onOptionsItemSelected(), if it returns true
        // then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Fragment fragment = null;
            switch (position)
            {
                case 0:
                    fragment = new HomeFragment();
                    break;
                case 1:
                    fragment = new AttendanceFragment();
                    break;
                case 2:
                    fragment = new NotesFragment();
                    break;
                case 3:
                    fragment = new SurveyItemFragment();
                    break;
                case 4:
                    fragment = new ProfileFragment();
                    break;
                case 5:
                    Log.e("Feedback", "You still need to implement this");
                    break;
                case 6:
                    fragment = new AnnouncementsFragment();
                    break;
                default:
                    fragment = new HomeFragment();
                    break;
            }

            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_frame, fragment).commit();

                // update selected item and title, then close the drawer
                mDrawerList.setItemChecked(position, true);
                mDrawerList.setSelection(position);
                setTitle(mNavTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                // error in creating fragment
                Log.e("MainActivity", "Error in creating fragment");
            }
        }
    }
}
