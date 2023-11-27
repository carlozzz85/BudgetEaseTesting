package com.cst2335.budgetease;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import androidx.core.app.NotificationCompat;
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
                    editTextBudget.setText("");
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
        double expenses = dbHelper.getTotalExpenses();
        double budgetUsage = (expenses / budget) * 100;

        checkAndSendNotification(budgetUsage);

        budgetProgressBar.setProgress((int) budgetUsage);
        textViewBudgetInfo.setText("Total Budget: " + budget + "\nExpenses: " + expenses);
    }
    private void checkAndSendNotification(double budgetUsagePercentage) {
        SharedPreferences prefs = getActivity().getSharedPreferences("BudgetPrefs", Context.MODE_PRIVATE);
        boolean is50Notified = prefs.getBoolean("is50Notified", false);
        boolean is75Notified = prefs.getBoolean("is75Notified", false);
        boolean is90Notified = prefs.getBoolean("is90Notified", false);

        if (budgetUsagePercentage >= 50 && !is50Notified) {
            sendBudgetNotification("50% of your budget is used.");
            prefs.edit().putBoolean("is50Notified", true).apply();
        } else if (budgetUsagePercentage >= 75 && !is75Notified) {
            sendBudgetNotification("75% of your budget is used.");
            prefs.edit().putBoolean("is75Notified", true).apply();
        } else if (budgetUsagePercentage >= 90 && !is90Notified) {
            sendBudgetNotification("Careful! 90% of your budget is used.");
            prefs.edit().putBoolean("is90Notified", true).apply();
        }
    }

    private void sendBudgetNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "budget_channel_id";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Budget Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("BudgetEase Channel");
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getActivity(), NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Budget Alert")
                .setContentText(message);

        notificationManager.notify(1001, notificationBuilder.build());
    }
}
