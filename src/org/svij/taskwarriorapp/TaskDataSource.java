package org.svij.taskwarriorapp;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TaskDataSource {

	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private String[] allColumns = { SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_DESCRIPTION, SQLiteHelper.COLUMN_DUEDATE,
			SQLiteHelper.COLUMN_ENTRY, SQLiteHelper.COLUMN_STATUS };

	public TaskDataSource(Context context) {
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Task createTask(String task_description, String date, String status) {
		ContentValues values = new ContentValues();
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, task_description);
		values.put(SQLiteHelper.COLUMN_DUEDATE, date);
		values.put(SQLiteHelper.COLUMN_ENTRY, System.currentTimeMillis() / 1000);
		values.put(SQLiteHelper.COLUMN_STATUS, status);
		long insertId = database.insert(SQLiteHelper.TABLE_TASKS, null, values);
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Task newTask = cursorToTask(cursor);
		cursor.close();
		return newTask;
	}

	public void deleteTask(Task task) {
		long id = task.getId();
		System.out.println("Task deleted with id: " + id);
		database.delete(SQLiteHelper.TABLE_TASKS, SQLiteHelper.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Task> getAllTasks() {
		List<Task> tasks = new ArrayList<Task>();
		Cursor cursor = database.query(SQLiteHelper.TABLE_TASKS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Task task = cursorToTask(cursor);
			tasks.add(task);
			cursor.moveToNext();
		}

		cursor.close();
		return tasks;
	}

	private Task cursorToTask(Cursor cursor) {
		Task task = new Task();
		Log.i("Cursor.getColumnCount(0): ",
				Integer.toString(cursor.getColumnCount()));
		task.setId(cursor.getLong(0));
		task.setDescription(cursor.getString(1));
		task.setDuedate(cursor.getString(2));
		task.setEntry(cursor.getLong(3));
		task.setStatus(cursor.getString(4));
		return task;
	}
}
