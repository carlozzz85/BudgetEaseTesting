package com.cst2335.budgetease;

import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;

import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements AddExpenseDialogFragment.OnExpenseAddedListener {
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new ExpenseTrackerFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.navigation_expense_tracker);
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.navigation_expense_tracker) {
                fragment = new ExpenseTrackerFragment();
            } else if (id == R.id.navigation_budget_planner) {
                fragment = new BudgetPlannerFragment();
            } else if (id == R.id.nav_info) {
                showDeveloperInfo();
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_container, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void showDeveloperInfo() {
        new AlertDialog.Builder(this)
                .setTitle("Development Team Info")
                .setMessage("Names: \nCarlos Herrera\nXinghua Wang\nPanqi Teng\nQingyi Zhang")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onExpenseAdded() {
        ExpenseTrackerFragment fragment = (ExpenseTrackerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_expense_tracker);
        if (fragment != null) {
            fragment.updateListView();
        } else {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, new ExpenseTrackerFragment())
                    .commit();

        }

    }
}
