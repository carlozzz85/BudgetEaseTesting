package com.cst2335.budgetease;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class BudgetPlannerFragment extends Fragment {

    private EditText editTextBudget;
    private ProgressBar budgetProgressBar;
    private TextView textViewBudgetInfo;
    private DatabaseHelper dbHelper;
    private Button saveBudgetButton;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_planner_fragment, container, false);

        editTextBudget = view.findViewById(R.id.editTextBudget);
        budgetProgressBar = view.findViewById(R.id.budgetProgressBar);
        textViewBudgetInfo = view.findViewById(R.id.textViewBudgetInfo);
        saveBudgetButton = view.findViewById(R.id.saveBudgetButton);
        dbHelper = new DatabaseHelper(getContext());

        saveBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double budget = Double.parseDouble(editTextBudget.getText().toString());
                    dbHelper.setBudget(budget);
                    updateBudgetDisplay();
                    Toast.makeText(getContext(), "Budget saved successfully", Toast.LENGTH_SHORT).show();
                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        updateBudgetDisplay();

        return view;
    }

    private void updateBudgetDisplay() {
        double budget = dbHelper.getBudget();
        double expenses = dbHelper.getTotalExpenses(); // Assume this method exists in dbHelper
        double budgetUsage = (expenses / budget) * 100;

        budgetProgressBar.setProgress((int) budgetUsage);
        textViewBudgetInfo.setText("Total Budget: " + budget + "\nExpenses: " + expenses);
    }
}
