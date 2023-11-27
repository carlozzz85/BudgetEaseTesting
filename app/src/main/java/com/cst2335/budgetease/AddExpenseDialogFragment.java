package com.cst2335.budgetease;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AddExpenseDialogFragment extends DialogFragment {
    DatabaseHelper dbHelper = new DatabaseHelper(getContext());
    // Define interface for updating the expense list in ExpenseTrackerFragment
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
            throw new RuntimeException(context.toString()
                    + " must implement OnExpenseAddedListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_expense, null);

        final EditText editTextAmount = view.findViewById(R.id.editTextAmount);
        final EditText editTextCategory = view.findViewById(R.id.editTextCategory);
        final EditText editTextDate = view.findViewById(R.id.editTextDate);
        final EditText editTextNote = view.findViewById(R.id.editTextNote);

        builder.setView(view)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        double amount = Double.parseDouble(editTextAmount.getText().toString());
                        String category = editTextCategory.getText().toString();
                        String date = editTextDate.getText().toString();
                        String note = editTextNote.getText().toString();

                        Expense newExpense = new Expense(-1, amount, category, date, note);
                        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
                        dbHelper.addExpense(newExpense);

                        mListener.onExpenseAdded();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddExpenseDialogFragment.this.getDialog().cancel();
                    }
                });


        return builder.create();
    }

}

