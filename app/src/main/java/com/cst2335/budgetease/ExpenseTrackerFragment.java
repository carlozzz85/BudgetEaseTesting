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
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ExpenseTrackerFragment extends Fragment {

    private static final String KEY_EXPENSE_CATEGORY = "Transaction";
    private ListView listView;
    private ExpenseAdapter adapter;
    private FloatingActionButton fabAddExpense;
    private ArrayList<Expense> expenses;
    private DatabaseHelper dbHelper;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dbHelper = new DatabaseHelper(context);
    }
    String tableName = dbHelper.getExpensesTableName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense_tracker, container, false);

        listView = view.findViewById(R.id.expense_list_view);
        fabAddExpense = view.findViewById(R.id.fab_add_expense);
        expenses = new ArrayList<>();
        adapter = new ExpenseAdapter(getContext(), expenses);
        listView.setAdapter(adapter);

        fabAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddExpenseForm();
            }
        });
        fetchExpensesAndUpdateListView();
        return view;
    }

    private void fetchExpensesAndUpdateListView() {
        ArrayList<Expense> expenses = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();



        Cursor cursor = db.query(TABLE_EXPENSES, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") Expense expense = new Expense(
                        cursor.getInt(cursor.getColumnIndex(KEY_EXPENSE_ID)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_EXPENSE_AMOUNT)),
                        cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_CATEGORY)),
                        cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_NOTE))
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
        updateListView(); // Update the list when an expense is added
    }

    public void updateListView() {
        fetchExpensesAndUpdateListView(); // Assuming this method fetches data and updates the adapter
    }



}
