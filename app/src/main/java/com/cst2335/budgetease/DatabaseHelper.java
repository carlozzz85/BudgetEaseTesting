package com.cst2335.budgetease;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "budgetEaseDatabase";
    private static final int DATABASE_VERSION = 2;

    // Table Names
    public static final String TABLE_EXPENSES = "expenses";

    // Expense Table Columns
    public static final String KEY_EXPENSE_ID = "id";
    public static final String KEY_EXPENSE_AMOUNT = "amount";
    public static final String KEY_EXPENSE_CATEGORY = "category";
    public static final String KEY_EXPENSE_DATE = "date";
    public static final String KEY_EXPENSE_NOTE = "note";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public String getExpensesTableName() {
        return TABLE_EXPENSES;
    }

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the expenses table
        String CREATE_EXPENSES_TABLE = "CREATE TABLE expenses (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "amount DOUBLE," +
                "category TEXT," +
                "date TEXT," +
                "note TEXT" + ")";

        // SQL statement to create the budget table
        String CREATE_BUDGET_TABLE = "CREATE TABLE budget (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "budget_amount DOUBLE" + ")";

        // Execute the SQL statements
        db.execSQL(CREATE_EXPENSES_TABLE);
        db.execSQL(CREATE_BUDGET_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        db.execSQL("DROP TABLE IF EXISTS budget");
        onCreate(db);
    }

    public void addExpense(Expense expense) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EXPENSE_AMOUNT, expense.getAmount());
        values.put(KEY_EXPENSE_CATEGORY, expense.getCategory());
        values.put(KEY_EXPENSE_DATE, expense.getDate());
        values.put(KEY_EXPENSE_NOTE, expense.getNote());

        db.insertOrThrow(TABLE_EXPENSES, null, values);
        db.close();
    }
    public void setBudget(double budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("budget_amount", budget);

        // Assuming there's a primary key (e.g., id = 1)
        int id = 1; // The ID of the budget row you want to update
        db.update("budget", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    @SuppressLint("Range")
    public double getBudget() {
        SQLiteDatabase db = this.getReadableDatabase();
        double budget = 0;

        Cursor cursor = db.query("budget", new String[] {"budget_amount"}, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            budget = cursor.getDouble(cursor.getColumnIndex("budget_amount"));
            cursor.close();
        }

        db.close();
        return budget;
    }


    @SuppressLint("Range")
    public double getTotalExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        double totalExpenses = 0;

        Cursor cursor = db.rawQuery("SELECT SUM(amount) as Total FROM expenses", null);
        if (cursor != null && cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(cursor.getColumnIndex("Total"));
            cursor.close();
        }

        db.close();
        return totalExpenses;
    }
    public int updateExpense(Expense expense) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_EXPENSE_AMOUNT, expense.getAmount());
        values.put(KEY_EXPENSE_CATEGORY, expense.getCategory());
        values.put(KEY_EXPENSE_DATE, expense.getDate());
        values.put(KEY_EXPENSE_NOTE, expense.getNote());

        // updating row
        return db.update(TABLE_EXPENSES, values, KEY_EXPENSE_ID + " = ?",
                new String[]{String.valueOf(expense.getId())});
    }
    public void deleteExpense(int expenseId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, KEY_EXPENSE_ID + " = ?",
                new String[]{String.valueOf(expenseId)});
        db.close();
    }
    @SuppressLint("Range")
    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_EXPENSES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Expense expense = new Expense();
                expense.setId(cursor.getInt(cursor.getColumnIndex(KEY_EXPENSE_ID)));
                expense.setAmount(cursor.getDouble(cursor.getColumnIndex(KEY_EXPENSE_AMOUNT)));
                expense.setCategory(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_CATEGORY)));
                expense.setDate(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_DATE)));
                expense.setNote(cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_NOTE)));
                expenses.add(expense);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return expenses;
    }
    public Expense getExpense(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXPENSES, new String[]{KEY_EXPENSE_ID,
                        KEY_EXPENSE_AMOUNT, KEY_EXPENSE_CATEGORY, KEY_EXPENSE_DATE, KEY_EXPENSE_NOTE}, KEY_EXPENSE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        @SuppressLint("Range") Expense expense = new Expense(cursor.getInt(cursor.getColumnIndex(KEY_EXPENSE_ID)),
                cursor.getDouble(cursor.getColumnIndex(KEY_EXPENSE_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_CATEGORY)),
                cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_DATE)),
                cursor.getString(cursor.getColumnIndex(KEY_EXPENSE_NOTE)));

        cursor.close();
        return expense;
    }

    public void clearAllExpenses() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXPENSES, null, null);
        db.close();
    }

}

