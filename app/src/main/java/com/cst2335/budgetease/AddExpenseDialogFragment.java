package com.cst2335.budgetease;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddExpenseDialogFragment extends DialogFragment {

    private DatabaseHelper dbHelper;
    private EditText editTextAmount, editTextCategory, editTextDate, editTextNote;

    public interface OnExpenseAddedListener {
        void onExpenseAdded();
    }

    private OnExpenseAddedListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnExpenseAddedListener) {
            mListener = (OnExpenseAddedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnExpenseAddedListener");
        }
        dbHelper = DatabaseHelper.getInstance(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_expense, null);
        setupViews(view);

        builder.setView(view)
                .setPositiveButton("Save", (dialog, id) -> {
                    if (saveExpense()) {
                        mListener.onExpenseAdded();
                        Toast.makeText(getContext(), "Expense added successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }

    private void setupViews(View view) {
        editTextAmount = view.findViewById(R.id.editTextAmount);
        editTextCategory = view.findViewById(R.id.editTextCategory);
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextNote = view.findViewById(R.id.editTextNote);

        String currentDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(new Date());
        editTextDate.setText(currentDate);
        editTextDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", monthOfYear + 1, dayOfMonth, year);
            editTextDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private boolean saveExpense() {
        String amountStr = editTextAmount.getText().toString();
        String category = editTextCategory.getText().toString();
        String date = editTextDate.getText().toString();
        String note = editTextNote.getText().toString();

        if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(category) || TextUtils.isEmpty(date)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            Expense newExpense = new Expense(-1, amount, category, date, note);
            dbHelper.addExpense(newExpense);
            return true;
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
