package com.cst2335.budgetease;



import static com.cst2335.budgetease.DatabaseHelper.KEY_EXPENSE_AMOUNT;
import static com.cst2335.budgetease.DatabaseHelper.KEY_EXPENSE_DATE;
import static com.cst2335.budgetease.DatabaseHelper.KEY_EXPENSE_ID;
import static com.cst2335.budgetease.DatabaseHelper.KEY_EXPENSE_NOTE;
import static com.cst2335.budgetease.DatabaseHelper.TABLE_EXPENSES;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExpenseTrackerFragment extends Fragment {

    private static final String KEY_EXPENSE_CATEGORY = "Transaction";
    private ExpenseAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_tracker, container, false);

        ListView listView = view.findViewById(R.id.expense_list_view);
        FloatingActionButton fabAddExpense = view.findViewById(R.id.fab_add_expense);
        ArrayList<Expense> expenses = new ArrayList<>();
        adapter = new ExpenseAdapter(getContext(), expenses);
        listView.setAdapter(adapter);
        FloatingActionButton fabClear = view.findViewById(R.id.fabClear);

        fabClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearExpenses();
            }
        });

        fabAddExpense.setOnClickListener(v -> openAddExpenseForm());
        fetchExpensesAndUpdateListView();
        return view;
    }

    private void clearExpenses() {
        dbHelper.clearAllExpenses();
        updateListView();

    }

    void fetchExpensesAndUpdateListView() {
        ArrayList<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(DatabaseHelper.TABLE_EXPENSES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Expense expense = new Expense(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_EXPENSE_ID)),
                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.KEY_EXPENSE_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_EXPENSE_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_EXPENSE_DATE)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_EXPENSE_NOTE))
                );
                expenses.add(expense);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        adapter.clear();
        adapter.addAll(expenses);
        adapter.notifyDataSetChanged();
    }

    private void openAddExpenseForm() {
        AddExpenseDialogFragment dialog = new AddExpenseDialogFragment();
        dialog.show(getChildFragmentManager(), "AddExpenseDialogFragment");
    }

    public void onExpenseAdded() {
        updateListView();
    }

    public void updateListView() {
        fetchExpensesAndUpdateListView();
    }
    public void refreshExpenses() {
        fetchExpensesAndUpdateListView();
    }
}
