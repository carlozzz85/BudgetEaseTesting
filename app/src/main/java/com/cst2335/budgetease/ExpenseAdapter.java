package com.cst2335.budgetease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpenseAdapter extends ArrayAdapter<Expense> {

    public ExpenseAdapter(Context context, ArrayList<Expense> expenses) {
        super(context, 0, expenses);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Expense expense = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_expense, parent, false);
        }

        TextView tvAmount = convertView.findViewById(R.id.tvAmount);
        TextView tvCategory = convertView.findViewById(R.id.tvCategory);
        // Set text for TextViews using expense data
        tvAmount.setText(String.valueOf(expense.getAmount()));
        tvCategory.setText(expense.getCategory());

        return convertView;
    }
}
