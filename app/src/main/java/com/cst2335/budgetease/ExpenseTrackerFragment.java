package com.cst2335.budgetease;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExpenseTrackerFragment extends Fragment {

    private ListView listView;
    private ExpenseAdapter adapter;
    private DatabaseHelper dbHelper;
    private FloatingActionButton fabAddExpense, fabClear;
    private ProgressBar expenseProgressBar;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_tracker, container, false);
        initViews(view);
        setupListView();
        setupFloatingActionButtons();
        expenseProgressBar = view.findViewById(R.id.expenseProgressBar);
        updateProgressBar();

        return view;
    }
    private void updateProgressBar() {

        double budget = dbHelper.getBudget();
        double expenses = dbHelper.getTotalExpenses();
        int progress = budget != 0 ? (int) ((expenses / budget) * 100) : 0;

        expenseProgressBar.setProgress(progress);
    }
    private void initViews(View view) {
        listView = view.findViewById(R.id.expense_list_view);
        fabAddExpense = view.findViewById(R.id.fab_add_expense);
        fabClear = view.findViewById(R.id.fabClear);
    }

    private void setupListView() {
        ArrayList<Expense> expenses = new ArrayList<>(dbHelper.getAllExpenses());
        adapter = new ExpenseAdapter(getContext(), expenses);
        listView.setAdapter(adapter);
    }

    private void setupFloatingActionButtons() {
        fabAddExpense.setOnClickListener(v -> openAddExpenseDialog());
        fabClear.setOnClickListener(v -> clearExpenses());
    }

    private void openAddExpenseDialog() {
        new AddExpenseDialogFragment().show(getChildFragmentManager(), "AddExpenseDialogFragment");
    }

    private void clearExpenses() {
        dbHelper.clearAllExpenses();
        adapter.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "All expenses cleared", Toast.LENGTH_SHORT).show();
    }

    public void refreshExpenses() {
        ArrayList<Expense> expenses = new ArrayList<>(dbHelper.getAllExpenses());
        adapter.clear();
        adapter.addAll(expenses);
        adapter.notifyDataSetChanged();
    }

    public void updateListView() {

        refreshExpenses();
    }


}
