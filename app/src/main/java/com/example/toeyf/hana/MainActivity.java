package com.example.toeyf.hana;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private FirebaseAuth mAuth;

    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsPagerAdapter myTabsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsPagerAdapter);
        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setIcon(R.drawable.ic_people);
        myTabLayout.getTabAt(1).setIcon(R.drawable.ic_chat);
        myTabLayout.getTabAt(2).setIcon(R.drawable.ic_notifications);


        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("HANA");
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null)
        {
            LogOutUser();
        }
    }

    private void LogOutUser()
    {
        Intent startPageIntent = new Intent(MainActivity.this, start_page_activity.class);
        startPageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(startPageIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_buttom)
        {
            mAuth.signOut();

            LogOutUser();
        }

        if (item.getItemId() == R.id.main_account_setting_buttom)
        {
            Intent settingIntent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(settingIntent);
        }

        if (item.getItemId() == R.id.main_all_user_button)
        {
            Intent allUserIntent = new Intent(MainActivity.this, AllUsersActivity.class);
            startActivity(allUserIntent);
        }


        return true;
    }
}
