package com.example.dora.huawei;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import org.json.JSONObject;

import java.io.InputStream;
import android.app.SearchManager;
/*import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.android.hms.agent.common.handler.CheckUpdateHandler;
import com.huawei.android.hms.agent.common.handler.ConnectHandler;*/

public class MainActivity extends AppCompatActivity
    implements MainFragmentCallbacks, NavigationDrawerFragment.Callbacks {

    private DrawerLayout drawerLayout;
    //private FrameLayout navigationDrawerLayout;
    private ScrimInsetsFrameLayout navigationDrawerScrim;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final String TAG = "MyActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 /*       HMSAgent.connect(this, new ConnectHandler() {
            @Override
            public void onConnect(int rst) {
                Log.i(TAG, "HMS connected " + rst);
            }
        });

        HMSAgent.checkUpdate(this, new CheckUpdateHandler() {
            @Override
            public void onResult(int rst) {
                Log.i(TAG, "check app update rst:" + rst);
            }
        });*/

        // ActionBar / Toolbar / app bar
        Toolbar appbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(appbar);

        // Add fragments
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        AMainFragment aMainFragment = AMainFragment.newInstance("Hi from MainActivity.onCreate");
        ft.add(R.id.container, aMainFragment);

        // In a production app there would likely be a User object that represents the current user.
        String json = null;
        String username = null;
        String email = null;
        int avatar = R.drawable.avatar;

        try {
            InputStream is = getBaseContext().getAssets().open("test.json");
            Log.d("check inputstream","inputstream execute");
            int size = is.available();
            Log.d("check size",Integer.toString(size));
            byte[] buffer = new byte[size];
            is.read(buffer);
            Log.d("check buffer","read buffer success");
            is.close();
            json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            Log.d("check JSONObject",obj.toString());

            username= obj.getString("Name");
            email = obj.getString("Email");

        } catch (Exception exception){
            Log.d("exception","Exception happens");
        }

        NavigationDrawerFragment navigationDrawer = NavigationDrawerFragment
            .newInstance(username, email, avatar);
        ft.add(R.id.navigation_drawer, navigationDrawer);

        ft.commit();

        // NavigationDrawer
        drawerLayout = (DrawerLayout) findViewById(R.id.navigation_drawer_layout);
        //navigationDrawerLayout = (FrameLayout) findViewById(R.id.navigation_drawer);
        navigationDrawerScrim = (ScrimInsetsFrameLayout) findViewById(R.id.navigation_drawer_inset);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, appbar,
            R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // Force onPrepareOptionsMenu() to be called
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Force onPrepareOptionsMenu() to be called
                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        // Make the StatusBar transparent
        // TODO: Set the StatusBar background color via a style
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

     ;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;

    }

    /**
     * Allows you to modify the options menu on the ActionBar when the NavigationDrawer is toggled.
     * Specifically, hide option menu icons when the NavigationDrawer is open.
     *
     * @param menu The Activity's menu.
     * @return Return true to display the menu.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // ref: https://developer.android.com/training/implementing-navigation/nav-drawer.html#OpenClose
        boolean drawerOpen = drawerLayout.isDrawerOpen(navigationDrawerScrim);

        // Hide the Search options menu button when the navigation drawer is open, and show the
        // Search options menu button when the navigation drawer is closed.
        menu.findItem(R.id.action_search).setVisible(!drawerOpen);

        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Handle option menu item click events.
     *
     * ref: https://developer.android.com/reference/android/app/Activity.html#onOptionsItemSelected%28android.view.MenuItem%29
     *
     * @param item The options menu item that was clicked.
     * @return True if no further event processing should occur.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.huaweiIDLogin) {
            // TODO: Implement settings
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        if (id == R.id.huaweiProductInfo) {
            // TODO: Implement settings.
            Intent intent = new Intent(this, PayActivity.class);
            startActivity(intent);
        }

        if (id == R.id.action_search) {
            handleSearch();
            // Return true to inform Android that the event has been handled
            return true;
        }

        // If the following condition is true, then it indicates that the event has already been
        // handled and that no further processing should occur.
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    private void handleSearch() {

    }

    // MainFragmentCallbacks

    @Override
    public void displayBActivity() {
        // Avoid creating a single use variable named "intent" as we are not passing any values in
        // a bundle.
        startActivity(new Intent(MainActivity.this, BasicInfoFragment.class));
    }

    // NavigationDrawerFragment.Callbacks

    @Override
    public void setHomeScreen(int menuPosition) {
        // Get the current NavigationDrawerMenuItem
        NavigationDrawerMenuItem item = NavigationDrawerFragment.getNavigationDrawerMenuItem(menuPosition);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        Fragment contentFragment = AMainFragment.newInstance("Hi from MainActivity.onCreate");

        if (item.getTitle().equals("Education")) {
            contentFragment = EducationFragment.newInstance("Click: Education");
        } else if (item.getTitle().equals("Work Experience")) {
            contentFragment = WorkExpienrenceFragment.newInstance("Click: Work Experience");
        } else if (item.getTitle().equals("Project Showcase")) {
            contentFragment = ProjectShowCaseFragment.newInstance("Click: Project Showcase");
        }else if (item.getTitle().equals("Technical Skills")) {
            contentFragment = technicalSkillFragment.newInstance("Click: Technical Skill");
        }else if (item.getTitle().equals("Social")) {
            contentFragment = AMainFragment.newInstance("Click: Technical Skill");
        }


        ft.replace(R.id.container, contentFragment);
        ft.commit();

        // Close the NavigationDrawer
        drawerLayout.closeDrawer(navigationDrawerScrim);
    }
}
