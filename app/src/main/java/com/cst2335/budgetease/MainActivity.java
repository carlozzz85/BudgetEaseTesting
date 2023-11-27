package com.cst2335.budgetease;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements AddExpenseDialogFragment.OnExpenseAddedListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        Fragment fragment = null;
        int id = item.getItemId();
        if (id == R.id.navigation_expense_tracker) {
            fragment = new ExpenseTrackerFragment();
        } else if (id == R.id.navigation_budget_planner) {
            fragment = new BudgetPlannerFragment();
        }
        return loadFragment(fragment);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Load the default fragment
        loadFragment(new ExpenseTrackerFragment());
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onExpenseAdded() {
        ExpenseTrackerFragment fragment = (ExpenseTrackerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_expense_tracker);
        if (fragment != null) {
            fragment.updateListView();
        }
    }
}
