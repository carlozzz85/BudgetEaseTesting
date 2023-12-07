package com.cst2335.budgetease;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
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

    private boolean areNotificationsEnabled() {
        return NotificationManagerCompat.from(getContext()).areNotificationsEnabled();
    }

    private int getUniqueNotificationId() {

        return (int) (System.currentTimeMillis() % 10000);
    }
}
