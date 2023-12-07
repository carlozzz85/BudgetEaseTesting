package com.cst2335.budgetease;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import java.util.Locale;

public class BudgetPlannerFragment extends Fragment {

    private EditText editTextBudget;
    private ProgressBar budgetProgressBar;
    private TextView textViewBudgetInfo;
    private DatabaseHelper dbHelper;
    private Button saveBudgetButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.budget_planner_fragment, container, false);

        initViews(view);
        dbHelper = DatabaseHelper.getInstance(getContext());

        saveBudgetButton.setOnClickListener(v -> saveBudget());
        updateBudgetDisplay();

        return view;
    }

    private void initViews(View view) {
        editTextBudget = view.findViewById(R.id.editTextBudget);
        budgetProgressBar = view.findViewById(R.id.budgetProgressBar);
        textViewBudgetInfo = view.findViewById(R.id.textViewBudgetInfo);
        saveBudgetButton = view.findViewById(R.id.saveBudgetButton);
    }

    private void saveBudget() {
        String budgetStr = editTextBudget.getText().toString();
        if (TextUtils.isEmpty(budgetStr)) {
            Toast.makeText(getContext(), "Please enter a budget", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double budget = Double.parseDouble(budgetStr);
            dbHelper.setBudget(budget);
            updateBudgetDisplay();
            Toast.makeText(getContext(), "Budget saved successfully", Toast.LENGTH_SHORT).show();
            editTextBudget.setText("");

            // Send a test notification
            Context context = getContext();
            if (context != null) {
                sendTestNotification(context, budget);
            } else {
                // Handle the case where context is null if needed
                Log.e("BudgetPlannerFragment", "Context is null when trying to send notification");
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid number format", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("MissingPermission")
    private void sendTestNotification(Context context, double budget) {
        String title = "Test Notification";
        String message = "New budget set: " + budget;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, getString(R.string.channel_id))
                .setSmallIcon(R.drawable.baseline_aod_24)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getUniqueNotificationId(), builder.build());
    }


    private void updateBudgetDisplay() {
        double budget = dbHelper.getBudget();
        double expenses = dbHelper.getTotalExpenses();
        double budgetUsage = budget != 0 ? (expenses / budget) * 100 : 0;

        budgetProgressBar.setProgress((int) budgetUsage);
        String budgetInfo = String.format(Locale.getDefault(), "Total Budget: %.2f\nExpenses: %.2f", budget, expenses);
        textViewBudgetInfo.setText(budgetInfo);

        checkAndSendNotification(budgetUsage);
    }

    private void checkAndSendNotification(double budgetUsage) {
        String title = "Budget Alert";
        String message = "";

        if (budgetUsage > 100) {
            message = "You have exceeded your budget limit!";
        } else if (budgetUsage >= 95) {
            message = "Warning: You are about to reach your budget limit!";
        } else if (budgetUsage >= 75) {
            message = "Alert: You have used 75% of your budget.";
        } else if (budgetUsage >= 50) {
            message = "Notice: You have used half of your budget.";
        }

        if (!message.isEmpty()) {
            sendNotification(title, message);
        }
    }

    @SuppressLint("MissingPermission")
    private void sendNotification(String title, String message) {
        if (!areNotificationsEnabled()) {
            return;
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), getString(R.string.channel_id))
                .setSmallIcon(R.drawable.ic_add)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
        notificationManager.notify(getUniqueNotificationId(), builder.build());
    }

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
    }

    private int getUniqueNotificationId() {

        return (int) (System.currentTimeMillis() % 10000);
    }
}
