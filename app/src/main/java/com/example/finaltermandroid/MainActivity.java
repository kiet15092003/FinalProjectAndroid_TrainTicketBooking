package com.example.finaltermandroid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.finaltermandroid.activity.LoginActivity;
import com.example.finaltermandroid.fragment.HelpFragment;
import com.example.finaltermandroid.fragment.HomeFragment;
import com.example.finaltermandroid.fragment.NotificationFragment;
import com.example.finaltermandroid.fragment.ProfileFragment;
import com.example.finaltermandroid.fragment.SettingFragment;
import com.example.finaltermandroid.fragment.TicketFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    BottomNavigationView bottomNavigationView;
    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            //// User is already logged in
            // set navigation
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            drawerLayout = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                    R.string.close_nav);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
            NavigationView navigationView = findViewById(R.id.nav_view);
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
                navigationView.setCheckedItem(R.id.nav_home);
            }
            navigationView.setNavigationItemSelectedListener(this);

            // set bottom bar
            setNavBottomBar();

            // get info user
            String uid = currentUser.getUid();
            String email = currentUser.getEmail();
        } else {
            //// User is not logged in
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            replaceFragment(new HomeFragment());
        } else if (item.getItemId() == R.id.nav_ticket) {
            replaceFragment(new TicketFragment());
        } else if (item.getItemId() == R.id.nav_profile) {
            replaceFragment(new ProfileFragment());
        } else if (item.getItemId() == R.id.nav_setting) {
            replaceFragment(new SettingFragment());
        } else if (item.getItemId() == R.id.nav_support) {
            replaceFragment(new HelpFragment());
        } else if (item.getItemId() == R.id.nav_notification) {
            replaceFragment(new NotificationFragment());
        } else if (item.getItemId() == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            return true;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
    private void setNavBottomBar(){
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new HomeFragment());
        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_home) {
                replaceFragment(new HomeFragment());
            } if (item.getItemId() == R.id.bottom_ticket) {
                replaceFragment(new TicketFragment());
            } if (item.getItemId() == R.id.bottom_help) {
                replaceFragment(new HelpFragment());
            } if (item.getItemId() == R.id.bottom_profile) {
                replaceFragment(new ProfileFragment());
            }
            return true;
        });
    }
}
